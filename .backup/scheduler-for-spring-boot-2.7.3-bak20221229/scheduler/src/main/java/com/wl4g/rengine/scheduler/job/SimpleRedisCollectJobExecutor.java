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
package com.wl4g.rengine.scheduler.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.wl4g.infra.common.jedis.JedisClient;
import com.wl4g.infra.support.cache.jedis.JedisClientAutoConfiguration.JedisProperties;
import com.wl4g.infra.support.cache.jedis.JedisClientFactoryBean;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link SimpleRedisCollectJobExecutor}
 * 
 * @author James Wong
 * @version 2022-10-26
 * @since v3.0.0
 */
public class SimpleRedisCollectJobExecutor extends CollectJobExecutor<SimpleRedisCollectJobExecutor.SimpleRedisJobParam>
        implements Closeable {

    private Map<String, JedisClient> jedisClientCaches;

    @Override
    protected EventJobType type() {
        return EventJobType.SIMPLE_REDIS;
    }

    @Override
    protected void execute(
            SimpleRedisJobParam shardingParam,
            int currentShardingTotalCount,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) throws Exception {

        JedisClient jedisClient = obtainShardingJedisClient(shardingParam, currentShardingTotalCount, jobConfig, context);

        List<String> luaKeys = resolveVariables(shardingParam.getLuaKeys());
        List<String> luaArgs = resolveVariables(shardingParam.getLuaArgs());
        Object result = jedisClient.eval(shardingParam.getLuaScript(), luaKeys, luaArgs);

        log.debug("Collect to result: {}", result);
        offer(shardingParam, jobConfig, jobFacade, context, result);
    }

    @Override
    protected BodyConverter getBodyConverter(
            SimpleRedisJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        return BodyConverter.DEFAULT_STRING;
    }

    @Override
    protected EventLocation getEventLocation(
            SimpleRedisJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // TODO setup all nodes to ipAddress?
        String ipAddress = safeList(shardingParam.getJedisConfig().getNodes()).stream().findFirst().orElse(null);
        return EventLocation.builder().ipAddress(ipAddress).build();
    }

    protected JedisClient obtainShardingJedisClient(
            SimpleRedisJobParam shardingParam,
            int currentShardingTotalCount,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) throws Exception {

        if (isNull(jedisClientCaches)) {
            synchronized (this) {
                if (isNull(jedisClientCaches)) {
                    jedisClientCaches = new ConcurrentHashMap<>(currentShardingTotalCount);
                }
            }
        }

        JedisClient jedisClient = jedisClientCaches.get(shardingParam.getName());
        if (isNull(jedisClient)) {
            synchronized (this) {
                jedisClient = jedisClientCaches.get(shardingParam.getName());
                if (isNull(jedisClient)) {
                    JedisClientFactoryBean factoryBean = new JedisClientFactoryBean(shardingParam.getJedisConfig());
                    factoryBean.afterPropertiesSet();
                    jedisClientCaches.put(shardingParam.getName(), (jedisClient = factoryBean.getObject()));
                }
            }
        }

        return jedisClient;
    }

    @Override
    public void close() throws IOException {
        if (nonNull(jedisClientCaches)) {
            log.info("Closing to target jedis clients for : {}", jedisClientCaches.keySet());
            jedisClientCaches.forEach((name, jedisClient) -> {
                try {
                    jedisClient.close();
                } catch (Exception e) {
                    log.warn(format("Unable to closing target redis. - %s", name), e);
                }
            });
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleRedisJobParam extends CollectJobExecutor.JobParamBase {
        private @NotNull JedisProperties jedisConfig = new JedisProperties();
        private @NotBlank String luaScript;
        private @NotEmpty List<String> luaKeys = new ArrayList<>();
        private @NotEmpty List<String> luaArgs = new ArrayList<>();
    }

}
