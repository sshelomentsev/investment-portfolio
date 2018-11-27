package com.sshelomentsev.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

public class User extends AbstractUser {

    private UserAuthProvider authProvider;

    public User(UserAuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Override
    protected void doIsPermitted(String s, Handler<AsyncResult<Boolean>> handler) {
        authProvider.hasPermission(s);
        handler.handle(Future.succeededFuture(true));
    }

    @Override
    public JsonObject principal() {
        JsonObject p = new JsonObject();
        //p.put("email", "aaa");
        return p;
    }

    @Override
    public void setAuthProvider(AuthProvider authProvider) {
        if (authProvider instanceof UserAuthProvider) {
            this.authProvider = (UserAuthProvider) authProvider;
        } else {
            throw new IllegalArgumentException("Not a provider");
        }
    }

}
