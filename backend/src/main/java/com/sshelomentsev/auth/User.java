package com.sshelomentsev.auth;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AbstractUser;
import io.vertx.ext.auth.AuthProvider;

public class User extends AbstractUser {

    private UserAuthProvider authProvider;
    private JsonObject principal;

    public User(UserAuthProvider authProvider, JsonObject principal) {
        this.authProvider = authProvider;
        this.principal = principal;
    }

    @Override
    protected void doIsPermitted(String s, Handler<AsyncResult<Boolean>> handler) {
        authProvider.hasPermission(s);
        handler.handle(Future.succeededFuture(true));
    }

    @Override
    public JsonObject principal() {
        return principal;
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
