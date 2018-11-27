package com.sshelomentsev.auth;

import com.arangodb.util.MapBuilder;
import com.sshelomentsev.arangodb.Database;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.reactivex.core.Vertx;

import java.util.Map;

public class UserAuthProvider implements AuthProvider {

    private final Vertx vertx;
    private final Database db;

    public UserAuthProvider(Vertx vertx, Database db) {
        this.vertx = vertx;
        this.db = db;
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<io.vertx.ext.auth.User>> handler) {
        vertx.executeBlocking(future -> {
            final String query = "for u in user filter u.email == @email and u.password == @password return u";
            Map<String, Object> bindVars = new MapBuilder()
                    .put("email", authInfo.getString("username"))
                    .put("password", authInfo.getString("password"))
                    .get();
            db.query(query, bindVars, event -> {
                if (event.succeeded() && 1 == event.result().size()) {
                    future.complete(true);
                } else {
                    future.fail("Not auth");
                }
            });
        }, event -> {
            System.out.println("auth");
            if (event.succeeded()) {
                handler.handle(Future.succeededFuture(new User(this)));
            } else {
                handler.handle(Future.failedFuture("No"));
            }
        });
    }

    public void hasPermission(String permission) {

    }
}
