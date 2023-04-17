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
package com.wl4g.rengine.executor.context;

import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.wl4g.infra.common.web.rest.RespBase;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * {@linkplain @ApplicationScoped} vs {@linkplain @Singleton} see:
 * https://quarkus.io/guides/cdi#applicationscoped-and-singleton-look-very-similar-which-one-should-i-choose-for-my-quarkus-application
 * 
 * {@linkplain @Singleton}: Better performance because there is no client proxy.
 * 
 * @author James Wong
 * @date 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/lifecycle#listening-for-startup-and-shutdown-events
 */
@Slf4j
@Singleton
public class ExecutorApplicationInitializer {

    @Inject
    RedisDataSource redisDS;

    void onStart(@Observes StartupEvent event, @ConfigProperty(name = "quarkus.application.name") String appName) {
        log.info("{} is starting ...", appName);
        RespBase.ErrorPromptMessageBuilder.setPromptDefault(appName);
        immdiatelyInitializeRedisDS();
    }

    void onStop(@Observes ShutdownEvent event) {
        log.info("The application is stopping...");
    }

    void immdiatelyInitializeRedisDS() {
        log.info("Immediately initializing redis dataSource ...");
        redisDS.string(String.class).set("NONE", valueOf(currentTimeMillis()));
        final var initTime = redisDS.string(String.class).get("NONE");
        log.info("Initialized redis dataSource for : {}", initTime);
    }

}