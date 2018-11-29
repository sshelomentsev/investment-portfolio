package com.sshelomentsev.service.impl;

import com.arangodb.util.MapBuilder;
import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.model.UserProfile;
import com.sshelomentsev.service.AuthService;
import com.sshelomentsev.service.Utils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServiceImpl implements AuthService {

    private final Vertx vertx;
    private final Database db;

    public AuthServiceImpl(Vertx vertx, Database db) {
        this.vertx = vertx;
        this.db = db;
    }

    @Override
    public AuthService createUser(UserProfile userProfile, Handler<AsyncResult<JsonObject>> resultHandler) {
        db.query("for u in user filter user.email == @email return u",
                new MapBuilder().put("email", userProfile.getEmail()).get(), event -> {
            if (0 == event.result().size()) {
                //String bcryptHashString = BCrypt.withDefaults().hashToString(12, userProfile.getPassword().toCharArray());
                String hashed = BCrypt.hashpw(userProfile.getPassword(), BCrypt.gensalt());
                userProfile.setPassword(hashed);
                db.collection("user").insert(JsonObject.mapFrom(userProfile), resultHandler);
            } else {
                resultHandler.handle(Utils.createFailureResult2("User already exist"));
            }
        });

        return this;
    }

}
