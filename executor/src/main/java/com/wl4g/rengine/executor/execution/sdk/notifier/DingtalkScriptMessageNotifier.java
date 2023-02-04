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

import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_failure;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_success;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_time;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_total;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Singleton;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.AccessToken;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.AccessTokenResult;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.CreateSceneGroupV2;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.CreateSceneGroupV2Result;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.RobotGroupMessagesSend;
import com.wl4g.infra.common.serialize.JacksonUtils;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.entity.Notification.DingtalkConfig;
import com.wl4g.rengine.executor.meter.MeterUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link DingtalkScriptMessageNotifier}
 * 
 * @author James Wong
 * @version 2023-01-06
 * @since v1.0.0
 */
@Getter
@Setter
@Singleton
public class DingtalkScriptMessageNotifier implements ScriptMessageNotifier {

    final static String METHOD_CREATESCENEGROUPV2 = "createSceneGroupV2";

    volatile RefreshedInfo refreshed;

    @Override
    public NotifierKind kind() {
        return NotifierKind.DINGTALK;
    }

    @Override
    public Object send(final @NotEmpty Map<String, Object> parameter) {
        notEmptyOf(parameter, "parameter");
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_SEND);
            return MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_SEND, () -> {
                // final DingtalkConfig config = loadCurrentDingtalkConfig();

                // Load the currently refreshed valid accessToken.
                final String accessToken = getRequiredRefreshed().getAccessToken();

                // Make request parameters.
                final RobotGroupMessagesSend request = JacksonUtils.convertBean(parameter, RobotGroupMessagesSend.class)
                        .validate();

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_SEND);
                return DingtalkAPI.getInstance().sendRobotGroupMessages(accessToken, request);
            });
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_SEND);
            throw e;
        }
    }

    public CreateSceneGroupV2Result createSceneGroupV2(final @NotEmpty Map<String, Object> parameter) {
        notEmptyOf(parameter, "parameter");
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_CREATESCENEGROUPV2);
            return MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_CREATESCENEGROUPV2, () -> {
                // final DingtalkConfig config = loadCurrentDingtalkConfig();

                // Load the currently refreshed valid accessToken.
                final String accessToken = getRequiredRefreshed().getAccessToken();

                // Make request parameters.
                final CreateSceneGroupV2 request = JacksonUtils.convertBean(parameter, CreateSceneGroupV2.class).validate();

                // Sets optional defaults.
                if (isBlank(request.getUuid())) {
                    request.setUuid(UUID.randomUUID().toString());
                }

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_CREATESCENEGROUPV2);
                return DingtalkAPI.getInstance().createSceneGroupV2(accessToken, request);
            });
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_CREATESCENEGROUPV2);
            throw e;
        }
    }

    public Map<String, String> processCallback(
            final @NotEmpty Map<String, String> parameter,
            final @NotNull Consumer<JsonNode> process) {
        notEmptyOf(parameter, "parameter");
        final String signature = parameter.get(PARAM_SIGNATURE);
        final String timestamp = parameter.get(PARAM_TIMESTAMP);
        final String nonce = parameter.get(PARAM_NONCE);
        final String bodyJson = parameter.get(PARAM_BODY);

        final DingtalkConfig config = currentDingtalkConfig().validate(true);
        return DingtalkAPI.getInstance()
                .processCallback(config.getToken(), config.getAesKey(), config.getCorpId(), signature, timestamp, nonce, bodyJson,
                        process);
    }

    private DingtalkConfig currentDingtalkConfig() {
        // Load current effective dingtalk config.
        final DingtalkConfig config = parseJSON((String) getRequiredRefreshed().getAttributes().get(KEY_DINGTALK_CONFIG),
                DingtalkConfig.class);
        notNull(config,
                "Internal error! Please check the redis cache configuration data, email config json is required. refreshed: %s",
                getRefreshed());
        return config;
    }

    @Override
    public void update(@NotNull RefreshedInfo refreshed) {
        notNullOf(refreshed, "refreshed");
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_UPDATE);
            MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_UPDATE, () -> {
                ScriptMessageNotifier.super.update(refreshed);
                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_UPDATE);
                return null;
            });
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_UPDATE);
            throw e;
        }
    }

    @Override
    public RefreshedInfo refresh(@NotNull Notification notification) {
        notNullOf(notification, "notification");
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_REFRESH);
            return MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_REFRESH, () -> {
                final DingtalkConfig config = (DingtalkConfig) notification.getProperties();
                final AccessTokenResult result = DingtalkAPI.getInstance()
                        .getAccessToken(
                                AccessToken.builder().appKey(config.getAppKey()).appSecret(config.getAppSecret()).build());

                // Sets default configuration properties.
                final Map<String, Object> attributes = new HashMap<>();
                attributes.put(KEY_DINGTALK_CONFIG, toJSONString(config));

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_REFRESH);
                return RefreshedInfo.builder()
                        .notifierType(kind())
                        .appKey(config.getAppKey())
                        // .appSecret(config.getAppSecret())
                        .accessToken(result.getAccessToken())
                        .expireSeconds(result.getExpireIn())
                        .attributes(attributes)
                        .build();
            });
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_REFRESH);
            throw e;
        }
    }

    public static final String KEY_DINGTALK_CONFIG = DingtalkConfig.class.getName();
    public static final String PARAM_SIGNATURE = "signature";
    public static final String PARAM_TIMESTAMP = "timestamp";
    public static final String PARAM_NONCE = "nonce";
    public static final String PARAM_BODY = "body";
}
