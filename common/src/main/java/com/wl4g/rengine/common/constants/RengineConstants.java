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
import static java.util.Objects.nonNull;
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

    // ----- Common definitions. -----

    public static final String CONF_PREFIX = "rengine";

    public static final String DEFAULT_MONGODB_DATABASE = getStringProperty("mongodb.database", "rengine");
    public static final String DEFAULT_MINIO_ENDPOINT = "http://localhost:9000";
    public static final String DEFAULT_MINIO_REGION = "us-east-1";
    public static final String DEFAULT_MINIO_BUCKET = "rengine";

    // ----- ApiServer definitions. -----

    public static final String CONF_PREFIX_APISERVER = CONF_PREFIX + ".apiserver";

    // The URI of form submit username and password processing login endpoint.
    // The default as: /login
    public static final String DEFAULT_SECURITY_PASSWORD_ENDPOINT_URI = "/login/password";

    // The base URI of the start OAuth2 authenticating request.
    // The default as: /oauth2/authorization
    // see:org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
    public static final String DEFAULT_SECURITY_OAUTH2_ENDPOINT_BASE_URI = "/login/oauth2";

    // The URI of form callback oauth2 processing login base uri.
    // The default as: /login/oauth2/code/*
    // see:org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter#DEFAULT_FILTER_PROCESSES_URI
    // see:org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer#loginProcessingUrl()
    public static final String DEFAULT_SECURITY_OAUTH2_CALLBACK_BASE_URI = "/login/oauth2/callback/*";

    // ----- Controller definitions. -----

    public static final String CONF_PREFIX_CONTROLLER = CONF_PREFIX + ".controller";

    // ----- Executor definitions. -----

    public static final String CONF_PREFIX_EXECUTOR = CONF_PREFIX + ".executor";

    public static final String API_EXECUTOR_EXECUTE_BASE = "/execute";

    // This API is only used to directly execute rule scripts for testing when
    // developing in the management console. The underlying implementation is
    // still the API for calling the workflow executor.
    public static final String API_EXECUTOR_EXECUTE_INTERNAL_RULESCRIPT = "/internal/rulescript";

    // Receive execution request internal from client SDK. For example, a
    // request from a business application JVM process via a depends client SDK.
    public static final String API_EXECUTOR_EXECUTE_INTERNAL_WORKFLOW = "/internal/workflow";

    // Receive execution requests custom from arbitrary external systems. For
    // example: the request sent when the pushes(or webhook) subscription events
    // from WeChat, Dingtalk and other social platforms servers.
    // This API is very useful, such as realizing chat interaction with WeChat
    // official account or Dingding robot.
    public static final String API_EXECUTOR_EXECUTE_CUSTOM = "/custom";

    // Notice: The handcode entrypoint function is 'process'
    public static final String DEFAULT_EXECUTOR_MAIN_FUNCTION = "process";
    public static final String DEFAULT_EXECUTOR_SCRIPT_TMP_CACHE_DIR = "/tmp/__rengine_script_caches";
    public static final String DEFAULT_EXECUTOR_SCRIPT_LOG_BASE_DIR = "/tmp/__rengine_script_log";
    public static final String DEFAULT_EXECUTOR_LOGGING_PREFIX = "log";
    public static final int DEFAULT_EXECUTOR_S3_OBJECT_READ_BUFFER = getIntProperty("EXECUTOR_S3_OBJECT_READ_BUFFER", 4 * 1024);
    public static final int DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT = getIntProperty("EXECUTOR_S3_OBJECT_MAX_LIMIT",
            10 * 1024 * 1024);

    // ----- Client definitions. -----

    public static final String CONF_PREFIX_CLIENT = CONF_PREFIX + ".client";

    // ----- Job definitions. -----

    public static final String DEFAULT_EVENTBUS_TOPIC = "rengine_event";

    // ----- Eventbus definitions. -----

    public static final String CONF_PREFIX_EVENTBUS = CONF_PREFIX + ".eventbus";
    public static final String CONF_PREFIX_EVENTBUS_KAFKA = CONF_PREFIX_EVENTBUS + ".kafka";
    public static final String CONF_PREFIX_EVENTBUS_PULSAR = CONF_PREFIX_EVENTBUS + ".pulsar";
    public static final String CONF_PREFIX_EVENTBUS_ROCKETMQ = CONF_PREFIX_EVENTBUS + ".rabbitmq";

    @Getter
    @AllArgsConstructor
    public static enum MongoCollectionDefinition {

        SYS_DICTS("sys_dicts", true),

        SYS_USERS("sys_users", true),

        SYS_ORGS("sys_orgs", true),

        SYS_IDENTITY_PROVIDERS("sys_identity_providers", true),

        SYS_NOTIFICATIONS("sys_notifications", true),

        T_SCENESES("t_sceneses", true),

        T_WORKFLOWS("t_workflows", true),

        T_WORKFLOW_GRAPHS("t_workflow_graphs", true),

        T_RULES("t_rules", true),

        T_RULE_SCRIPTS("t_rule_scripts", true),

        T_UPLOADS("t_uploads", true),

        T_DATASOURCES("t_datasources", true),

        T_CONTROLLER_SCHEDULE("t_controller_schedules", true),

        T_CONTROLLER_LOG("t_controller_logs", true),

        R_AGGREGATES("r_aggregates", true);

        private final String name;
        private final boolean isWriteConcernSafe;

        public static MongoCollectionDefinition of(String type) {
            final MongoCollectionDefinition collection = safeOf(type);
            if (nonNull(collection)) {
                return collection;
            }
            throw new IllegalArgumentException(format("Invalid Mongo collection type for '%s'", type));
        }

        public static MongoCollectionDefinition safeOf(String type) {
            for (MongoCollectionDefinition a : values()) {
                if (equalsAnyIgnoreCase(type, a.name(), a.getName())) {
                    return a;
                }
            }
            return null;
        }

    }

}
