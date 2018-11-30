package com.sshelomentsev.service.impl;

import com.arangodb.util.MapBuilder;
import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.model.UserProfile;
import com.sshelomentsev.service.AsyncResultFailure;
import com.sshelomentsev.service.UserService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServiceImpl implements UserService {

    private final Database db;

    public AuthServiceImpl(Database db) {
        this.db = db;
    }

    @Override
    public UserService createUser(UserProfile userProfile, Handler<AsyncResult<JsonObject>> resultHandler) {
        db.query("for u in user filter user.email == @email return u",
                new MapBuilder().put("email", userProfile.getEmail()).get(), event -> {
            if (event.succeeded()) {
                if (0 == event.result().size()) {
                    String hashed = BCrypt.hashpw(userProfile.getPassword(), BCrypt.gensalt());
                    userProfile.setPassword(hashed);
                    db.collection("user").insert(JsonObject.mapFrom(userProfile), resultHandler);
                } else {
                    resultHandler.handle(new AsyncResultFailure("User with specified email is already exist"));
                }
            } else {
                event.cause().printStackTrace();
            }
        });

        return this;
    }

}
