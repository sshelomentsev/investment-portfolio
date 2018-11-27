package com.sshelomentsev.rest;

import com.sshelomentsev.arangodb.Database;
import com.sshelomentsev.auth.RxAuthProvider;
import com.sshelomentsev.auth.UserAuthProvider;
import com.sshelomentsev.service.InvestmentService;
import com.sshelomentsev.service.impl.InvestmentServiceImpl;
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

    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);
        db = new Database(vertx, context.config().getJsonObject("db"), handler -> {
            if (handler.succeeded()) {
                System.out.println("db connected");
                investmentService = new InvestmentServiceImpl(vertx, handler.result());
            }
        });
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        UserAuthProvider authProvider = new UserAuthProvider(vertx, db);
        RxAuthProvider provider = new RxAuthProvider(authProvider);
        AuthHandler authHandler = BasicAuthHandler.create(provider);
        authHandler.addAuthority("whatever");

        WebClient client = WebClient.create(vertx);
        Router router = Router.router(vertx);
        router.route().produces("application/json");

        CookieHandler cookieHandler = CookieHandler.create();
        router.route().handler(cookieHandler);

        SessionStore store = LocalSessionStore.create(vertx);
        SessionHandler sessionHandler = SessionHandler.create(store);
        router.route().handler(sessionHandler);

        router.route().handler(UserSessionHandler.create(provider));

        router.route("/api/v1/*").handler(authHandler);
        router.get("/api/v1/ticks").handler(createTicksHandler(client));
        router.get("/api/v1/snapshots/:period").handler(createSnapshotsHandler(client));
        router.get("/api/v1/marketcap").handler(createMarkerCapHandler(client));

        router.post("/api/v1/coins/buy");
        router.post("/api/v1/coins/sell");

        vertx.createHttpServer().requestHandler(router::accept).listen(8888);
    }

    private Handler<RoutingContext> createSnapshotsHandler(WebClient client) {
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

    private Handler<RoutingContext> createMarkerCapHandler(WebClient client) {
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

    private Handler<RoutingContext> createTicksHandler(WebClient client) {
        return ctx -> {
            List<Observable<JsonObject>> observables = Arrays.stream(currencies)
                    .map(currency -> client.getAbs(getPriceUrl(currency))
                            .rxSend()
                            .toObservable()
                            .map(resp -> new JsonObject().put(currency, resp.bodyAsJsonObject())))
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
