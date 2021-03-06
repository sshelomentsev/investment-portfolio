package com.sshelomentsev.service.impl;

import com.arangodb.util.MapBuilder;
import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.model.UserProfile;
import com.sshelomentsev.model.AsyncResultFailure;
import com.sshelomentsev.model.AsyncResultSuccess;
import com.sshelomentsev.service.UserService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Map;

public class UserServiceImpl implements UserService {

    private final Vertx vertx;
    private final Database db;

    public UserServiceImpl(Vertx vertx, Database db) {
        this.vertx = vertx;
        this.db = db;
    }

    @Override
    public UserService authenticate(String username, String password, Handler<AsyncResult<JsonObject>> resultHandler) {
        vertx.executeBlocking(future -> {
            final String query = "for u in user filter u.email == @email return " +
                    "{_id: u._id, email: u.email, password: u.password, " +
                    "firstName: u.firstName, lastName: u.lastName, phoneNumber: u.phoneNumber}";
            Map<String, Object> bindVars = new MapBuilder()
                    .put("email", username)
                    .get();
            db.query(query, bindVars, event -> {
                if (event.succeeded() && 1 == event.result().size()) {
                    String hash = event.result().getJsonObject(0).getString("password");
                    if (BCrypt.checkpw(password, hash)) {
                        JsonObject ret = event.result().getJsonObject(0);
                        ret.remove("password");
                        JsonObject res = event.result().getJsonObject(0);
                        future.complete(res);
                    } else {
                        future.fail("User or password are not correct");
                    }
                } else {
                    future.fail("Server error");
                }
            });
        }, event -> {
            if (event.succeeded()) {
                resultHandler.handle(Future.succeededFuture((JsonObject) event.result()));
            } else {
                resultHandler.handle(Future.failedFuture(event.cause().getMessage()));
            }
        });

        return this;
    }

    @Override
    public UserService createUser(UserProfile userProfile, Handler<AsyncResult<JsonObject>> resultHandler) {
        db.query("for u in user filter u.email == @email return u",
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
                resultHandler.handle(new AsyncResultFailure<>("Server error"));
            }
        });

        return this;
    }

    @Override
    public UserService getUserProfile(String user, Handler<AsyncResult<JsonObject>> resultHandler) {
        db.query("for u in user filter u._id == @id return {_id: u._id, email: u.email, firstName: u.firstName, " +
                "lastName: u.lastName, phoneNumber: u.phoneNumber}", new MapBuilder().put("id", user).get(), event -> {
            if (event.succeeded()) {
                resultHandler.handle(new AsyncResultSuccess<>(event.result().getJsonObject(0)));
            } else {
                resultHandler.handle(new AsyncResultFailure<>("Unable to finish user request"));
            }
        });

        return this;
    }

}
