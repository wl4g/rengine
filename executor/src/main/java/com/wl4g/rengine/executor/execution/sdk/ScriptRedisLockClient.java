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
package com.wl4g.rengine.executor.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_failure;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_success;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_time;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_total;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.jedis.JedisClient;
import com.wl4g.infra.common.locks.JedisLockManager;
import com.wl4g.rengine.common.exception.ExecutionScriptException;
import com.wl4g.rengine.executor.execution.sdk.datasource.RedisSourceFacade;
import com.wl4g.rengine.executor.meter.MeterUtil;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.string.SetArgs;
import io.quarkus.redis.datasource.string.StringCommands;
import io.vertx.mutiny.redis.client.RedisAPI;
import lombok.ToString;
import redis.clients.jedis.params.SetParams;

/**
 * {@link ScriptRedisLockClient}
 * 
 * @author James Wong
 * @version 2022-09-25
 * @since v1.0.0
 */
@ToString(callSuper = true)
public class ScriptRedisLockClient {
    final static String METHOD_GET_LOCK = "getLock";

    final @NotNull JedisLockManager jedisLockManager;

    public @HostAccess.Export ScriptRedisLockClient(@NotNull final RedisSourceFacade redisSourceFacade) {
        notNullOf(redisSourceFacade, "redisSourceFacade");
        this.jedisLockManager = new JedisLockManager(redisSourceFacade.getJedisClient());
    }

    public @HostAccess.Export ScriptRedisLockClient(@NotNull final RedisDataSource redisDS) {
        this.jedisLockManager = buildJedisLockManager(redisDS);
    }

    public @HostAccess.Export Lock getLock(@NotBlank String lockName) {
        return getLock(lockName, 6_000L);
    }

    public @HostAccess.Export Lock getLock(@NotBlank String lockName, @Min(0) long timeoutMs) {
        MeterUtil.counter(execution_sdk_client_total, ScriptRedisLockClient.class, METHOD_GET_LOCK);
        try {
            final Lock lock = MeterUtil.timer(execution_sdk_client_time, ScriptRedisLockClient.class, METHOD_GET_LOCK,
                    () -> jedisLockManager.getLock(lockName, timeoutMs, TimeUnit.MILLISECONDS));
            MeterUtil.counter(execution_sdk_client_success, ScriptRedisLockClient.class, METHOD_GET_LOCK);
            return lock;
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptRedisLockClient.class, METHOD_GET_LOCK);
            throw new ExecutionScriptException(format("Failed to get lock for : '%s'", lockName), e);
        }
    }

    public static JedisLockManager buildJedisLockManager(final @NotNull RedisDataSource redisDS) {
        notNullOf(redisDS, "redisDS");
        final StringCommands<String, String> redisStringCommands = redisDS.string(String.class);
        final RedisAPI redisApi = RedisAPI.api(redisDS.getReactive().getRedis());
        return new JedisLockManager(new JedisClient() {
            @Override
            public void close() throws IOException {
            }

            @Override
            public Object eval(String script, List<String> keys, List<String> args) {
                return redisApi.evalAndAwait(asList(script, "1", keys.get(0), args.get(0))).toString();
            }

            @Override
            public String get(String key) {
                return redisStringCommands.get(key);
            }

            @Override
            public String set(String key, String value, SetParams params) {
                redisStringCommands.set(key, value, new SetArgs().nx().px(Duration.ofMillis(params.getParam("px"))));
                return "OK";
            }
        });
    }

}
