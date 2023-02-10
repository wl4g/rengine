/*
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.util.redis;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.bitmap.BitMapCommands;
import io.quarkus.redis.datasource.geo.GeoCommands;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.hyperloglog.HyperLogLogCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.pubsub.PubSubCommands;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.sortedset.SortedSetCommands;
import io.quarkus.redis.datasource.string.StringCommands;
import io.quarkus.redis.datasource.transactions.OptimisticLockingTransactionResult;
import io.quarkus.redis.datasource.transactions.TransactionResult;
import io.quarkus.redis.datasource.transactions.TransactionalRedisDataSource;
import io.quarkus.redis.runtime.datasource.ReactiveRedisDataSourceImpl;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisConnection;
import io.vertx.redis.client.Request;
import io.vertx.redis.client.Response;
import lombok.Getter;

/**
 * {@link RedisDataSourceImpl}
 * 
 * @author James Wong
 * @version 2023-02-11
 * @since v1.0.0
 */
@Getter
public class RedisDataSourceImpl implements RedisDataSource {

    private final Vertx vertx;
    private final Redis redis;
    private final RedisAPI redisAPI;
    private final RedisConnection connection;
    private final Marshaller marshaller;

    public RedisDataSourceImpl(ReactiveRedisDataSource reactiveRedisDS) {
        this((ReactiveRedisDataSourceImpl) reactiveRedisDS);
    }

    public RedisDataSourceImpl(ReactiveRedisDataSourceImpl reactiveRedisDS) {
        this(reactiveRedisDS.getVertx().getDelegate(), reactiveRedisDS.getRedis().getDelegate());
    }

    public RedisDataSourceImpl(Vertx vertx, Redis redis) {
        this(vertx, redis, new Marshaller(), null);
    }

    public RedisDataSourceImpl(Vertx vertx, Redis redis, Marshaller marshaller) {
        this(vertx, redis, marshaller, null);
    }

    public RedisDataSourceImpl(Vertx vertx, Redis redis, Marshaller marshaller, RedisConnection connection) {
        this.vertx = notNullOf(vertx, "vertx");
        this.redis = notNullOf(redis, "redis");
        this.redisAPI = RedisAPI.api(redis);
        this.marshaller = notNullOf(marshaller, "marshaller");
        this.connection = connection;
    }

    public Future<Response> execute(Request request) {
        if (connection != null) {
            return connection.send(request);
        }
        return redis.send(request);
    }

    @Override
    public void withConnection(Consumer<RedisDataSource> consumer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionResult withTransaction(Consumer<TransactionalRedisDataSource> tx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransactionResult withTransaction(Consumer<TransactionalRedisDataSource> tx, String... watchedKeys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <I> OptimisticLockingTransactionResult<I> withTransaction(
            Function<RedisDataSource, I> preTxBlock,
            BiConsumer<I, TransactionalRedisDataSource> tx,
            String... watchedKeys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void select(long index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flushall() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, F, V> HashCommands<K, F, V> hash(Class<K> redisKeyType, Class<F> typeOfField, Class<V> typeOfValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> GeoCommands<K, V> geo(Class<K> redisKeyType, Class<V> memberType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> KeyCommands<K> key(Class<K> redisKeyType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> SortedSetCommands<K, V> sortedSet(Class<K> redisKeyType, Class<V> valueType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> StringCommands<K, V> string(Class<K> redisKeyType, Class<V> valueType) {
        return new StringCommandsImpl<>(this, marshaller, valueType);
    }

    @Override
    public <K, V> SetCommands<K, V> set(Class<K> redisKeyType, Class<V> memberType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> ListCommands<K, V> list(Class<K> redisKeyType, Class<V> memberType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K, V> HyperLogLogCommands<K, V> hyperloglog(Class<K> redisKeyType, Class<V> memberType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <K> BitMapCommands<K> bitmap(Class<K> redisKeyType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <V> PubSubCommands<V> pubsub(Class<V> messageType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public io.vertx.mutiny.redis.client.Response execute(String command, String... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public io.vertx.mutiny.redis.client.Response execute(io.vertx.mutiny.redis.client.Command command, String... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public io.vertx.mutiny.redis.client.Response execute(Command command, String... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReactiveRedisDataSource getReactive() {
        throw new UnsupportedOperationException();
    }

}
