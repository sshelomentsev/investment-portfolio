package com.sshelomentsev.service;

import com.sshelomentsev.model.UserProfile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public interface AuthService {

    AuthService createUser(UserProfile userProfile, Handler<AsyncResult<JsonObject>> resultHandler);

}
