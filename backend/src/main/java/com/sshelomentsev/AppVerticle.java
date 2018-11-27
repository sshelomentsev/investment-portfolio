package com.sshelomentsev;

import com.sshelomentsev.rest.RestService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;


public class AppVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        System.out.println("START");
        JsonObject dbConfig = new JsonObject();
        dbConfig.put("host", "127.0.0.1");
        dbConfig.put("port", 8529);
        dbConfig.put("user", "investment");
        dbConfig.put("password", "investment");
        dbConfig.put("name", "investment");

        config().put("db", dbConfig);

        deployRestService();
    }

    private Future<Void> deployRestService() {
        Future<String> future = Future.future();
        vertx.deployVerticle(RestService.class.getName(), new DeploymentOptions().setConfig(config()), future.completer());
        return future.map(r -> null);
    }

}
