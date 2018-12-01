package com.sshelomentsev.rest;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.auth.RxAuthProvider;
import com.sshelomentsev.auth.UserAuthProvider;
import com.sshelomentsev.model.UserProfile;
import com.sshelomentsev.service.UserService;
import com.sshelomentsev.service.InvestmentService;
import com.sshelomentsev.service.StatisticsService;
import com.sshelomentsev.util.Runner;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.BasicAuthHandler;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CookieHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.handler.UserSessionHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import io.vertx.reactivex.ext.web.sstore.SessionStore;

public class RestService extends AbstractVerticle {

    public static void main(String... args) {
        Runner.runExample();
    }

    private InvestmentService investmentService;
    private StatisticsService statisticsService;
    private UserService authService;
    private Database db;

    public RestService(InvestmentService investmentService, StatisticsService statisticsService, UserService authService, Database db) {
        this.investmentService = investmentService;
        this.statisticsService = statisticsService;
        this.authService = authService;
        this.db = db;
    }

    @Override
    public void start(Future<Void> startFuture) {
        UserAuthProvider authProvider = new UserAuthProvider(vertx, db);
        RxAuthProvider provider = new RxAuthProvider(authProvider);
        AuthHandler authHandler = BasicAuthHandler.create(provider);
        authHandler.addAuthority("whatever");

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().produces("application/json");

        CookieHandler cookieHandler = CookieHandler.create();
        router.route().handler(cookieHandler);

        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(sessionHandler);

        router.route().handler(UserSessionHandler.create(provider));

        router.route("/api/v1/*").handler(authHandler);

        router.post("/api/users/login").handler(createUserAuthHandler(provider));
        router.post("/api/users/logout").handler(ctx -> {
            ctx.clearUser();
            ctx.response().setStatusCode(302).end();
        });
        router.route("/api/users/profile").handler(authHandler);
        router.get("/api/users/profile").handler(createGetUserProfileHandler());

        router.post("/api/users/signup").handler(createUserAccount());

        router.post("/api/v1/coins/buy").handler(createBuyCoinsHandler());
        router.post("/api/v1/coins/sell").handler(createSellCoinsHandler());

        router.get("/api/v1/coins/transactions").handler(ctx ->
                investmentService.getTransactionsHistory(getUserId(ctx), event -> processArrayResponse(ctx, event)));

        router.get("/api/v1/portfolio").handler(ctx ->
                investmentService.getStackingCoins(getUserId(ctx), event -> processArrayResponse(ctx, event)));

        vertx.createHttpServer().requestHandler(router::accept).listen(8888);
    }

    private Handler<RoutingContext> createGetUserProfileHandler() {
        return ctx -> authService.getUserProfile(ctx.user().principal().getString("_id"),
                event -> processJsonResponse(ctx, event));
    }

    private Handler<RoutingContext> createUserAccount() {
        return ctx -> {
            try {
                UserProfile profile = ctx.getBodyAsJson().mapTo(UserProfile.class);
                authService.createUser(profile, event -> processJsonResponse(ctx, event));
            } catch (IllegalArgumentException e) {
                ctx.response().setStatusCode(400).end();
            }
        };
    }

    private Handler<RoutingContext> createUserAuthHandler(RxAuthProvider provider) {
        return ctx -> provider.authenticate(ctx.getBodyAsJson(), res -> {
            if (res.succeeded()) {
                ctx.response().putHeader("Content-type", "application/json")
                        .end(res.result().principal().encodePrettily());
            } else {
                ctx.response().setStatusCode(401).end();
            }
        });
    }

    private Handler<RoutingContext> createSellCoinsHandler() {
        return ctx -> {
            final String userId = ctx.user().principal().getString("_id");
            final String currency = ctx.getBodyAsJson().getString("currency");
            final double amount = ctx.getBodyAsJson().getDouble("amount");
            investmentService.sellCoins(userId, currency, amount, event -> processJsonResponse(ctx, event));
        };
    }

    private Handler<RoutingContext> createBuyCoinsHandler() {
        return ctx -> {
            System.out.println(ctx.getBodyAsJson().encodePrettily());
            final String userId = ctx.user().principal().getString("_id");
            final String currency = ctx.getBodyAsJson().getString("currency");
            final double amount = ctx.getBodyAsJson().getDouble("amount");
            investmentService.buyCoins(userId, currency, amount, event -> processJsonResponse(ctx, event));

        };
    }

    private void processJsonResponse(RoutingContext ctx, AsyncResult<JsonObject> event) {
        if (event.succeeded()) {
            ctx.response().putHeader("Content-type", "application/json").end(event.result().encodePrettily());
        } else {
            ctx.response().setStatusCode(400).end();
        }
    }

    private void processArrayResponse(RoutingContext ctx, AsyncResult<JsonArray> event) {
        if (event.succeeded()) {
            ctx.response().putHeader("Content-type", "application/json").end(event.result().encodePrettily());
        } else {
            ctx.response().setStatusCode(400).end();
        }
    }

    private String getUserId(RoutingContext ctx) {
        return ctx.user().principal().getString("_id");
    }

}
