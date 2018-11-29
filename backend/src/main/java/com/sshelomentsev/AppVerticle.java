package com.sshelomentsev;

import com.sshelomentsev.rest.RestService;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.reactivex.config.ConfigRetriever;
import io.vertx.reactivex.core.AbstractVerticle;


public class AppVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
        configRetriever.getConfig(event -> {
            if (event.succeeded()) {
                DeploymentOptions options = new DeploymentOptions();
                options.setConfig(event.result());
                deployRestService(options);
            }
        });
    }

    private Future<Void> deployRestService(DeploymentOptions options) {
        Future<String> future = Future.future();
        vertx.deployVerticle(RestService.class.getName(), options, future.completer());
        return future.map(r -> null);
    }

}
