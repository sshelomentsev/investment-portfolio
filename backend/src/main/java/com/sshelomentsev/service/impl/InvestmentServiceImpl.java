package com.sshelomentsev.service.impl;

import com.arangodb.model.TransactionOptions;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackBuilder;
import com.arangodb.velocypack.ValueType;
import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.model.Operation;
import com.sshelomentsev.model.StakingCoin;
import com.sshelomentsev.model.Tick;
import com.sshelomentsev.model.Transaction;
import com.sshelomentsev.model.AsyncResultFailure;
import com.sshelomentsev.model.AsyncResultSuccess;
import com.sshelomentsev.service.InvestmentService;
import com.sshelomentsev.service.StatisticsService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;


public class InvestmentServiceImpl implements InvestmentService {

    private static final String SELL_AQL_ACTION = "function(params) {\n"+
            "            const db = require(\"@arangodb\").db;\n" +
            "            const user = params['user'];" +
            "            const currency = params['currency'];\n" +
            "            const amount = params['amount'];\n" +
            "            const rate = params['rate'];\n" +
            "            let querySum = `for t in transaction filter t.user == \"${user}\" and t.currency == \"${currency}\" collect aggregate s = sum(t.amount) return s`\n"+
            "            let currAmount = db._query(querySum).toArray()[0]\n"+
            "            if (currAmount >= amount) {\n"+
            "                let amountToSave = -1 * amount;\n"+
            "                db.transaction.save({user: user, operation: 'SELL', rate: rate, timestamp: new Date().getTime(), currency: currency, amount: amountToSave})\n"+
            "                return {'success': true};\n"+
            "            }\n"+
            "            return {'success': false};\n"+
            "        }";

    private static final String TRANSACTION_COLLECTION_NAME = "transaction";

    private final Database db;
    private final StatisticsService statisticsService;

    private Map<String, String> currencies;

    public InvestmentServiceImpl(Database db, StatisticsService statisticsService) {
        this.db = db;
        this.statisticsService = statisticsService;
    }

    @Override
    public InvestmentService initialize(Handler<AsyncResult<Void>> resultHandler) {
        db.query("for c in currency return c", event -> {
            if (event.succeeded()) {
                currencies = new HashMap<>();
                for (int i = 0; i < event.result().size(); i++) {
                    JsonObject currency = event.result().getJsonObject(i);
                    currencies.put(currency.getString("code"), currency.getString("name"));
                }

                resultHandler.handle(null);
            }
        });
        return this;
    }

    @Override
    public InvestmentService buyCoins(String buyer, String currency, Double amount, Handler<AsyncResult<JsonObject>> resultHandler) {
        final Transaction transaction = new Transaction(buyer, currency, amount, Operation.BUY);
        if (currencies.containsKey(transaction.getCurrency())) {
            statisticsService.getRate(currency, rate -> {
                if (rate.succeeded()) {
                    transaction.setRate(rate.result());
                    db.collection(TRANSACTION_COLLECTION_NAME).insert(JsonObject.mapFrom(transaction), event -> {
                        if (event.succeeded()) {
                            resultHandler.handle(new AsyncResultSuccess<>(new JsonObject().put("success", true)));
                        } else {
                            resultHandler.handle(new AsyncResultSuccess<>(new JsonObject().put("success", false)));
                        }
                    });
                } else {
                    resultHandler.handle(new AsyncResultFailure<>("Unable to get rate for the specified currency"));
                }
            });
        } else {
            resultHandler.handle(new AsyncResultFailure<>("Specified currency does not exist or is not served"));
        }
        return this;
    }

    @Override
    public InvestmentService sellCoins(String seller, String currency, Double amount, Handler<AsyncResult<JsonObject>> resultHandler) {
        if (currencies.containsKey(currency)) {
            statisticsService.getRate(currency, rate -> {
                if (rate.succeeded()) {
                    final TransactionOptions options = new TransactionOptions().writeCollections(TRANSACTION_COLLECTION_NAME)
                            .params(new VPackBuilder().add(ValueType.OBJECT)
                                    .add("currency", currency)
                                    .add("user",seller)
                                    .add("amount", amount)
                                    .add("rate", rate.result())
                                    .close().slice());

                    db.transaction(SELL_AQL_ACTION, options, event -> {
                        if (event.succeeded()) {
                            resultHandler.handle(new AsyncResultSuccess<>(event.result()));
                        } else {
                            resultHandler.handle(new AsyncResultFailure<>(event.cause().getMessage()));
                        }
                    });
                } else {
                    resultHandler.handle(new AsyncResultFailure<>("Unable to get rate for the specified currency"));
                }
            });
        } else {
            resultHandler.handle(new AsyncResultFailure<>("Specified currency does not exist or is not served"));
        }
        return this;
    }

    @Override
    public InvestmentService getTransactionsHistory(String user, Handler<AsyncResult<JsonArray>> resultHandler) {
        db.query("for t in transaction filter t.user == @user sort t.timestamp desc return t",
                new MapBuilder().put("user", user).get(), resultHandler);
        return this;
    }

    @Override
    public InvestmentService getStackingCoins(String user, Handler<AsyncResult<JsonArray>> resultHandler) {
        statisticsService.getTicks(ticks -> {
            statisticsService.getMarketCaps(marketCaps -> {
                db.query("for t in transaction filter t.user == @user return t", new MapBuilder().put("user", user).get(), event -> {
                    if (event.succeeded()) {
                        Map<String, Double> map = new HashMap<>();
                        for (int i = 0; i < event.result().size(); i++) {
                            Transaction transaction = event.result().getJsonObject(i).mapTo(Transaction.class);

                            Double currentAmount = map.getOrDefault(transaction.getCurrency(), 0.0);
                            currentAmount += transaction.getAmount();
                            map.put(transaction.getCurrency(), currentAmount);
                        }

                        JsonArray stakingCoins = new JsonArray();
                        for (int i = 0; i < ticks.result().size(); i++) {
                            Tick tick = ticks.result().getJsonObject(i).mapTo(Tick.class);

                            StakingCoin stakingCoin = new StakingCoin();
                            stakingCoin.setCurrencyCode(tick.getCurrency());
                            stakingCoin.setCurrencyName(currencies.get(stakingCoin.getCurrencyCode()));
                            stakingCoin.setRate(tick.getRate());

                            final Double current = map.getOrDefault(stakingCoin.getCurrencyCode(), 0.0);
                            stakingCoin.setAmountCrypto(current);
                            stakingCoin.setAmountFiat(current * stakingCoin.getRate());

                            stakingCoin.setHourChange(tick.getHour());
                            stakingCoin.setDayChange(tick.getDay());
                            stakingCoin.setWeekChange(tick.getWeek());

                            stakingCoin.setMarketCap(marketCaps.result().getString(tick.getCurrency()));

                            stakingCoins.add(JsonObject.mapFrom(stakingCoin));
                        }

                        resultHandler.handle(new AsyncResultSuccess<>(stakingCoins));
                    }
                });
            });
        });

        return this;
    }


}
