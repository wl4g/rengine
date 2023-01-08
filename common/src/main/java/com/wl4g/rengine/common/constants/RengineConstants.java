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
package com.wl4g.rengine.common.constants;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;

import com.wl4g.infra.common.lang.EnvironmentUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link RengineConstants}
 * 
 * @author James Wong
 * @version 2022-09-08
 * @since v1.0.0
 */
public abstract class RengineConstants extends EnvironmentUtil {

    //
    // Basic definitions.
    //

    public static final String CONF_PREFIX = "rengine";

    //
    // ApiServer definitions.
    //

    public static final String CONF_PREFIX_APISERVER = CONF_PREFIX + ".apiserver";
    public static final String DEFAULT_MONGODB_DATABASE = getStringProperty("mongodb.database", "rengine");
    public static final String DEFAULT_MINIO_ENDPOINT = "http://localhost:9000";
    public static final String DEFAULT_MINIO_REGION = "us-east-1";
    public static final String DEFAULT_MINIO_BUCKET = "rengine";

    //
    // Scheduler definitions.
    //

    public static final String CONF_PREFIX_SCHEDULER = CONF_PREFIX + ".scheduler";

    //
    // Executor definitions.
    //

    public static final String CONF_PREFIX_EXECUTOR = CONF_PREFIX + ".executor";
    public static final String API_EXECUTOR_EXECUTE_BASE = "/execution";
    // Receive execution request from client SDK. For example, a request from a
    // business application JVM process via a dependent client SDK.
    public static final String API_EXECUTOR_EXECUTE = "/execute";
    // Receive execution requests from arbitrary external systems. For example:
    // the request sent when the pushes(or webhook) subscription events from
    // WeChat, Dingtalk and other social platforms servers.
    // This API is very useful, such as realizing chat interaction with WeChat
    // official account or Dingding robot.
    public static final String API_EXECUTOR_EXECUTE_WRAPPER = "/execute-wrapper";
    // Notice: The handcode entrypoint function is 'process'
    public static final String DEFAULT_EXECUTOR_MAIN_FUNCTION = "process";
    public static final String DEFAULT_EXECUTOR_TMP_SCRIPT_CACHE_DIR = "/tmp/__rengine_script_caches";
    public static final String DEFAULT_EXECUTOR_LOGGING_PREFIX = "log";
    public static final int DEFAULT_EXECUTOR_S3_OBJECT_READ_BUFFER = getIntProperty("EXECUTOR_S3_OBJECT_READ_BUFFER", 4 * 1024);
    public static final int DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT = getIntProperty("EXECUTOR_S3_OBJECT_MAX_LIMIT",
            10 * 1024 * 1024);

    //
    // Eventbus definitions.
    //

    public static final String CONF_PREFIX_EVENTBUS = CONF_PREFIX + ".eventbus";
    public static final String CONF_PREFIX_EVENTBUS_KAFKA = CONF_PREFIX_EVENTBUS + ".kafka";
    public static final String CONF_PREFIX_EVENTBUS_PULSAR = CONF_PREFIX_EVENTBUS + ".pulsar";
    public static final String CONF_PREFIX_EVENTBUS_ROCKETMQ = CONF_PREFIX_EVENTBUS + ".rabbitmq";

    //
    // Client definitions.
    //

    public static final String CONF_PREFIX_CLIENT = CONF_PREFIX + ".client";

    //
    // Jobs definitions.
    //

    public static final String DEFAULT_EVENTBUS_TOPIC = "rengine_event";

    @Getter
    @AllArgsConstructor
    public static enum MongoCollectionDefinition {

        SYS_IDP_CONFIG("sys_idp_config", true),

        SYS_NOTIFICATION_CONFIG("sys_notification_config", true),

        SCENESES("t_sceneses", true),

        WORKFLOWS("t_workflows", true),

        WORKFLOW_GRAPHS("t_workflow_graphs", true),

        RULES("t_rules", true),

        RULE_SCRIPTS("t_rule_scripts", true),

        UPLOADS("t_uploads", true),

        DATASOURCES("t_datasources", true),

        SCHEDULING_TRIGGER("t_scheduling_triggers", true),

        SCHEDULING_JOBS("t_scheduling_jobs", true),

        AGGREGATES("t_aggregates", true);

        private final String name;
        private final boolean isWriteConcernSafe;

        public static MongoCollectionDefinition of(String type) {
            for (MongoCollectionDefinition a : values()) {
                if (equalsAnyIgnoreCase(type, a.name(), a.getName())) {
                    return a;
                }
            }
            throw new IllegalArgumentException(format("Invalid Mongo collection type for '%s'", type));
        }

    }

}
