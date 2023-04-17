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

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;

/**
 * {@link MongoUserDetailsManagerTests}
 * 
 * @author James Wong
 * @date 2023-03-11
 * @since v1.0.0
 */
public class MongoUserDetailsManagerTests {

    static MongoUserDetailsManager userDetailsManager;
    static AuthenticationService authenticationService;

    @BeforeClass
    public static void init() {
        authenticationService = AuthenticationServiceTests.getAuthenticationService();
        userDetailsManager = new MongoUserDetailsManager(authenticationService.getConfig(),
                new BCryptPasswordEncoder(BCryptVersion.$2Y, 13), authenticationService, null);
    }

    @Test
    public void testLoadUserByUsername() {
        final var user = userDetailsManager.loadUserByUsername("root");
        System.out.println(toJSONString(user, true));
    }

}
