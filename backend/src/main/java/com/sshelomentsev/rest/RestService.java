package com.sshelomentsev.rest;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.auth.RxAuthProvider;
import com.sshelomentsev.auth.UserAuthProvider;
import com.sshelomentsev.service.InvestmentService;
import com.sshelomentsev.service.StatisticsService;
import com.sshelomentsev.service.impl.InvestmentServiceImpl;
import com.sshelomentsev.service.impl.StatisticsServiceImpl;
import com.sshelomentsev.util.Runner;
import io.reactivex.Observable;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import io.vertx.core.Vertx;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RestService extends AbstractVerticle {

    private static final String[] currencies = new String[]{"BTC", "ETH", "XRP", "LTC", "DASH"};

    public static void main(String... args) {
        Runner.runExample();
    }

    private Database db;
    private InvestmentService investmentService;
    private StatisticsService statisticsService;
    private WebClient client;

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        db = new Database(vertx, context.config().getJsonObject("db"), handler -> {
            if (handler.succeeded()) {
                System.out.println("db connected");
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        UserAuthProvider authProvider = new UserAuthProvider(vertx, db);
        RxAuthProvider provider = new RxAuthProvider(authProvider);
        AuthHandler authHandler = BasicAuthHandler.create(provider);
        authHandler.addAuthority("whatever");

        statisticsService = new StatisticsServiceImpl(vertx, db);
        investmentService = new InvestmentServiceImpl(vertx, db, statisticsService);

        client = WebClient.create(vertx);
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

        router.route("/api/users/login/").handler(authHandler);
        router.get("/api/users/login").handler(createUserAuthHandler());
        router.get("/api/users/logout").handler(ctx -> {
            ctx.clearUser();
            ctx.response().setStatusCode(302).end();
        });

        router.get("/api/v1/currencies").handler(createCurrenciesHandler());

        router.get("/api/v1/ticks").handler(createTicksHandler());
        router.get("/api/v1/snapshots/:period").handler(createSnapshotsHandler());
        router.get("/api/v1/marketcap").handler(createMarkerCapHandler());

        router.post("/api/v1/coins/buy").handler(createBuyCoinsHandler());
        router.post("/api/v1/coins/sell").handler(createSellCoinsHandler());

        router.get("/api/v1/coins/transactions").handler(createGetTransactionsHandler());

        router.get("/api/v1/portfolio").handler(createInvestmentPortfolioHandler());

        vertx.createHttpServer().requestHandler(router::accept).listen(8888);
    }

    private Handler<RoutingContext> createInvestmentPortfolioHandler() {
        return ctx -> {
            investmentService.getInvestmentPortfolio(getUserId(ctx), event -> {
                if (event.succeeded()) {
                    ctx.response().putHeader("Content-type", "application/json").end(event.result().encodePrettily());
                }
            });
        };
    }


    private Handler<RoutingContext> createCurrenciesHandler() {
        return ctx -> {
            db.query("for c in currency return {code: c.code}", event -> {
                if (event.succeeded()) {
                    System.out.println(event.result().encodePrettily());
                    List<String> currencies = event.result().stream().map(s -> ((JsonObject) s).getString("code")).collect(Collectors.toList());
                    ctx.response().end();
                } else {
                    event.cause().printStackTrace();
                    ctx.response().setStatusCode(400).end();
                }
            });
        };
    }

    private Handler<RoutingContext> createGetTransactionsHandler() {
        return ctx -> {
            investmentService.getTransactionsHistory(getUserId(ctx), event -> {
                if (event.succeeded()) {
                    ctx.response().setStatusCode(200).end(event.result().encodePrettily());
                } else {
                    event.cause().printStackTrace();
                    ctx.response().setStatusCode(400).end();
                }
            });
        };
    }

    private Handler<RoutingContext> createUserAuthHandler() {
        return ctx -> {
            ctx.response().end();
        };
    }

    private Handler<RoutingContext> createSellCoinsHandler() {
        return ctx -> {
            final String userId = ctx.user().principal().getString("_id");
            final String currency = ctx.getBodyAsJson().getString("currency");
            final double amount = ctx.getBodyAsJson().getDouble("amount");
            investmentService.sellCoins(userId, currency, amount, event -> {
                if (event.succeeded()) {
                    ctx.response().setStatusCode(200).end(ctx.user().principal().encodePrettily());
                } else {
                    event.cause().printStackTrace();
                    ctx.response().setStatusCode(400).end();
                }
            });
        };
    }

    private Handler<RoutingContext> createBuyCoinsHandler() {
        return ctx -> {
            final String userId = ctx.user().principal().getString("_id");
            final String currency = ctx.getBodyAsJson().getString("currency");
            final double amount = ctx.getBodyAsJson().getDouble("amount");
            investmentService.buyCoins(userId, currency, amount, event -> {
                if (event.succeeded()) {
                    ctx.response().setStatusCode(200).end(ctx.user().principal().encodePrettily());
                } else {
                    event.cause().printStackTrace();
                    ctx.response().setStatusCode(400).end();
                }
            });

        };
    }

    private Handler<RoutingContext> createSnapshotsHandler() {
        return ctx -> {
            String period = ctx.request().getParam("period");
            if ("day".equals(period) || "month".equals(period) || "week".equals(period)) {
                List<Observable<JsonObject>> observables = Arrays.stream(currencies)
                        .map(currency -> client.getAbs(getSnapshotsUrl(currency))
                                .rxSend()
                                .toObservable()
                                .map(resp -> new JsonObject().put(currency, resp.bodyAsJsonArray())))
                        .collect(Collectors.toList());

                Observable.zip(observables, jsons -> {
                    JsonArray ret = new JsonArray();
                    for (Object json : jsons) {
                        ret.add(json);
                    }
                    return ret;
                }).subscribe(res -> ctx
                        .response()
                        .putHeader("Content-type", "application/json")
                        .end(res.encodePrettily()));
            } else {
                ctx.response().setStatusCode(400).end();
            }
        };

    }

    private Handler<RoutingContext> createMarkerCapHandler() {
        return ctx -> {
            List<Observable<JsonObject>> observables = Arrays.stream(currencies)
                    .map(currency -> client.getAbs(getMarketCapUrl(currency))
                            .rxSend()
                            .toObservable()
                            .map(resp -> new JsonObject().put(currency, resp.bodyAsString())))
                    .collect(Collectors.toList());

            Observable.zip(observables, jsons -> {
                JsonArray ret = new JsonArray();
                for (Object json : jsons) {
                    ret.add(json);
                }
                return ret;
            }).subscribe(res -> ctx
                    .response()
                    .putHeader("Content-type", "application/json")
                    .end(res.encodePrettily()));
        };
    }

    private Handler<RoutingContext> createTicksHandler() {
        return ctx -> {
            statisticsService.getTicks(res -> {
                if (res.succeeded()) {
                    ctx.response().putHeader("Content-type", "application/json").end(res.result().encodePrettily());
                } else {
                    res.cause().printStackTrace();
                    ctx.response().setStatusCode(400).end();
                }
            });
        };
    }

    private String getUserId(RoutingContext ctx) {
        return ctx.user().principal().getString("_id");
    }

    private static String getMarketCapUrl(String currency) {
        return "https://api.cryptometr.io/api/v1/metrics-data/market-cap?currency=" + currency;
    }

    private static String getPriceUrl(String currency) {
        return "https://api.cryptometr.io/api/v1/snapshots/ticker?from=" + currency + "&to=USD";
    }

    private static String getSnapshotsUrl(String currency) {
        return "https://api.cryptometr.io/api/v1/snapshots/chart?from=" + currency + "&to=USD&period=day";
    }
}
