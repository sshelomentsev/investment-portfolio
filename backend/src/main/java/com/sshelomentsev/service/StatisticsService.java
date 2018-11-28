package com.sshelomentsev.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

public interface StatisticsService {

    StatisticsService getTicks(Handler<AsyncResult<JsonArray>> resultHandler);

}
