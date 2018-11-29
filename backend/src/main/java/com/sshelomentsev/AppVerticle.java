package com.sshelomentsev;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.rest.RestService;
import com.sshelomentsev.service.AuthService;
import com.sshelomentsev.service.InvestmentService;
import com.sshelomentsev.service.StatisticsService;
import com.sshelomentsev.service.impl.AuthServiceImpl;
import com.sshelomentsev.service.impl.InvestmentServiceImpl;
import com.sshelomentsev.service.impl.StatisticsServiceImpl;
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

                new Database(vertx.getDelegate(), event.result().getJsonObject("db"), dbEvent -> {
                    if (dbEvent.succeeded()) {
                        System.out.println("db connected");
                        StatisticsService statisticsService = new StatisticsServiceImpl(vertx, dbEvent.result()).initialize(e -> {});
                        InvestmentService investmentService = new InvestmentServiceImpl(vertx, dbEvent.result(), statisticsService).initialize(e -> {});
                        AuthService authService = new AuthServiceImpl(vertx, dbEvent.result());

                        vertx.getDelegate().deployVerticle(new RestService(investmentService, statisticsService, authService, dbEvent.result()), options);
                    }
                });
            }
        });
    }


    private Future<Void> deployRestService(DeploymentOptions options) {
        Future<String> future = Future.future();
        vertx.deployVerticle(RestService.class.getName(), options, future.completer());
        return future.map(r -> null);
    }

}
