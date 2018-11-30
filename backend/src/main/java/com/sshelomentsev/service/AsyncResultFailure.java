package com.sshelomentsev.service;

import io.vertx.core.AsyncResult;

public class AsyncResultFailure implements AsyncResult {

    private final String errorMessage;

    public AsyncResultFailure(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public Object result() {
        return null;
    }

    @Override
    public Throwable cause() {
        return new IllegalStateException(errorMessage);
    }

    @Override
    public boolean succeeded() {
        return false;
    }

    @Override
    public boolean failed() {
        return false;
    }

}
