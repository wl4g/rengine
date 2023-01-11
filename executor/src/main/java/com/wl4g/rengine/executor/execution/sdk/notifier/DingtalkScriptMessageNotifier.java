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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_notifier_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_notifier_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_notifier_time;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_notifier_total;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.infra.common.notification.dingtalk.DingtalkMessageNotifier;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.AccessToken;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.AccessTokenResult;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.CreateSceneGroupV2;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.CreateSceneGroupV2Result;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.MsgKeyType;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.RobotGroupMessagesSend;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.entity.Notification.DingtalkConfig;
import com.wl4g.rengine.executor.metrics.MeterUtil;

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
    public Object send(final @NotNull Map<String, Object> parameter) {
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_SEND);
            return MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_SEND, () -> {
                final DingtalkConfig config = loadCurrentDingtalkConfig();

                // Gets request parameters.
                final String accessToken = getRequiredRefreshed().getAccessToken();
                final String msgKey = (String) parameter.getOrDefault(DingtalkMessageNotifier.KEY_MSG_KEY,
                        MsgKeyType.sampleMarkdown.name());
                final String msgParam = (String) parameter.get(DingtalkMessageNotifier.KEY_MSG_PARAM);

                // The following parameters are optional and default
                // values are supported.
                final String openConversationId = (String) parameter
                        .getOrDefault(DingtalkMessageNotifier.KEY_OPEN_CONVERSATION_ID, config.getDefaultOpenConversationId());
                final String robotCode = (String) parameter.getOrDefault(DingtalkMessageNotifier.KEY_ROBOT_CODE,
                        config.getDefaultRobotCode());

                hasTextOf(accessToken, "acccessToken");
                hasTextOf(msgKey, "msgKey");
                hasTextOf(msgParam, "msgParam");
                hasTextOf(openConversationId, "openConversationId");
                hasTextOf(robotCode, "robotCode");

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_SEND);
                return DingtalkAPI.getInstance()
                        .sendRobotGroupMessages(accessToken,
                                RobotGroupMessagesSend.builder()
                                        .msgKey(MsgKeyType.valueOf(msgKey))
                                        .msgParam(msgParam)
                                        .openConversationId(openConversationId)
                                        .robotCode(robotCode)
                                        .build());
            });
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_SEND);
            throw e;
        }
    }

    public CreateSceneGroupV2Result createSceneGroupV2(final @NotNull CreateSceneGroupV2 request) {
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_CREATESCENEGROUPV2);
            return MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_CREATESCENEGROUPV2, () -> {
                final DingtalkConfig config = loadCurrentDingtalkConfig();

                // Gets request parameters.
                final String accessToken = getRequiredRefreshed().getAccessToken();

                // The following parameters are optional and default
                // values are supported.
                String templateId = request.getTemplate_id();
                if (isBlank(templateId)) {
                    templateId = config.getDefaultScenesGroupV2TemplateId();
                }
                // group admin users.
                List<String> adminUserIds = safeArrayToList(split(request.getSubadmin_ids(), ","));
                if (CollectionUtils2.isEmpty(adminUserIds)) {
                    adminUserIds = config.getDefaultScenesGroupV2AdminUserIds();
                    request.setSubadmin_ids(join(adminUserIds, ","));
                }
                // group users.
                List<String> userIds = safeArrayToList(split(request.getUser_ids(), ","));
                if (CollectionUtils2.isEmpty(userIds)) {
                    userIds = config.getDefaultScenesGroupV2UserIds();
                    request.setUser_ids(join(userIds, ","));
                }
                // group owner user.
                String ownerUserId = request.getOwner_user_id();
                if (isBlank(ownerUserId)) {
                    // By default, the first administrator is the group
                    // owner.
                    if (!adminUserIds.isEmpty()) {
                        ownerUserId = adminUserIds.get(0);
                        request.setOwner_user_id(ownerUserId);
                    }
                }
                // Other optional fields.
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
            final @NotNull Map<String, String> parameter,
            final @NotNull Consumer<JsonNode> process) {
        notNullOf(parameter, "parameter");
        final String signature = parameter.get("signature");
        final String timestamp = parameter.get("timestamp");
        final String nonce = parameter.get("nonce");
        final String bodyJson = parameter.get("nonce");
        final DingtalkConfig config = loadCurrentDingtalkConfig().validate(true);
        return DingtalkAPI.getInstance()
                .processCallback(config.getToken(), config.getAesKey(), config.getCorpId(), signature, timestamp, nonce, bodyJson,
                        process);
    }

    private DingtalkConfig loadCurrentDingtalkConfig() {
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
    public RefreshedInfo refresh(Notification notification) {
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

    public static final String KEY_TOKEN = "token";
    public static final String KEY_AES_KEY = "aesKey";
    public static final String KEY_CORP_ID = "corpId";

}
