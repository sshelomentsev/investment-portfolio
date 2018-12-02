package com.sshelomentsev.rest;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.model.AsyncResultFailure;
import com.sshelomentsev.model.UserProfile;
import com.sshelomentsev.service.UserService;
import com.sshelomentsev.service.InvestmentService;
import com.sshelomentsev.service.StatisticsService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.reactivex.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.AuthHandler;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.CookieHandler;
import io.vertx.reactivex.ext.web.handler.JWTAuthHandler;
import io.vertx.reactivex.ext.web.handler.SessionHandler;
import io.vertx.reactivex.ext.web.handler.UserSessionHandler;
import io.vertx.reactivex.ext.web.sstore.LocalSessionStore;
import io.vertx.reactivex.ext.web.sstore.SessionStore;

public class RestService extends AbstractVerticle {

    private InvestmentService investmentService;
    private StatisticsService statisticsService;
    private UserService userService;
    private Database db;

    public RestService(InvestmentService investmentService, StatisticsService statisticsService, UserService userService, Database db) {
        this.investmentService = investmentService;
        this.statisticsService = statisticsService;
        this.userService = userService;
        this.db = db;
    }

    @Override
    public void start(Future<Void> startFuture) {
        JWTAuthOptions config = new JWTAuthOptions()
                .setKeyStore(new KeyStoreOptions()
                        .setPath("keystore.jceks")
                        .setPassword("secret"));


        JWTAuth authProvider = JWTAuth.create(vertx, config);
        AuthHandler authHandler = JWTAuthHandler.create(authProvider);

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().produces("application/json");

        CookieHandler cookieHandler = CookieHandler.create();
        router.route().handler(cookieHandler);

        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(sessionHandler);

        router.route().handler(UserSessionHandler.create(authProvider));


        router.route("/api/v1/*").handler(authHandler);


        router.post("/api/users/login").handler(createUserAuthHandler(authProvider));
        router.post("/api/users/logout").handler(ctx -> {
            ctx.clearUser();
            ctx.response().setStatusCode(200).end();
        });
        router.route("/api/users/profile").handler(authHandler);
        router.get("/api/users/profile").handler(createGetUserProfileHandler());

        router.post("/api/users/signup").handler(createUserAccount());

        router.get("/api/v1/snapshots/:period").handler(ctx ->
                statisticsService.getSnapshots(ctx.request().getParam("period"), event ->
                        processArrayResponse(ctx, event)));

        router.post("/api/v1/coins/buy").handler(createBuyCoinsHandler());
        router.post("/api/v1/coins/sell").handler(createSellCoinsHandler());
        router.get("/api/v1/coins/transactions").handler(ctx ->
                investmentService.getTransactionsHistory(getUserId(ctx), event -> processArrayResponse(ctx, event)));
        router.get("/api/v1/portfolio").handler(ctx ->
                investmentService.getStackingCoins(getUserId(ctx), event -> processArrayResponse(ctx, event)));

        vertx.createHttpServer().requestHandler(router::accept).listen(8888);
    }

    private Handler<RoutingContext> createGetUserProfileHandler() {
        return ctx -> {
            userService.getUserProfile(ctx.user().principal().getString("_id"),
                    event -> processJsonResponse(ctx, event));
        };

    }

    private Handler<RoutingContext> createUserAccount() {
        return ctx -> {
            try {
                UserProfile profile = ctx.getBodyAsJson().mapTo(UserProfile.class);
                userService.createUser(profile, event -> processJsonResponse(ctx, event));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                ctx.response().setStatusCode(400).end();
            }
        };
    }

    private Handler<RoutingContext> createUserAuthHandler(JWTAuth provider) {
        return ctx -> {
            final String username = ctx.getBodyAsJson().getString("username");
            final String password = ctx.getBodyAsJson().getString("password");
            userService.authenticate(username, password, event -> {
                if (event.succeeded()) {
                    final String token = provider.generateToken(new JsonObject().
                            put(username, password).put("_id", event.result().getString("_id")), new JWTOptions());
                    final JsonObject ret = event.result().put("token", token);
                    ctx.response().putHeader("Content-type", "application/json")
                            .end(ret.encodePrettily());
                } else {
                    event.cause().printStackTrace();
                    ctx.response().setStatusCode(401)
                            .end(new JsonObject().put("success", false).put("error", event.cause().getMessage())
                                    .encodePrettily());
                }
            });
        };
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
            ctx.response().setStatusCode(400).putHeader("Content-tyoe", "application/json")
                    .end(((AsyncResultFailure) event).errorResult().encodePrettily());
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
