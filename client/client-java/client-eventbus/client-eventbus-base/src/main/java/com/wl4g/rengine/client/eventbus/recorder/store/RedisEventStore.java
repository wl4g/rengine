/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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
package com.wl4g.rengine.client.eventbus.recorder.store;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.cache.jedis.JedisClientBuilder;
import com.wl4g.infra.common.cache.jedis.JedisService;
import com.wl4g.infra.common.cache.jedis.ScanCursor;
import com.wl4g.rengine.client.eventbus.config.ClientEventBusConfig;

import lombok.Getter;

/**
 * {@link RedisRecordCache}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-12 v3.0.0
 * @since v1.0.0
 */
@Getter
public class RedisEventStore implements EventStore, Closeable {
    private final ClientEventBusConfig config;
    private final JedisService jedisService;
    private final Class<?> valueClass;

    public RedisEventStore(@NotNull ClientEventBusConfig config, Class<?> valueClass, @NotBlank String name) {
        this.config = notNullOf(config, "config");
        this.valueClass = notNullOf(valueClass, "valueClass");
        this.jedisService = new JedisService(new JedisClientBuilder(config.getStore().getRedis()).build());
    }

    @Override
    public Object getOriginalCache() {
        return jedisService;
    }

    @Override
    public Iterator<Entry<String, Object>> iterator() {
        return new ScanCursor<>(jedisService.getJedisClient(), valueClass);
    }

    @Override
    public Object get(String key) {
        return jedisService.getObjectAsJson(key, valueClass);
    }

    @Override
    public Boolean put(String key, Object value) {
        jedisService.setObjectAsJson(key, value, Integer.MAX_VALUE);
        return true;
    }

    @Override
    public Long remove(String key) {
        return jedisService.del(key);
    }

    @Override
    public Boolean removeAll() {
        // TODO
        return false;
    }

    @Override
    public Long size() {
        // TODO
        return 0L;
    }

    @Override
    public Boolean cleanUp() {
        // TODO
        return false;
    }

    @Override
    public void close() throws IOException {
        if (nonNull(jedisService)) {
            jedisService.getJedisClient().close();
        }
    }

}
