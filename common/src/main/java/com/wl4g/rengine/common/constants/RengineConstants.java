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
package com.wl4g.rengine.common.constants;

import com.wl4g.infra.common.lang.EnvironmentUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link RengineConstants}
 * 
 * @author James Wong
 * @version 2022-09-08
 * @since v3.0.0
 */
public abstract class RengineConstants extends EnvironmentUtil {

    public static final String CONF_PREFIX = "spring.rengine";
    public static final String CONF_PREFIX_EVENTBUS = CONF_PREFIX + ".eventbus";
    public static final String CONF_PREFIX_EVENTBUS_KAFKA = CONF_PREFIX_EVENTBUS + ".kafka";
    public static final String CONF_PREFIX_EVENTBUS_PULSAR = CONF_PREFIX_EVENTBUS + ".pulsar";
    public static final String CONF_PREFIX_EVENTBUS_ROCKETMQ = CONF_PREFIX_EVENTBUS + ".rabbitmq";

    public static final String DEF_EVENTBUS_TOPIC = "rengine_event";

    public static final String DEF_MINIO_ENDPOINT = "http://localhost:9000";
    public static final String DEF_MINIO_REGION = "us-east-1";
    public static final String DEF_MINIO_BUCKET = "rengine";

    @Getter
    @AllArgsConstructor
    public static enum MongoCollectionDefinition {

        PROJECTS("projects"),

        WORKFLOWS("workflows"),

        RULES("rules"),

        UPLOADS("uploads"),

        JOBS("jobs"),

        SYS_NOTIFICATION_CONFIG("sys_notification_config"),

        SYS_IDP_CONFIG("sys_idp_config");

        private final String name;
    }

}
