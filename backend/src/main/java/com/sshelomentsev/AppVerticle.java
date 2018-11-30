package com.sshelomentsev;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.rest.RestService;
import com.sshelomentsev.service.UserService;
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
    public void start(Future<Void> startFuture) {
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx);
        configRetriever.getConfig(event -> {
            if (event.succeeded()) {
                DeploymentOptions options = new DeploymentOptions();
                options.setConfig(event.result());

                new Database(vertx.getDelegate(), event.result().getJsonObject("db"), dbEvent -> {
                    if (dbEvent.succeeded()) {
                        StatisticsService statisticsService = new StatisticsServiceImpl(vertx, dbEvent.result()).initialize(e -> {});
                        InvestmentService investmentService = new InvestmentServiceImpl(dbEvent.result(), statisticsService).initialize(e -> {});
                        UserService authService = new AuthServiceImpl(dbEvent.result());

                        vertx.getDelegate().deployVerticle(new RestService(investmentService, statisticsService, authService, dbEvent.result()), options);
                    }
                });
            }
        });
    }

}
