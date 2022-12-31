/**
 * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.execution.sdk.datasource;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseToNode;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_datasource_facade_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_datasource_facade_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_datasource_facade_time;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_datasource_facade_total;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.infra.common.jedis.JedisClient;
import com.wl4g.infra.common.jedis.JedisClientBuilder;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourcePropertiesBase;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.common.entity.DataSourceProperties.RedisDataSourceProperties;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.metrics.MeterUtil;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link RedisSourceFacade}
 *
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@Getter
@CustomLog
@AllArgsConstructor
public class RedisSourceFacade implements DataSourceFacade {

    final static String METHOD_GET = "get";
    final static String METHOD_SET = "set";
    final static String METHOD_SETEX = "setex";
    final static String METHOD_SETNX = "setnx";
    final static String METHOD_SETNXEX = "setnxex";
    final static String METHOD_DEL = "del";
    final static String METHOD_EXPIRE = "expire";
    final static String METHOD_HGETALL = "hgetAll";
    final static String METHOD_HGET = "hget";
    final static String METHOD_HSET = "hset";
    final static String METHOD_HSETNX = "hsetnx";
    final static String METHOD_EVAL = "eval";

    final ExecutionConfig executionConfig;
    final String dataSourceName;
    final JedisClient jedisClient;

    @Override
    public void close() throws IOException {
        if (nonNull(jedisClient)) {
            log.info("Closing to redis single or cluster data source for {} ...", dataSourceName);
            jedisClient.close();
        }
    }

    public JsonNode get(final @NotBlank String key) {
        hasTextOf(key, "key");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_GET);
        try {
            final JsonNode result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                    METHOD_GET, () -> parseToNode(jedisClient.get(key), null));

            MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_GET);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_GET);
            throw e;
        }
    }

    public String set(final @NotBlank String key, final Object value) {
        hasTextOf(key, "key");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_SET);
        try {
            if (nonNull(value)) {
                final String result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                        METHOD_SET, () -> jedisClient.set(key, toJSONString(value)));

                MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_SET);
                return result;
            } else {
                return null;
            }
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_SET);
            throw e;
        }
    }

    public String setex(final @NotBlank String key, final Object value, final @Min(-2) long seconds) {
        hasTextOf(key, "key");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_SETEX);
        try {
            if (nonNull(value)) {
                final String result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                        METHOD_SETEX, () -> jedisClient.setex(key, seconds, toJSONString(value)));

                MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_SETEX);
                return result;
            } else {
                return null;
            }
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_SETEX);
            throw e;
        }
    }

    public Long setnx(final @NotBlank String key, final Object value) {
        return setnxex(key, value, Long.MAX_VALUE);
    }

    public Long setnxex(final @NotBlank String key, final Object value, final @Min(-2) long seconds) {
        hasTextOf(key, "key");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_SETNXEX);
        try {
            if (nonNull(value)) {
                final Long result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                        METHOD_SETNXEX, () -> {
                            try {
                                return jedisClient.setnx(key, toJSONString(value));
                            } finally {
                                if (seconds < Long.MAX_VALUE) {
                                    jedisClient.expire(key, seconds);
                                }
                            }
                        });
                MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_SETNXEX);
                return result;
            } else {
                return 0L;
            }
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_SETNXEX);
            throw e;
        }
    }

    public Long del(final @NotBlank String key) {
        hasTextOf(key, "key");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_DEL);
        try {
            final Long result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                    METHOD_DEL, () -> jedisClient.del(key));

            MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_DEL);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_DEL);
            throw e;
        }
    }

    public Long expire(final @NotBlank String key, final @Min(-2) long seconds) {
        hasTextOf(key, "key");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_EXPIRE);
        try {
            final Long result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                    METHOD_EXPIRE, () -> jedisClient.expire(key, seconds));

            MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_EXPIRE);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_EXPIRE);
            throw e;
        }
    }

    public Map<String, JsonNode> hgetAll(final @NotBlank String key) {
        hasTextOf(key, "key");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_HGETALL);
        try {
            final Map<String, JsonNode> result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName,
                    DataSourceType.REDIS, METHOD_HGETALL,
                    () -> safeMap(jedisClient.hgetAll(key)).entrySet()
                            .stream()
                            .collect(toMap(e -> e.getKey(), e -> parseToNode(e.getValue(), null))));

            MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_HGETALL);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_HGETALL);
            throw e;
        }
    }

    public JsonNode hget(final @NotBlank String key, final @NotBlank String field) {
        hasTextOf(key, "key");
        hasTextOf(field, "field");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_HGET);
        try {
            final JsonNode result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                    METHOD_HGET, () -> parseToNode(jedisClient.hget(key, field), null));

            MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_HGET);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_HGET);
            throw e;
        }
    }

    public Long hset(final @NotBlank String key, final @NotBlank String field, final Object value) {
        hasTextOf(key, "key");
        hasTextOf(field, "field");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_HSET);
        try {
            if (nonNull(value)) {
                final Long result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                        METHOD_HSET, () -> jedisClient.hset(key, field, toJSONString(value)));

                MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_HSET);
                return result;
            } else {
                return 0L;
            }
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_HSET);
            throw e;
        }
    }

    public Long hsetnx(final @NotBlank String key, final @NotBlank String field, final Object value) {
        hasTextOf(key, "key");
        hasTextOf(field, "field");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_HSETNX);
        try {
            if (nonNull(value)) {
                final Long result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                        METHOD_HSETNX, () -> jedisClient.hsetnx(key, field, toJSONString(value)));

                MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_HSETNX);
                return result;
            } else {
                return 0L;
            }
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_HSETNX);
            throw e;
        }
    }

    public Object eval(final @NotBlank String script, int keyCount, String... params) {
        hasTextOf(script, "script");
        MeterUtil.counter(execution_datasource_facade_total, dataSourceName, DataSourceType.REDIS, METHOD_EVAL);
        try {
            final Object result = MeterUtil.timer(execution_datasource_facade_time, dataSourceName, DataSourceType.REDIS,
                    METHOD_EVAL, () -> jedisClient.eval(script, keyCount, params));

            MeterUtil.counter(execution_datasource_facade_success, dataSourceName, DataSourceType.REDIS, METHOD_EVAL);
            return result;
        } catch (Throwable e) {
            MeterUtil.counter(execution_datasource_facade_failure, dataSourceName, DataSourceType.REDIS, METHOD_EVAL);
            throw e;
        }
    }

    @Singleton
    public static class RedisSourceFacadeBuilder implements DataSourceFacadeBuilder {

        @Override
        public DataSourceFacade newInstnace(
                final @NotNull ExecutionConfig config,
                final @NotBlank String dataSourceName,
                final @NotNull DataSourcePropertiesBase dataSourceProperties) {
            notNullOf(config, "properties");
            hasTextOf(dataSourceName, "dataSourceName");

            final RedisDataSourceProperties _config = (RedisDataSourceProperties) dataSourceProperties;
            final JedisClient jedisClient = new JedisClientBuilder(_config.getJedisConfig()).build();
            return new RedisSourceFacade(config, dataSourceName, jedisClient);
        }

        @Override
        public DataSourceType type() {
            return DataSourceType.REDIS;
        }

    }

}
