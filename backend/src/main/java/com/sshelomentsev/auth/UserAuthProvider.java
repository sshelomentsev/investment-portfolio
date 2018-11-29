package com.sshelomentsev.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
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
            final String query = "for u in user filter u.email == @email return u";
            Map<String, Object> bindVars = new MapBuilder()
                    .put("email", authInfo.getString("username"))
                    .get();
            db.query(query, bindVars, event -> {
                if (event.succeeded() && 1 == event.result().size()) {
                    String hash = event.result().getJsonObject(0).getString("password");
                    BCrypt.Result result = BCrypt.verifyer().verify(authInfo.getString("password").toCharArray(), hash);
                    if (result.verified) {
                        System.out.println(event.result().getJsonObject(0).encodePrettily());
                        future.complete(event.result().getJsonObject(0));
                    } else {
                        future.fail("{'res': 'user in not correct}");
                    }
                } else {
                    future.fail("{'res': 'Not auth'}");
                }
            });
        }, event -> {
            if (event.succeeded()) {
                System.out.println(event.result().getClass().getName());
                handler.handle(Future.succeededFuture(new User(this, (JsonObject) event.result())));
            } else {
                handler.handle(Future.failedFuture("No"));
            }
        });
    }

    public void hasPermission(String permission) {

    }
}
