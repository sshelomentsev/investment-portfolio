package com.sshelomentsev.util;

import com.sshelomentsev.AppVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.function.Consumer;

public class Runner {

    public static void runExample() {
        Consumer<Vertx> runner = vertx -> {
            try {
                vertx.deployVerticle(new AppVerticle());
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        Vertx vertx = Vertx.vertx(new VertxOptions());
        runner.accept(vertx);
    }

}
