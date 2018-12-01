package com.sshelomentsev.model;

public class StakingCoin {

    private String currencyCode;
    private String currencyName;
    private Double rate;
    private Double amountCrypto;
    private Double amountFiat;
    private Double hourChange;
    private Double dayChange;
    private Double weekChange;
    private String marketCap;

    public StakingCoin() {

    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getAmountCrypto() {
        return amountCrypto;
    }

    public void setAmountCrypto(Double amountCrypto) {
        this.amountCrypto = amountCrypto;
    }

    public Double getAmountFiat() {
        return amountFiat;
    }

    public void setAmountFiat(Double amountFiat) {
        this.amountFiat = amountFiat;
    }

    public Double getHourChange() {
        return hourChange;
    }

    public void setHourChange(Double hourChange) {
        this.hourChange = hourChange;
    }

    public Double getDayChange() {
        return dayChange;
    }

    public void setDayChange(Double dayChange) {
        this.dayChange = dayChange;
    }

    public Double getWeekChange() {
        return weekChange;
    }

    public void setWeekChange(Double weekChange) {
        this.weekChange = weekChange;
    }

    public String getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(String marketCap) {
        this.marketCap = marketCap;
    }
}
