package com.sshelomentsev.service;

public interface InvestmentService {

    boolean buyCoins(String buyer, String currency, Double amount);

    boolean sellCoins(String seller, String currency, Double amount);

}
