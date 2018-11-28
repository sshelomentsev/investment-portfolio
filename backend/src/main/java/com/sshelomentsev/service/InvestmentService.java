package com.sshelomentsev.service;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public interface InvestmentService {

    InvestmentService buyCoins(String buyer, String currency, Double amount, Handler<AsyncResult<JsonObject>> resultHandler);

    InvestmentService sellCoins(String seller, String currency, Double amount, Handler<AsyncResult<JsonObject>> resultHandler);

    InvestmentService getTransactionsHistory(String user, Handler<AsyncResult<JsonArray>> resultHandler);

    InvestmentService getInvestmentPortfolio(String user, Handler<AsyncResult<JsonArray>> resultHandler);

}
