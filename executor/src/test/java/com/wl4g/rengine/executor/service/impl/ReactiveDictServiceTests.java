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
package com.wl4g.rengine.executor.service.impl;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.isNull;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import com.wl4g.rengine.common.entity.Dict;
import com.wl4g.rengine.common.entity.Dict.DictType;
import com.wl4g.rengine.executor.util.TestDefaultBaseSetup;
import com.wl4g.rengine.executor.util.TestDefaultRedisSetup;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.ScanArgs;
import io.quarkus.redis.datasource.autosuggest.ReactiveAutoSuggestCommands;
import io.quarkus.redis.datasource.bitmap.ReactiveBitMapCommands;
import io.quarkus.redis.datasource.bloom.ReactiveBloomCommands;
import io.quarkus.redis.datasource.countmin.ReactiveCountMinCommands;
import io.quarkus.redis.datasource.cuckoo.ReactiveCuckooCommands;
import io.quarkus.redis.datasource.geo.ReactiveGeoCommands;
import io.quarkus.redis.datasource.graph.ReactiveGraphCommands;
import io.quarkus.redis.datasource.hash.ReactiveHashCommands;
import io.quarkus.redis.datasource.hash.ReactiveHashScanCursor;
import io.quarkus.redis.datasource.hyperloglog.ReactiveHyperLogLogCommands;
import io.quarkus.redis.datasource.json.ReactiveJsonCommands;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.quarkus.redis.datasource.pubsub.ReactivePubSubCommands;
import io.quarkus.redis.datasource.search.ReactiveSearchCommands;
import io.quarkus.redis.datasource.set.ReactiveSetCommands;
import io.quarkus.redis.datasource.sortedset.ReactiveSortedSetCommands;
import io.quarkus.redis.datasource.string.ReactiveStringCommands;
import io.quarkus.redis.datasource.timeseries.ReactiveTimeSeriesCommands;
import io.quarkus.redis.datasource.topk.ReactiveTopKCommands;
import io.quarkus.redis.datasource.transactions.OptimisticLockingTransactionResult;
import io.quarkus.redis.datasource.transactions.ReactiveTransactionalRedisDataSource;
import io.quarkus.redis.datasource.transactions.TransactionResult;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Response;

/**
 * {@link ReactiveDictServiceTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v1.0.0
 */
@SuppressWarnings("deprecation")
public class ReactiveDictServiceTests {

    static ReactiveDictServiceImpl dictService;

    public void setup() {
        if (isNull(dictService)) {
            synchronized (ReactiveDictServiceTests.class) {
                if (isNull(dictService)) {
                    dictService = new ReactiveDictServiceImpl();
                    dictService.config = TestDefaultBaseSetup.createExecutionConfig();
                    dictService.mongoRepository = TestDefaultBaseSetup.createMongoRepository();
                    // dictService.reactiveRedisDS =
                    // mockReactiveRedisDataSource;
                    dictService.reactiveRedisDS = TestDefaultRedisSetup.buildRedisDataSourceDefault().getReactive();
                    System.out.println("Init ...");
                    dictService.init();
                }
            }
        }
    }

    @Test
    public void testSerialzeDicts() {
        setup();

        final var dict1 = Dict.builder()
                .id(6305460145405952L)
                .type(DictType.ENGINE_EXECUTION_CUSTOM_RESP_TPL)
                .key("dingtalk")
                .value("{\"msg_signature\":\"%s\",\"timeStamp\":\"%s\",\"nonce\":\"%s\",\"encrypt\":\"%s\"}")
                .sort(1)
                .createBy(1L)
                .createDate(new Date())
                .build();
        System.out.println(toJSONString(dict1));
    }

