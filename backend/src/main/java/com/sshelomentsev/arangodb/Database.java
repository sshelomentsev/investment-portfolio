package com.sshelomentsev.arangodb;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.arangodb.model.TransactionOptions;
import com.arangodb.util.MapBuilder;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * ArangoDatabase wrapper that provides a way to make queries in asynchronous manner
 */
public class Database {

    private Vertx vertx;
    private ArangoDatabase database;

    public Database(Vertx vertx, JsonObject options, Handler<AsyncResult<Database>> handler) {
        System.out.println("DB");
        this.vertx = vertx;
        vertx.executeBlocking(future -> {
            try {
                ArangoDB.Builder builder = new ArangoDB.Builder();
                builder.host(options.getString("host"), options.getInteger("port"));
                builder.user(options.getString("user"));
                builder.password(options.getString("password"));
                builder.maxConnections(options.getInteger("maxConnections", 20));

                database = builder.build().db(options.getString("name"));
            } catch (Exception e) {
                e.printStackTrace();
                future.fail(e);
            } finally {
                future.complete(this);
            }
        }, handler);
    }

    public Collection collection(String name) {
        return new Collection(vertx, database.collection(name));
    }

    public Database query(String query, Map<String, Object> bindVars, Handler<AsyncResult<JsonArray>> handler) {
        vertx.executeBlocking(future -> future.complete(
                new JsonArray(database.query(query, bindVars, null, String.class)
                        .asListRemaining().stream().map(JsonObject::new).collect(Collectors.toList()))
        ), handler);
        return this;
    }

    public Database query(String query, Handler<AsyncResult<JsonArray>> handler) {
        return query(query, new MapBuilder().get(), handler);
    }

    public Database transaction(String action, TransactionOptions options, Handler<AsyncResult<JsonObject>> handler) {
        vertx.executeBlocking(future -> future.complete(
                new JsonObject(database.transaction(action, String.class, options))), handler);
        return this;
    }

}
