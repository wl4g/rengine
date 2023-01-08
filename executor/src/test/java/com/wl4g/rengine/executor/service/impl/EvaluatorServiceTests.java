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
import static java.util.Collections.singletonList;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.executor.service.EngineExecutionService;
import com.wl4g.rengine.executor.util.TestDefaultBaseSetup;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.bitmap.ReactiveBitMapCommands;
import io.quarkus.redis.datasource.geo.ReactiveGeoCommands;
import io.quarkus.redis.datasource.hash.ReactiveHashCommands;
import io.quarkus.redis.datasource.hyperloglog.ReactiveHyperLogLogCommands;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.list.ReactiveListCommands;
import io.quarkus.redis.datasource.pubsub.ReactivePubSubCommands;
import io.quarkus.redis.datasource.set.ReactiveSetCommands;
import io.quarkus.redis.datasource.sortedset.ReactiveSortedSetCommands;
import io.quarkus.redis.datasource.string.GetExArgs;
import io.quarkus.redis.datasource.string.ReactiveStringCommands;
import io.quarkus.redis.datasource.string.SetArgs;
import io.quarkus.redis.datasource.transactions.OptimisticLockingTransactionResult;
import io.quarkus.redis.datasource.transactions.ReactiveTransactionalRedisDataSource;
import io.quarkus.redis.datasource.transactions.TransactionResult;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.redis.client.Command;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.Response;

/**
 * {@link EvaluatorServiceTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v1.0.0
 */
// @QuarkusTest
// @ExtendWith(MockitoExtension.class)
// @QuarkusTestResource(value = MongoTestResource.class, initArgs =
// @ResourceArg(name = MongoTestResource.PORT, value = "27017"))
public class EvaluatorServiceTests {

    // @Mock
    // @org.mockito.Mock
    // @InjectMock(convertScopes = true)
    EngineExecutionService engineExecutionService;

    @Before
    public void setup() {
        // MockitoAnnotations.openMocks(this);
        // JobService mock = Mockito.mock(JobService.class);
        // QuarkusMock.installMockForType(mock, JobService.class);

        // Manual setup/inject depends.
        final EngineExecutionServiceImpl evaluatorService = new EngineExecutionServiceImpl(testNullReactiveRedisDataSource);
        evaluatorService.mongoRepository = TestDefaultBaseSetup.createMongoRepository();
        evaluatorService.config = TestDefaultBaseSetup.createExecutionConfig();
        this.engineExecutionService = evaluatorService;
    }

    @Test
    public void testFindScenesWorkflowGraphRules() {
        try {
            List<ScenesWrapper> sceneses = engineExecutionService.findScenesWorkflowGraphRules(singletonList("ecommerce_trade_gift"),
                    1);
            System.out.println(toJSONString(sceneses, true));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    final static String testNullScenesesJson = null;
    final static ReactiveRedisDataSource testNullReactiveRedisDataSource = new ReactiveRedisDataSource() {

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
            return new ReactiveStringCommands<>() {

                @Override
                public Uni<Long> append(K key, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> decr(K key) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> decrby(K key, long amount) {
                    throw new UnsupportedOperationException();
                }

                @SuppressWarnings("unchecked")
                @Override
                public Uni<V> get(K key) {
                    return (Uni<V>) Uni.createFrom().item(() -> testNullScenesesJson);
                }

                @Override
                public Uni<V> getdel(K key) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<V> getex(K key, GetExArgs args) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<String> getrange(K key, long start, long end) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<V> getset(K key, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> incr(K key) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> incrby(K key, long amount) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Double> incrbyfloat(K key, double amount) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<String> lcs(K key1, K key2) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> lcsLength(K key1, K key2) {
                    throw new UnsupportedOperationException();
                }

                @SuppressWarnings("unchecked")
                @Override
                public Uni<Map<K, V>> mget(K... keys) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Void> mset(Map<K, V> map) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Boolean> msetnx(Map<K, V> map) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Void> psetex(K key, long milliseconds, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Void> set(K key, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Void> set(K key, V value, SetArgs setArgs) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<V> setGet(K key, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<V> setGet(K key, V value, SetArgs setArgs) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Void> setex(K key, long seconds, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Boolean> setnx(K key, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> setrange(K key, long offset, V value) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public Uni<Long> strlen(K key) {
                    throw new UnsupportedOperationException();
                }
            };
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
            throw new UnsupportedOperationException();
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
    };

}
