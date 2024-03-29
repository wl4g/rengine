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
package com.wl4g;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;

import com.wl4g.infra.support.cache.jedis.JedisClientAutoConfiguration;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link RengineScheduler}
 * 
 * @author James Wong
 * @date 2022-10-16
 * @since v1.0.0
 */
@Slf4j
@SpringBootApplication(
        exclude = { JdbcTemplateAutoConfiguration.class, DataSourceAutoConfiguration.class, JedisClientAutoConfiguration.class })
public class RengineScheduler {

    // Private are not accessible, can only be checked using the class-name.
    public static final String SILENTEXITEXCEPTION_CLASS = "org.springframework.boot.devtools.restart.SilentExitExceptionHandler$SilentExitException";

    public static void main(String[] args) {
        try {
            SpringApplication.run(RengineScheduler.class, args);
        } catch (Exception e) {
            if (e.getClass().getName().equals(SILENTEXITEXCEPTION_CLASS)) {
                // issue-see:https://stackoverflow.com/questions/32770884/breakpoint-at-throw-new-silentexitexception-in-eclipse-spring-boot
                // System.setProperty("spring.devtools.restart.enabled","false");
                log.warn("SilentExitException exception occurred. This is a known issue that "
                        + "usually only occurs on development eclipse, but it may not affect "
                        + "configuration hot reloading, you can verify this and ignore it.");
            } else {
                throw e;
            }
        }
    }

}
