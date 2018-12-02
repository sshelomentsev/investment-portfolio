package com.sshelomentsev.model;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;

public class AsyncResultFailure<T> implements AsyncResult<T> {

    private final String errorMessage;

    public AsyncResultFailure(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public T result() {
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

    public JsonObject errorResult() {
        return new JsonObject().put("success", false).put("error", errorMessage);
    }

}
