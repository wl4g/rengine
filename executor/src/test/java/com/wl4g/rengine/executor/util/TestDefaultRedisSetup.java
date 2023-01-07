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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.util;

import java.time.Duration;
import java.util.OptionalInt;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.runtime.LaunchMode;
import io.quarkus.vertx.core.runtime.QuarkusExecutorFactory;
import io.quarkus.vertx.core.runtime.config.VertxConfiguration;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxBuilder;
import io.vertx.core.net.impl.transport.Transport;
import io.vertx.core.spi.VertxThreadFactory;
import io.vertx.mutiny.redis.client.Redis;
import io.vertx.mutiny.redis.client.RedisAPI;
import io.vertx.redis.client.RedisClientType;
import io.vertx.redis.client.RedisOptions;

/**
 * {@link TestDefaultRedisSetup}
 * 
 * @author James Wong
 * @version 2023-01-07
 * @since v1.0.0
 */
public class TestDefaultRedisSetup {

    public static Vertx buildVertxDefault() {
        final VertxConfiguration conf = new VertxConfiguration();
        conf.queueSize = OptionalInt.of(2);
        conf.workerPoolSize = 2;
        conf.eventLoopsPoolSize = OptionalInt.of(2);
        conf.internalBlockingPoolSize = 2;
        conf.keepAliveTime = Duration.ofSeconds(3);
        conf.maxWorkerExecuteTime = Duration.ofSeconds(3);
        return new VertxBuilder().transport(Transport.transport(true))
                .executorServiceFactory(new QuarkusExecutorFactory(conf, LaunchMode.TEST))
                .threadFactory(VertxThreadFactory.INSTANCE)
                .vertx();
    }

    public static Redis buildRedisDefault(final Vertx vertx) {
        final RedisOptions options = new RedisOptions().addConnectionString("redis://localhost:6379")
                .addConnectionString("redis://localhost:6380")
                .addConnectionString("redis://localhost:6381")
                .addConnectionString("redis://localhost:7379")
                .addConnectionString("redis://localhost:7380")
                .addConnectionString("redis://localhost:7381")
                .setPassword("zzx!@#$%")
                .setType(RedisClientType.CLUSTER);

        // final io.vertx.redis.client.impl.RedisClient redisClient = new
        // io.vertx.redis.client.impl.RedisClient(vertx, options);

        return Redis.createClient(new io.vertx.mutiny.core.Vertx(vertx), options);
    }

    public static RedisAPI buildRedisAPIDefault(final Redis redis) {
        return RedisAPI.api(redis);
    }

    public static RedisDataSource buildRedisDataSourceDefault() {
        final Vertx vertx = buildVertxDefault();
        final Redis redis = buildRedisDefault(vertx);
        final RedisAPI redisAPI = buildRedisAPIDefault(redis);
        return new io.quarkus.redis.runtime.datasource.BlockingRedisDataSourceImpl(new io.vertx.mutiny.core.Vertx(vertx), redis,
                redisAPI, Duration.ofSeconds(5));
    }

}
