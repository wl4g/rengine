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
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

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

    volatile RefreshedInfo refreshed;

    @Override
    public NotifierKind kind() {
        return NotifierKind.DINGTALK;
    }

    @Override
    public Object send(final @NotNull Map<String, Object> parameter) {
        final String accessToken = getRequiredRefreshed().getAccessToken();
        final String msgKey = (String) parameter.getOrDefault(DingtalkMessageNotifier.KEY_MSG_KEY,
                MsgKeyType.sampleMarkdown.name());
        final String msgParam = (String) parameter.get(DingtalkMessageNotifier.KEY_MSG_PARAM);

        // Gets default configuration properties.
        final Map<String, Object> attributes = getRequiredRefreshed().getAttributes();

        // The following parameters are optional and default values are
        // supported.
        final String openConversationId = (String) parameter.getOrDefault(DingtalkMessageNotifier.KEY_OPEN_CONVERSATION_ID,
                attributes.get(DingtalkMessageNotifier.KEY_OPEN_CONVERSATION_ID));
        final String robotCode = (String) parameter.getOrDefault(DingtalkMessageNotifier.KEY_ROBOT_CODE,
                attributes.get(DingtalkMessageNotifier.KEY_ROBOT_CODE));

        hasTextOf(accessToken, "acccessToken");
        hasTextOf(msgKey, "msgKey");
        hasTextOf(msgParam, "msgParam");
        hasTextOf(openConversationId, "openConversationId");
        hasTextOf(robotCode, "robotCode");

        return DingtalkAPI.sendRobotGroupMessages(accessToken,
                RobotGroupMessagesSend.builder()
                        .msgKey(MsgKeyType.valueOf(msgKey))
                        .msgParam(msgParam)
                        .openConversationId(openConversationId)
                        .robotCode(robotCode)
                        .build());
    }

    public CreateSceneGroupV2Result createSceneGroupV2(final @NotNull CreateSceneGroupV2 request) {
        final String accessToken = getRequiredRefreshed().getAccessToken();

        // Gets default configuration properties.
        final Map<String, Object> attributes = getRequiredRefreshed().getAttributes();

        final List<String> defaultScenesGroupV2AdminUserIds = safeArrayToList(
                split((String) attributes.get(DingtalkMessageNotifier.KEY_SCENES_GROUP_V2_ADMIN_USER_IDS), ","));
        final List<String> defaultScenesGroupV2UserIds = safeArrayToList(
                split((String) attributes.get(DingtalkMessageNotifier.KEY_SCENES_GROUP_V2_USER_IDS), ","));

        // The following parameters are optional and default values are
        // supported.
        String templateId = request.getTemplate_id();
        if (isBlank(templateId)) {
            templateId = (String) attributes.get(DingtalkMessageNotifier.KEY_SCENES_GROUP_V2_TEMPLATE_ID);
        }
        // group admin users.
        List<String> adminUserIds = safeArrayToList(split(request.getSubadmin_ids(), ","));
        if (CollectionUtils2.isEmpty(adminUserIds)) {
            adminUserIds = defaultScenesGroupV2AdminUserIds;
            request.setSubadmin_ids(join(adminUserIds, ","));
        }
        // group users.
        List<String> userIds = safeArrayToList(split(request.getUser_ids(), ","));
        if (CollectionUtils2.isEmpty(userIds)) {
            userIds = defaultScenesGroupV2UserIds;
            request.setUser_ids(join(userIds, ","));
        }
        // group owner user.
        String ownerUserId = request.getOwner_user_id();
        if (isBlank(ownerUserId)) {
            // By default, the first administrator is the group owner.
            if (!adminUserIds.isEmpty()) {
                ownerUserId = adminUserIds.get(0);
                request.setOwner_user_id(ownerUserId);
            }
        }
        // Other optional fields.
        if (isBlank(request.getUuid())) {
            request.setUuid(UUID.randomUUID().toString());
        }

        return DingtalkAPI.createSceneGroupV2(accessToken, request);
    }

    @Override
    public RefreshedInfo refresh(Notification notification) {
        final DingtalkConfig config = (DingtalkConfig) notification.getProperties();
        final AccessTokenResult result = DingtalkAPI
                .getAccessToken(AccessToken.builder().appKey(config.getAppKey()).appSecret(config.getAppSecret()).build());

        // Sets default configuration properties.
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(DingtalkMessageNotifier.KEY_OPEN_CONVERSATION_ID, config.getDefaultOpenConversationId());
        attributes.put(DingtalkMessageNotifier.KEY_ROBOT_CODE, config.getDefaultRobotCode());
        attributes.put(DingtalkMessageNotifier.KEY_SCENES_GROUP_V2_TEMPLATE_ID, config.getDefaultScenesGroupV2TemplateId());
        attributes.put(DingtalkMessageNotifier.KEY_SCENES_GROUP_V2_ADMIN_USER_IDS, config.getDefaultScenesGroupV2AdminUserIds());
        attributes.put(DingtalkMessageNotifier.KEY_SCENES_GROUP_V2_USER_IDS, config.getDefaultScenesGroupV2UserIds());

        return RefreshedInfo.builder()
                .notifierType(kind())
                .appKey(config.getAppKey())
                // .appSecret(config.getAppSecret())
                .accessToken(result.getAccessToken())
                .expireSeconds(result.getExpireIn())
                .attributes(attributes)
                .build();
    }

}
