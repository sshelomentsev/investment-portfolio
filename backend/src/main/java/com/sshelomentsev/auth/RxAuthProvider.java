package com.sshelomentsev.auth;

import io.vertx.reactivex.ext.auth.AuthProvider;

public class RxAuthProvider extends AuthProvider {

    public RxAuthProvider(io.vertx.ext.auth.AuthProvider delegate) {
        super(delegate);
    }

}
