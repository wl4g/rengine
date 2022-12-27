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
package com.wl4g.rengine.evaluator.util;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link LogUncaughtExceptionHandler}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 */
public class LogUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(LogUncaughtExceptionHandler.class);

    @Override
    public void uncaughtException(final @NotNull Thread thread, final @NotNull Throwable throwable) {
        log.error("Uncaught exception in thread '{}'.", thread.getName(), throwable);
    }

}