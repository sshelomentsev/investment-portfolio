package com.sshelomentsev.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

/**
 * A service that responsible to retrieve an information about current state of cryptocurrencies
 */
public interface StatisticsService {

    /**
     * Initialize statistics service
     * @param resultHandler
     * @return
     */
    StatisticsService initialize(Handler<AsyncResult<Void>> resultHandler);

    /**
     * Get snapshots for an available list of cryptocurrencies for the specified period
     * @param period
     *      period of time. Can be a day, a week, or a month
     * @param resultHandler
     * @return
     */
    StatisticsService getSnapshots(String period, Handler<AsyncResult<JsonArray>> resultHandler);

    /**
     * Get ticks for hour/day/week for an available list of cryptocurrencies
     * @param resultHandler
     * @return
     */
    StatisticsService getTicks(Handler<AsyncResult<JsonArray>> resultHandler);

}
