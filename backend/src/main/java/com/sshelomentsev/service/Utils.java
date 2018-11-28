package com.sshelomentsev.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonArray;

public class Utils {

    public static AsyncResult<JsonArray> createAsyncResult(JsonArray ret) {
        return new AsyncResult<JsonArray>() {
            @Override
            public JsonArray result() {
                return ret;
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
        };
    }

}
