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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR;

import java.util.List;

import javax.validation.constraints.NotBlank;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
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

    // ----------------------------------------------------------------------------
    // ----- Rengine Common constants. -----
    // ----------------------------------------------------------------------------

    public static final String CONF_PREFIX = "rengine";

    public static final String DEFAULT_MONGODB_DATABASE = getStringProperty("mongodb.database", "rengine");

    public static final String DEFAULT_MINIO_ENDPOINT = "http://localhost:9000";
    public static final String DEFAULT_MINIO_REGION = "us-east-1";
    public static final String DEFAULT_MINIO_BUCKET = "rengine";

    // ----------------------------------------------------------------------------
    // ----- Rengine ApiServer constants. -----
    // ----------------------------------------------------------------------------

    public static final String CONF_PREFIX_APISERVER = CONF_PREFIX + ".apiserver";

    public static final String CACHE_LOGIN_SECRET_PREFIX = "login:secret:";
    public static final String CACHE_LOGIN_OIDC_PREFIX = "login:oidc:";

    public static final String API_V1_USER_BASE_URI = "/v1/user";
    public static final String API_V1_USER_SECURE_URI = "/secure";
    public static final String API_V1_USER_USERINFO_URI = "/userinfo";

    public static final String API_LOGIN_PAGE_PATH = "/#/login";

    // The URI of form submit username and password processing login endpoint.
    // The default as: /login
    public static final String API_LOGIN_PASSWORD_ENDPOINT = "/login/password";

    // The base URI of the start OAuth2 authenticating request.
    // The default as: /oauth2/authorization
    // see:org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
    public static final String API_LOGIN_OAUTH2_ENDPOINT_BASE = "/login/oauth2";

    // The URI of form callback oauth2 processing login base uri.
    // The default as: /login/oauth2/code/*
    // see:org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter#DEFAULT_FILTER_PROCESSES_URI
    // see:org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer#loginProcessingUrl()
    public static final String API_LOGIN_OAUTH2_CALLBACK_ENDPOINT_BASE = "/login/oauth2/callback/*";

    // API base URI dedicated to kubernetes operator internal request
    // processing.
    public static final String API_V1_KUBERNETES_OPERATOR_BASE_URI = "/_/v1/operator";

    // ----------------------------------------------------------------------------
    // ----- Rengine Controller constants. -----
    // ----------------------------------------------------------------------------

    public static final String CONF_PREFIX_CONTROLLER = CONF_PREFIX + ".controller";

    public static final String DEFAULT_CONTROLLER_JAR_TMP_DIR = getStringProperty("script.log.dir",
            JAVA_IO_TMPDIR + "/__rengine_jars");

    //
    // ----- Rengine Executor constants. -----
    //

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

    public static final String DEFAULT_EXECUTOR_SCRIPT_ROOTFS_DIR = getStringProperty("script.rootfs.dir",
            JAVA_IO_TMPDIR + "/__rengine_script_rootfs");
    public static final String DEFAULT_EXECUTOR_SCRIPT_WORKING_DIR = getStringProperty("script.working.dir",
            JAVA_IO_TMPDIR + "/__rengine_script_works");
    public static final String DEFAULT_EXECUTOR_SCRIPT_CACHE_DIR = getStringProperty("script.cache.dir",
            JAVA_IO_TMPDIR + "/__rengine_script_caches");
    public static final String DEFAULT_EXECUTOR_SCRIPT_LOG_DIR = getStringProperty("script.log.dir",
            JAVA_IO_TMPDIR + "/__rengine_script_log");

    public static final String DEFAULT_EXECUTOR_LOGGING_PREFIX = "log";
    public static final int DEFAULT_EXECUTOR_S3_OBJECT_READ_BUFFER = getIntProperty("EXECUTOR_S3_OBJECT_READ_BUFFER", 4 * 1024);
    public static final int DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT = getIntProperty("EXECUTOR_S3_OBJECT_MAX_LIMIT",
            10 * 1024 * 1024);

    // ----------------------------------------------------------------------------
    // ----- Rengine Job constants. -----
    // ----------------------------------------------------------------------------

    public static final String DEFAULT_EVENTBUS_TOPIC = "rengine_event";

    // ----------------------------------------------------------------------------
    // ----- Rengine Eventbus constants. -----
    // ----------------------------------------------------------------------------

    public static final String CONF_PREFIX_EVENTBUS = CONF_PREFIX + ".eventbus";
    public static final String CONF_PREFIX_EVENTBUS_KAFKA = CONF_PREFIX_EVENTBUS + ".kafka";
    public static final String CONF_PREFIX_EVENTBUS_PULSAR = CONF_PREFIX_EVENTBUS + ".pulsar";
    public static final String CONF_PREFIX_EVENTBUS_ROCKETMQ = CONF_PREFIX_EVENTBUS + ".rabbitmq";

    // ----------------------------------------------------------------------------
    // ----- Rengine Client constants. -----
    // ----------------------------------------------------------------------------

    public static final String CONF_PREFIX_CLIENT = CONF_PREFIX + ".client";

    @Getter
    @AllArgsConstructor
    public static enum MongoCollectionDefinition {

        SYS_GLOBAL_SEQUENCES("sys_global_sequences", true),

        SYS_DICTS("sys_dicts", true),

        SYS_TENANTS("sys_tenants", true),

        SYS_USERS("sys_users", true),

        SYS_USER_ROLES("sys_user_roles", true),

        SYS_ROLES("sys_roles", true),

        SYS_MENU_ROLES("sys_menu_roles", true),

        SYS_MENUS("sys_menus", true),

        SYS_IDENTITY_PROVIDERS("sys_identity_providers", true),

        SYS_NOTIFICATIONS("sys_notifications", true),

        SYS_UPLOADS("sys_uploads", true),

        RE_SCENESES("re_sceneses", true),

        RE_WORKFLOWS("re_workflows", true),

        RE_WORKFLOW_GRAPHS("re_workflow_graphs", true),

        RE_RULES("re_rules", true),

        RE_RULE_SCRIPTS("re_rule_scripts", true),

        RE_DATASOURCES("re_datasources", true),

        RE_CONTROLLER_SCHEDULE("re_controllers", true),

        RE_CONTROLLER_LOG("re_controller_logs", true);

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

    /**
     * This helper class is used to enable various resource key names to support
     * multi-tenant isolation. For example, in the internal deployment
     * environment of an enterprise, the possible requirement is to deploy two
     * tenants, but the external middleware redis cluster, mongo cluster, and
     * minio cluster that the rengine controller and rengine executor services
     * depend on are actually the same shared instance. We should allow this,
     * but should make sure that the data happens without conflicts. Therefore,
     * we need to uniformly increase the tenant identifier prefix to solve this
     * problem.
     */
    public static interface TenantedHolder {

        public static final String CURRENT_TENANT_ID = getStringProperty("tenant.id", "0");

        public static String getColonKey(@NotBlank String key) {
            hasTextOf(key, "key");
            return transformKey(key, ":");
        }

        public static String getSlashKey(@NotBlank String key) {
            hasTextOf(key, "key");
            return transformKey(key, "/");
        }

        public static String getUnderlineKey(@NotBlank String key) {
            hasTextOf(key, "key");
            return transformKey(key, "_");
        }

        @VisibleForTesting
        static String transformKey(@NotBlank String key, @NotBlank String delimiter) {
            hasTextOf(key, "key");
            hasTextOf(delimiter, "delimiter");
            final List<String> parts = Lists.newArrayList(split(key, delimiter));
            if (parts.isEmpty()) {
                throw new IllegalArgumentException(format("invalid the key '%s'", key));
            } else if (parts.size() == 1) {
                parts.add(CURRENT_TENANT_ID);
            } else {
                parts.add(1, CURRENT_TENANT_ID);
            }
            return join(parts.toArray(new String[0]), delimiter);
        }
    }

}
