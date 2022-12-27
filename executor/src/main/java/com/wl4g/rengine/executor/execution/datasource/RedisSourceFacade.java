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
package com.wl4g.rengine.executor.execution.datasource;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseToNode;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
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
        return parseToNode(jedisClient.get(key), null);
    }

    public void set(final @NotBlank String key, final Object value) {
        hasTextOf(key, "key");
        if (nonNull(value)) {
            jedisClient.set(key, toJSONString(value));
        }
    }

    public void setex(final @NotBlank String key, final Object value, final @Min(-2) long seconds) {
        hasTextOf(key, "key");
        if (nonNull(value)) {
            jedisClient.setex(key, seconds, toJSONString(value));
        }
    }

    public void setnx(final @NotBlank String key, final Object value) {
        setnxex(key, value, Long.MAX_VALUE);
    }

    public void setnxex(final @NotBlank String key, final Object value, final @Min(-2) long seconds) {
        hasTextOf(key, "key");
        if (nonNull(value)) {
            jedisClient.setnx(key, toJSONString(value));
            if (seconds < Long.MAX_VALUE) {
                jedisClient.expire(key, seconds);
            }
        }
    }

    public void del(final @NotBlank String key) {
        hasTextOf(key, "key");
        jedisClient.del(key);
    }

    public void expire(final @NotBlank String key, final @Min(-2) long seconds) {
        hasTextOf(key, "key");
        jedisClient.expire(key, seconds);
    }

    public Map<String, JsonNode> hgetAll(final @NotBlank String key) {
        hasTextOf(key, "key");
        return safeMap(jedisClient.hgetAll(key)).entrySet()
                .stream()
                .collect(toMap(e -> e.getKey(), e -> parseToNode(e.getValue(), null)));
    }

    public JsonNode hget(final @NotBlank String key, final @NotBlank String field) {
        hasTextOf(key, "key");
        hasTextOf(field, "field");
        return parseToNode(jedisClient.hget(key, field), null);
    }

    public void hset(final @NotBlank String key, final @NotBlank String field, final Object value) {
        hasTextOf(key, "key");
        hasTextOf(field, "field");
        if (nonNull(value)) {
            jedisClient.hset(key, field, toJSONString(value));
        }
    }

    public void hsetnx(final @NotBlank String key, final @NotBlank String field, final Object value) {
        hasTextOf(key, "key");
        hasTextOf(field, "field");
        if (nonNull(value)) {
            jedisClient.hsetnx(key, field, toJSONString(value));
        }
    }

    public Object eval(final @NotBlank String script, int keyCount, String... params) {
        hasTextOf(script, "script");
        return jedisClient.eval(script, keyCount, params);
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
