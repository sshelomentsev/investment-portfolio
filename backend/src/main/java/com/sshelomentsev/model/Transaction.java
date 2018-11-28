package com.sshelomentsev.model;

public class Transaction {

    private String _id;
    private String _key;
    private String _rev;
    private String user;
    private String currency;
    private Double amount;
    private Operation operation;
    private long timestamp;

    public Transaction() {
        this.timestamp = System.currentTimeMillis();
    }

    public Transaction(String user, String currency, Double amount, Operation operation) {
        this();
        this.user = user;
        this.currency = currency;
        this.amount = amount;
        this.operation = operation;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_key() {
        return _key;
    }

    public void set_key(String _key) {
        this._key = _key;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
