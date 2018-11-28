package com.sshelomentsev.service.impl;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.service.StatisticsService;
import com.sshelomentsev.service.Utils;
import io.reactivex.Observable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

public class StatisticsServiceImpl implements StatisticsService {

    private final Vertx vertx;
    private final Database db;
    private final WebClient client;

    public StatisticsServiceImpl(Vertx vertx, Database db) {
        this.vertx = vertx;
        this.db = db;
        client = WebClient.create(vertx);
    }

    @Override
    public StatisticsService getTicks(Handler<AsyncResult<JsonArray>> resultHandler) {

        db.query("for c in currency return {code: c.code}", event -> {
            List<String> currencies = event.result()
                    .stream().map(s -> ((JsonObject) s).getString("code"))
                    .collect(Collectors.toList());

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
            }).subscribe(s -> resultHandler.handle(Utils.createAsyncResult(s)));
        });


        return this;
    }

    private static String getPriceUrl(String currency) {
        System.out.println("price url " + System.currentTimeMillis());
        return "https://api.cryptometr.io/api/v1/snapshots/ticker?from=" + currency + "&to=USD";
    }


}
