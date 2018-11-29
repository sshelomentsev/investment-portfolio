package com.sshelomentsev.service.impl;

import com.arangodb.util.MapBuilder;
import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.model.Operation;
import com.sshelomentsev.model.Transaction;
import com.sshelomentsev.service.InvestmentService;
import com.sshelomentsev.service.StatisticsService;
import com.sshelomentsev.service.Utils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InvestmentServiceImpl implements InvestmentService {

    private static final String TRANSACTION_COLLECTION_NAME = "transaction";

    private final Vertx vertx;
    private final Database db;
    private final StatisticsService statisticsService;

    private List<String> currencies = new ArrayList<>();

    public InvestmentServiceImpl(Vertx vertx, Database db, StatisticsService statisticsService) {
        this.vertx = vertx;
        this.db = db;
        this.statisticsService = statisticsService;
    }

    @Override
    public InvestmentService initialize(Handler<AsyncResult<Void>> resultHandler) {
        db.query("for c in currency return {code: c.code}", event -> {
            if (event.succeeded()) {
                currencies = event.result()
                        .stream().map(s -> ((JsonObject) s).getString("code"))
                        .collect(Collectors.toList());
                resultHandler.handle(null);
            }
        });
        return this;
    }

    @Override
    public InvestmentService buyCoins(String buyer, String currency, Double amount, Handler<AsyncResult<JsonObject>> resultHandler) {
        final Transaction transaction = new Transaction(buyer, currency, amount, Operation.BUY);
        processTransaction(transaction, resultHandler);
        return this;
    }

    @Override
    public InvestmentService sellCoins(String seller, String currency, Double amount, Handler<AsyncResult<JsonObject>> resultHandler) {
        final Transaction transaction = new Transaction(seller, currency, amount, Operation.SELL);
        processTransaction(transaction, resultHandler);
        return this;
    }

    private void processTransaction(Transaction transaction, Handler<AsyncResult<JsonObject>> resultHandler) {
        if (currencies.contains(transaction.getCurrency())) {
            db.collection(TRANSACTION_COLLECTION_NAME).insert(JsonObject.mapFrom(transaction), resultHandler);
        } else {
            resultHandler.handle(Utils.createFailureResult2("Specified currency does not exist"));
        }
    }

    @Override
    public InvestmentService getTransactionsHistory(String user, Handler<AsyncResult<JsonArray>> resultHandler) {
        db.query("for t in transaction filter t.user == @user sort t.timestamp desc return t",
                new MapBuilder().put("user", user).get(), resultHandler);
        return this;
    }

    @Override
    public InvestmentService getInvestmentPortfolio(String user, Handler<AsyncResult<JsonArray>> resultHandler) {
        statisticsService.getTicks(res -> {
            if (res.succeeded()) {
                db.query("for t in transaction filter t.user == @user return t", new MapBuilder().put("user", user).get(), event -> {
                    if (event.succeeded()) {
                        Map<String, Double> map = new HashMap<>();
                        for (int i = 0; i < event.result().size(); i++) {
                            Transaction transaction = event.result().getJsonObject(i).mapTo(Transaction.class);

                            Double currentAmount = map.getOrDefault(transaction.getCurrency(), 0.0);
                            if (Operation.BUY.equals(transaction.getOperation())) {
                                currentAmount += transaction.getAmount();
                            } else {
                                currentAmount -= transaction.getAmount();
                            }
                            map.put(transaction.getCurrency(), currentAmount);
                        }

                        JsonArray ret = new JsonArray();
                        for (int i = 0; i < res.result().size(); i++) {
                            final JsonObject tick = res.result().getJsonObject(i);
                            System.out.println(tick.encodePrettily());
                            final String currency = tick.getString("currency");
                            final Double rate = tick.getDouble("rate");
                            System.out.println(currency + " -> " + rate);

                            final Double current = map.getOrDefault(currency, 0.0);
                            if (current > 0) {
                                JsonObject rs = new JsonObject();
                                rs.put("currency", currency);
                                rs.put("rate", rate);
                                rs.put("amountNative", current);
                                rs.put("amountUsd", current * rate);
                                ret.add(rs);
                            }
                        }

                        resultHandler.handle(Utils.createAsyncResult(ret));
                    }
                });
            }
        });

        return this;
    }




}
