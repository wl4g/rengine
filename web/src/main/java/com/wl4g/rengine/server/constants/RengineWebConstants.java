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
package com.wl4g.rengine.server.constants;

import com.wl4g.rengine.common.constant.RengineConstants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link RengineWebConstants}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
public abstract class RengineWebConstants extends RengineConstants {

    public static final String CONF_PREFIX = "spring.rengine";
    public static final String CONF_MINIO_PREFIX = CONF_PREFIX + ".minio";

    @Getter
    @AllArgsConstructor
    public static enum MongoCollectionDefinition {

        PROJECT("project"),

        WORKFLOW("workflow"),

        RULE("rule"),

        USER_LIBRARY("user_library"),

        TEST_DATASET("test_dataset");

        private final String name;
    }

}
