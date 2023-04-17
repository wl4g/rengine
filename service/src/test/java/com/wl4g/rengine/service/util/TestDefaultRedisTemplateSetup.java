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
package com.wl4g.rengine.service.util;

import static java.util.Arrays.asList;

import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * {@link TestDefaultRedisTemplateSetup}
 * 
 * @author James Wong
 * @date 2023-02-04
 * @since v1.0.0
 */
public class TestDefaultRedisTemplateSetup {

    public static RedisTemplate<String, String> createRedisTemplate() {
        final RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(createLettuceConnectionFactory());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public static RedisTemplate<String, byte[]> createRedisTemplateWithByteArray() {
        final RedisTemplate<String, byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(createLettuceConnectionFactory());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public static LettuceConnectionFactory createLettuceConnectionFactory() {
        final RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(
                asList("localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381"));
        clusterConfiguration.setPassword("zzx!@#$%");

        final LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(clusterConfiguration);
        connectionFactory.afterPropertiesSet();

        return connectionFactory;
    }

}
