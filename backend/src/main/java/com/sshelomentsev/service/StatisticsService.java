package com.sshelomentsev.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

public interface StatisticsService {

    StatisticsService initialize(Handler<AsyncResult<Void>> resultHandler);

    StatisticsService getSnapshots(String period, Handler<AsyncResult<JsonArray>> resultHandler);

    StatisticsService getSnapshots(String currency, String period, Handler<AsyncResult<JsonArray>> resultHandler);

    StatisticsService getTicks(Handler<AsyncResult<JsonArray>> resultHandler);

}
