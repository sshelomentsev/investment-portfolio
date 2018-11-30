package com.sshelomentsev.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * A service that for investment operations
 */
public interface InvestmentService {

    /**
     * Initialize investment service
     * @param resultHandler
     * @return
     */
    InvestmentService initialize(Handler<AsyncResult<Void>> resultHandler);

    /**
     * Process buy coins
     * @param buyer
     *      A buyer's user id
     * @param currency
     *      Cryptocurrency code
     * @param amount
     *      An amount of coins to buy
     * @param resultHandler
     *      Async result handler
     * @return
     */
    InvestmentService buyCoins(String buyer, String currency, Double amount, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Process sell coints
     * @param seller
     *      A seller's used id
     * @param currency
     *      Cryptocurrency code
     * @param amount
     *      An amount of coints to sell
     * @param resultHandler
     *      Async result handler
     * @return
     */
    InvestmentService sellCoins(String seller, String currency, Double amount, Handler<AsyncResult<JsonObject>> resultHandler);

    /**
     * Get history of transactions for a user
     * @param user
     *      User id
     * @param resultHandler
     *      Async result handler
     * @return
     */
    InvestmentService getTransactionsHistory(String user, Handler<AsyncResult<JsonArray>> resultHandler);

    /**
     * Get stacking structure state
     * @param user
     *      User id
     * @param resultHandler
     *      Async result handler
     * @return
     */
    InvestmentService getStackingCoins(String user, Handler<AsyncResult<JsonArray>> resultHandler);

}
