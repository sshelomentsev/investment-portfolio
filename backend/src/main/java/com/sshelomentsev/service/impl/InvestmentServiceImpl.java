package com.sshelomentsev.service.impl;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.service.InvestmentService;
import io.vertx.core.Vertx;

public class InvestmentServiceImpl implements InvestmentService {

    private final Vertx vertx;
    private final Database db;

    public InvestmentServiceImpl(Vertx vertx, Database db) {
        this.vertx = vertx;
        this.db = db;
    }

    @Override
    public boolean buyCoins(String buyer, String currency, Double amount) {
        return false;
    }

    @Override
    public boolean sellCoins(String seller, String currency, Double amount) {
        return false;
    }
}
