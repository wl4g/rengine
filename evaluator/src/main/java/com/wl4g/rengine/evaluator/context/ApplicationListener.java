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
package com.wl4g.rengine.evaluator.context;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ApplicationListener}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v3.0.0
 * @see https://quarkus.io/guides/lifecycle#listening-for-startup-and-shutdown-events
 */
@Slf4j
@ApplicationScoped
public class ApplicationListener {

    void onStart(@Observes StartupEvent event) {
        log.info("The application is starting...");
    }

    void onStop(@Observes ShutdownEvent event) {
        log.info("The application is stopping...");
    }

}