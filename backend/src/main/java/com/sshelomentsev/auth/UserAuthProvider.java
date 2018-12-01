package com.sshelomentsev.auth;

import com.arangodb.util.MapBuilder;
import com.sshelomentsev.arangodb.Database;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import io.vertx.ext.auth.AuthProvider;
import io.vertx.reactivex.core.Vertx;
import org.mindrot.jbcrypt.BCrypt;

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
            final String query = "for u in user filter u.email == @email return " +
                    "{_id: u._id, email: u.email, password: u.password, " +
                    "firstName: u.firstName, lastName: u.lastName, phoneNumber: u.phoneNumber}";
            Map<String, Object> bindVars = new MapBuilder()
                    .put("email", authInfo.getString("username"))
                    .get();
            db.query(query, bindVars, event -> {
                if (event.succeeded() && 1 == event.result().size()) {
                    String hash = event.result().getJsonObject(0).getString("password");
                    if (BCrypt.checkpw((authInfo.getString("password")), hash)) {
                        JsonObject ret = event.result().getJsonObject(0);
                        ret.remove("password");
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
                handler.handle(Future.succeededFuture(new User(this, (JsonObject) event.result())));
            } else {
                handler.handle(Future.failedFuture("No"));
            }
        });
    }

    public void hasPermission(String permission) {

    }
}
