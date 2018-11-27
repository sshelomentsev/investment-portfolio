package com.sshelomentsev.arangodb;

import com.arangodb.ArangoCollection;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.List;

public class Collection {

    private Vertx vertx;
    private ArangoCollection collection;

    public Collection(Vertx vertx, ArangoCollection collection) {
        this.vertx = vertx;
        this.collection = collection;
    }

    public Collection get(String key, Handler<AsyncResult<JsonObject>> handler) {
        vertx.executeBlocking(future -> future.complete(new JsonObject(collection.getDocument(key, String.class))), handler);
        return this;
    }

    public Collection insert(JsonObject document, Handler<AsyncResult<JsonObject>> handler) {
        vertx.executeBlocking(fut -> fut.complete(new JsonObject(Json.encode(collection.insertDocument(document.encode())))), handler);
        return this;
    }

    public Collection insert(List<String> documents, Handler<AsyncResult<JsonObject>> handler) {
        vertx.executeBlocking(fut -> fut.complete(new JsonObject(Json.encode(collection.insertDocuments(documents)))), handler);
        return this;
    }

    public Collection insert(String key, JsonObject document, Handler<AsyncResult<JsonObject>> handler) {
        vertx.executeBlocking(future -> future.complete(new JsonObject(Json.encode(collection.insertDocument(document.put("_key", key).encode())))), handler);
        return this;
    }

    public Collection update(String key, JsonObject document, Handler<AsyncResult<JsonObject>> handler) {
        vertx.executeBlocking(fut -> fut.complete(new JsonObject(Json.encode(collection.updateDocument(key, document.encode())))), handler);
        return this;
    }


}
