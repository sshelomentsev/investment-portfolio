package com.sshelomentsev.service.impl;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.model.AsyncResultFailure;
import com.sshelomentsev.model.AsyncResultSuccess;
import com.sshelomentsev.service.StatisticsService;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsServiceImpl implements StatisticsService {

    private final Database db;
    private final WebClient client;

    private List<String> currencies = new ArrayList<>();

    public StatisticsServiceImpl(Vertx vertx, Database db) {
        this.db = db;
        client = WebClient.create(vertx);
    }

    @Override
    public StatisticsService initialize(Handler<AsyncResult<Void>> resultHandler) {
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
    public StatisticsService getSnapshots(String period, Handler<AsyncResult<JsonArray>> resultHandler) {
        getSnapshotsForCurrencies(currencies, period, resultHandler);
        return this;
    }

    @Override
    public StatisticsService getTicks(Handler<AsyncResult<JsonArray>> resultHandler) {
        List<Observable<JsonObject>> observables = currencies.stream()
                .map(currency -> client.getAbs(getPriceUrl(currency))
                        .rxSend()
                        .toObservable()
                        .map(resp -> resp.bodyAsJsonObject().put("currency", currency)))
                .collect(Collectors.toList());

        Observable.zip(observables, jsons -> {
            JsonArray ret = new JsonArray();
            for (Object json : jsons) {
                ret.add(json);
            }
            return ret;
        }).subscribe(s -> resultHandler.handle(new AsyncResultSuccess<>(s)));

        return this;
    }

    @Override
    public StatisticsService getRate(String currency, Handler<AsyncResult<Double>> resultHandler) {
        client.getAbs(getPriceUrl(currency)).rxSend().toObservable()
                .map(resp -> resp.bodyAsJsonObject().getDouble("rate"))
                .subscribe(s -> resultHandler.handle(new AsyncResultSuccess<>(s)));

        return this;
    }

    @Override
    public StatisticsService getMarketCaps(Handler<AsyncResult<JsonObject>> resultHandler) {
        List<Observable<JsonObject>> observables = currencies.stream()
                .map(currency -> client.getAbs(getMarketCapUrl(currency))
                        .rxSend()
                        .toObservable()
                        .map(resp -> new JsonObject().put(currency, resp.bodyAsString().replaceAll("\"", ""))))
                .collect(Collectors.toList());

        Observable.zip(observables, jsons -> {
            JsonObject ret = new JsonObject();
            for (Object json : jsons) {
                ret.mergeIn((JsonObject) json);
            }
            return ret;
        }).subscribe(s -> resultHandler.handle(new AsyncResultSuccess<>(s)));

        return this;
    }

    private void getSnapshotsForCurrencies(List<String> currencies, String period, Handler<AsyncResult<JsonArray>> resultHandler) {
        if ("day".equals(period) || "month".equals(period) || "week".equals(period)) {
            List<Observable<JsonObject>> observables = currencies.stream()
                    .map(currency -> client.getAbs(getSnapshotsUrl(currency, period))
                            .rxSend()
                            .toObservable()
                            .map(resp -> new JsonObject().put("currency", currency).put("values", sortShapshotItems(resp.bodyAsJsonArray()))))
                    .collect(Collectors.toList());

            Observable.zip(observables, jsons -> {
                JsonArray ret = new JsonArray();
                for (Object json : jsons) {
                    ret.add(json);
                }
                return ret;
            }).subscribe(res -> resultHandler.handle(new AsyncResultSuccess<>(res)));
        } else {
            resultHandler.handle(new AsyncResultFailure<>("Incorrect period"));
        }
    }

    private JsonArray sortShapshotItems(JsonArray arr) {
        return arr.stream().sorted(Comparator.comparingInt(a -> ((JsonArray) a).getInteger(0)))
                .collect(JsonArray::new, JsonArray::add, JsonArray::add);
    }

    private static String getPriceUrl(String currency) {
        return "https://api.cryptometr.io/api/v1/snapshots/ticker?from=" + currency + "&to=USD";
    }

    private static String getSnapshotsUrl(String currency, String period) {
        return "https://api.cryptometr.io/api/v1/snapshots/chart?from=" + currency + "&to=USD&period=" + period;
    }

    private static String getMarketCapUrl(String currency) {
        return "https://api.cryptometr.io/api/v1/metrics-data/market-cap?currency=" + currency;
    }

}
