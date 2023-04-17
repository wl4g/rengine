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
package com.wl4g.rengine.executor.execution.sdk.notifier;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.notification.MessageNotifier;
import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.rengine.common.entity.sys.Notification;

import io.vertx.core.Vertx;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ScriptMessageNotifier}
 * 
 * @author James Wong
 * @date 2023-01-06
 * @since v1.0.0
 */
public interface ScriptMessageNotifier {

    MessageNotifier.NotifierKind kind();

    @HostAccess.Export
    Object send(final @NotEmpty Map<String, Object> parameter);

    RefreshedInfo getRefreshed();

    void setRefreshed(RefreshedInfo refreshed);

    default RefreshedInfo getRequiredRefreshed() {
        return notNull(getRefreshed(),
                "Internal error! Should not be here, the current local cached refreshed is null, it should have been initialized before calling the notifier sending method.");
    }

    default void update(@NotNull RefreshedInfo refreshed, @NotNull Vertx vertx) {
        notNull(refreshed, "Internal error! The setup current refreshed is required.");
        notNullOf(vertx, "vertx");
        setRefreshed(refreshed);
    }

    RefreshedInfo refresh(@NotNull Notification notification);

    default String getStringParam(@NotEmpty Map<String, Object> parameter, @NotBlank String paramName, boolean required) {
        final Object paramObj = parameter.get(hasTextOf(paramName, "paramName"));
        if (required && (isNull(paramObj) || isBlank(paramObj.toString()))) {
            throw new IllegalArgumentException(format("parameter['%s'] is required", paramName));
        }
        if (nonNull(paramObj)) {
            return paramObj.toString();
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    default List<String> getArrayParam(@NotEmpty Map<String, Object> parameter, @NotBlank String paramName, boolean required) {
        final Object paramObj = parameter.get(hasTextOf(paramName, "paramName"));
        if (required && (isNull(paramObj) || isBlank(paramObj.toString()))) {
            throw new IllegalArgumentException(format("parameter['%s'] is required", paramName));
        }
        if (nonNull(paramObj)) {
            List<String> params = emptyList();
            if (paramObj instanceof List) {
                params = (List<String>) paramObj;
            } else if (paramObj instanceof String) {
                params = asList(split((String) paramObj, ","));
            } else {
                throw new UnsupportedOperationException(format(
                        "Unsupported %s parameter type, please check whether the parameters are correct, "
                                + "only arrays or comma-separated strings are supported. %s: %s",
                        paramName, paramName, paramObj));
            }
            if (required) {
                Assert2.notEmpty(params, format("parameter['%s']", paramName));
            }
            return params;
        }
        return emptyList();
    }

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @ToString
    public static class RefreshedInfo {
        NotifierKind notifierType;

        String appKey;
        // String appSecret;

        String accessToken;

        Integer expireSeconds;

        Integer effectiveExpireSeconds;

        @Default
        Map<String, Object> attributes = new HashMap<>();

        public RefreshedInfo validate() {
            hasTextOf(accessToken, "acccessToken");
            notNullOf(expireSeconds, "expireSeconds");
            return this;
        }
    }

    final static String METHOD_SEND = "send";
    final static String METHOD_UPDATE = "update";
    final static String METHOD_REFRESH = "refresh";
}