    @Test
    @RepeatedTest(5)
    public void testFindDicts() {
        setup();

        try {
            Uni<List<Dict>> dictsUni = dictService.findDicts(DictType.ENGINE_EXECUTION_CUSTOM_RESP_TPL,
                    "dingtalk" /* null */);

            System.out.println("Await for " + dictsUni + " ...");
            System.out.println("----------------");

            final var dicts = dictsUni.await().atMost(Duration.ofSeconds(60));
            System.out.println(toJSONString(dicts, true));
            assert !dicts.isEmpty();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    final static String mockDict1Json = "{\"id\":6305460145405952,\"orgCode\":null,\"enable\":null,\"labels\":null,\"remark\":null,\"createBy\":1,\"createDate\":\"2023-01-14 23:07:15\",\"updateBy\":null,\"updateDate\":null,\"delFlag\":null,\"humanCreateDate\":null,\"humanUpdateDate\":null,\"type\":\"ENGINE_EXECUTION_CUSTOM_RESP_TPL\",\"key\":\"dingtalk\",\"value\":\"{\\\"msg_signature\\\":\\\"%s\\\",\\\"timeStamp\\\":\\\"%s\\\",\\\"nonce\\\":\\\"%s\\\",\\\"encrypt\\\":\\\"%s\\\"}\",\"sort\":1}";
    final static ReactiveRedisDataSource mockReactiveRedisDataSource = new ReactiveRedisDataSource() {

        @Override
        public <I> Uni<OptimisticLockingTransactionResult<I>> withTransaction(
                Function<ReactiveRedisDataSource, Uni<I>> preTxBlock,
                BiFunction<I, ReactiveTransactionalRedisDataSource, Uni<Void>> tx,
                String... watchedKeys) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Uni<TransactionResult> withTransaction(
                Function<ReactiveTransactionalRedisDataSource, Uni<Void>> tx,
                String... watchedKeys) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Uni<TransactionResult> withTransaction(Function<ReactiveTransactionalRedisDataSource, Uni<Void>> tx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Uni<Void> withConnection(Function<ReactiveRedisDataSource, Uni<Void>> function) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveStringCommands<K, V> string(Class<K> redisKeyType, Class<V> valueType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveSortedSetCommands<K, V> sortedSet(Class<K> redisKeyType, Class<V> valueType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveSetCommands<K, V> set(Class<K> redisKeyType, Class<V> memberType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Uni<Void> select(long index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <V> ReactivePubSubCommands<V> pubsub(Class<V> messageType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveListCommands<K, V> list(Class<K> redisKeyType, Class<V> memberType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> ReactiveKeyCommands<K> key(Class<K> redisKeyType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveHyperLogLogCommands<K, V> hyperloglog(Class<K> redisKeyType, Class<V> memberType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, F, V> ReactiveHashCommands<K, F, V> hash(Class<K> redisKeyType, Class<F> fieldType, Class<V> valueType) {
            return new ReactiveHashCommands<K, F, V>() {

                @SuppressWarnings("unchecked")
                @Override
                public Uni<Integer> hdel(K key, F... fields) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Boolean> hexists(K key, F field) {
                    throw new UnsupportedOperationException();
                }

                @SuppressWarnings("unchecked")
                @Override
                public Uni<V> hget(K key, F field) {
                    return Uni.createFrom().item(() -> {
                        return (V) mockDict1Json;
                    });
                }

                @Override
                public Uni<Long> hincrby(K key, F field, long amount) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Double> hincrbyfloat(K key, F field, double amount) {
                    throw new UnsupportedOperationException();
                }

                @SuppressWarnings("unchecked")
                @Override
                public Uni<Map<F, V>> hgetall(K key) {
                    return Uni.createFrom().item(() -> {
                        Map<F, V> mockDicts = new HashMap<>();
                        String hashKey = DictType.ENGINE_EXECUTION_CUSTOM_RESP_TPL.name() + ":" + "dingtalk";
                        mockDicts.put((F) hashKey, (V) mockDict1Json);
                        return mockDicts;
                    });
                }

                @Override
                public Uni<List<F>> hkeys(K key) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> hlen(K key) {
                    throw new UnsupportedOperationException();
                }

                @SuppressWarnings("unchecked")
                @Override
                public Uni<Map<F, V>> hmget(K key, F... fields) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Void> hmset(K key, Map<F, V> map) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<F> hrandfield(K key) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<List<F>> hrandfield(K key, long count) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Map<F, V>> hrandfieldWithValues(K key, long count) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public ReactiveHashScanCursor<F, V> hscan(K key) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public ReactiveHashScanCursor<F, V> hscan(K key, ScanArgs scanArgs) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Boolean> hset(K key, F field, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> hset(K key, Map<F, V> map) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Boolean> hsetnx(K key, F field, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> hstrlen(K key, F field) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<List<V>> hvals(K key) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public ReactiveRedisDataSource getDataSource() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public Redis getRedis() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveGeoCommands<K, V> geo(Class<K> redisKeyType, Class<V> memberType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Uni<Void> flushall() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Uni<Response> execute(io.vertx.redis.client.Command command, String... args) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Uni<Response> execute(Command command, String... args) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Uni<Response> execute(String command, String... args) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> ReactiveBitMapCommands<K> bitmap(Class<K> redisKeyType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> ReactiveAutoSuggestCommands<K> autosuggest(Class<K> arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveBloomCommands<K, V> bloom(Class<K> redisKeyType, Class<V> valueType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveCountMinCommands<K, V> countmin(Class<K> redisKeyType, Class<V> valueType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveCuckooCommands<K, V> cuckoo(Class<K> redisKeyType, Class<V> valueType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> ReactiveGraphCommands<K> graph(Class<K> arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> ReactiveJsonCommands<K> json(Class<K> redisKeyType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> ReactiveSearchCommands<K> search(Class<K> arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> ReactiveTimeSeriesCommands<K> timeseries(Class<K> arg0) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveTopKCommands<K, V> topk(Class<K> redisKeyType, Class<V> valueType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> ReactiveValueCommands<K, V> value(Class<K> redisKeyType, Class<V> valueType) {
            throw new UnsupportedOperationException();
        }
    };

}
