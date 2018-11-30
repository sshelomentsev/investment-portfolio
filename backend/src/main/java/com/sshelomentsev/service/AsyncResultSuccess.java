package com.sshelomentsev.service;

import io.vertx.core.AsyncResult;

public class AsyncResultSuccess<T> implements AsyncResult {

    private T value;

    public AsyncResultSuccess(T value) {
        this.value = value;
    }

    @Override
    public T result() {
        return value;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean succeeded() {
        return true;
    }

    @Override
    public boolean failed() {
        return false;
    }
}
