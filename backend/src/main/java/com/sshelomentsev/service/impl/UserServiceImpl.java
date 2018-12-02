package com.sshelomentsev.service.impl;

import com.arangodb.util.MapBuilder;
import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.model.UserProfile;
import com.sshelomentsev.model.AsyncResultFailure;
import com.sshelomentsev.model.AsyncResultSuccess;
import com.sshelomentsev.service.UserService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import org.mindrot.jbcrypt.BCrypt;

public class UserServiceImpl implements UserService {

    private final Database db;

    public UserServiceImpl(Database db) {
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
                    resultHandler.handle(new AsyncResultFailure<>("User with specified email is already exist"));
                }
            } else {
                resultHandler.handle(new AsyncResultFailure<>("Unable to finish user request"));
            }
        });

        return this;
    }

    @Override
    public UserService getUserProfile(String user, Handler<AsyncResult<JsonObject>> resultHandler) {
        db.query("for u in user filter u._id == @id return {email: u.email, firstName: u.firstName, " +
                "lastName: u.lastName, phoneNumber: u.phoneNumber}", new MapBuilder().put("id", user).get(), event -> {
            if (event.succeeded()) {
                resultHandler.handle(new AsyncResultSuccess<JsonObject>(event.result().getJsonObject(0)));
            } else {
                resultHandler.handle(new AsyncResultFailure<>("Unable to finish user request"));
            }
        });

        return this;
    }

}
