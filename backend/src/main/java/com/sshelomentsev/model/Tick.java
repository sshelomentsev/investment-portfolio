package com.sshelomentsev.model;

public class Tick {

    private String currency;
    private Double rate;
    private Double rate_hour;
    private Double rate_day;
    private Double rate_week;
    private Double hour;
    private Double day;
    private Double week;

    public Tick() {

    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRate_hour() {
        return rate_hour;
    }

    public void setRate_hour(Double rate_hour) {
        this.rate_hour = rate_hour;
    }

    public Double getRate_day() {
        return rate_day;
    }

    public void setRate_day(Double rate_day) {
        this.rate_day = rate_day;
    }

    public Double getRate_week() {
        return rate_week;
    }

    public void setRate_week(Double rate_week) {
        this.rate_week = rate_week;
    }

    public Double getHour() {
        return hour;
    }

    public void setHour(Double hour) {
        this.hour = hour;
    }

    public Double getDay() {
        return day;
    }

    public void setDay(Double day) {
        this.day = day;
    }

    public Double getWeek() {
        return week;
    }

    public void setWeek(Double week) {
        this.week = week;
    }
}
