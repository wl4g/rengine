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
package com.wl4g.rengine.service.security.user;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;

import com.wl4g.rengine.service.security.RengineWebSecurityProperties;
import com.wl4g.rengine.service.util.TestDefaultMongoSetup;
import com.wl4g.rengine.service.util.TestDefaultRedisTemplateSetup;

import lombok.Getter;

/**
 * {@link AuthenticationServiceTests}
 * 
 * @author James Wong
 * @version 2023-03-17
 * @since v1.0.0
 */
@Getter
public class AuthenticationServiceTests {

    static RengineWebSecurityProperties config;
    static MongoUserDetailsManager userDetailsManager;
    static MongoTemplate mongoTemplate;
    static RedisTemplate<String, byte[]> redisTemplate;

    public static AuthenticationService getAuthenticationService() {
        config = new RengineWebSecurityProperties();
        mongoTemplate = TestDefaultMongoSetup.createMongoTemplate();
        redisTemplate = TestDefaultRedisTemplateSetup.createRedisTemplateWithByteArray();
        return new AuthenticationService(config, redisTemplate, mongoTemplate);
    }

}
