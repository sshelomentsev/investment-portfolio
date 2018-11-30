package com.sshelomentsev.service;

import com.sshelomentsev.model.UserProfile;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 * A service that responsible for user operations
 */
public interface UserService {

    /**
     * Create a new user
     * @param userProfile
     * @param resultHandler
     * @return
     */
    UserService createUser(UserProfile userProfile, Handler<AsyncResult<JsonObject>> resultHandler);

}