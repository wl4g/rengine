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
package com.wl4g.rengine.common.entity.sys;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.entity.BaseEntity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Notification}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class Notification extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @NotNull
    NotifierDetailsBase details;

    // 1.多态参见:https://swagger.io/docs/specification/data-models/inheritance-and-polymorphism/
    // 2.对于swagger3注解,父类必须是抽象的，否则swagger3页面请求参数schemas展开后会以父类名重复展示3个.
    @Schema(oneOf = { EmailConfig.class, DingtalkConfig.class, AliyunSmsConfig.class, AliyunVmsConfig.class,
            WeChatMpConfig.class }, discriminatorProperty = "type")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
    @JsonSubTypes({ @Type(value = EmailConfig.class, name = "EMAIL"), @Type(value = DingtalkConfig.class, name = "DINGTALK"),
            @Type(value = AliyunSmsConfig.class, name = "ALIYUN_SMS"), @Type(value = AliyunVmsConfig.class, name = "ALIYUN_VMS"),
            @Type(value = WeChatMpConfig.class, name = "WECHAT_MP") })
    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static abstract class NotifierDetailsBase {
        @Schema(name = "type", implementation = NotifierKind.class)
        @JsonProperty(value = "type", access = Access.WRITE_ONLY)
        @NotNull
        private @NotBlank @EnumValue(enumCls = NotifierKind.class) String type;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class EmailConfig extends NotifierDetailsBase {

        /**
         * Notice: 暂仅支持SMTP, 原因是 rengine executor 服务的
         * {@link com.wl4g.rengine.executor.execution.sdk.notifier.EmailScriptMessageNotifier}
         * 使用了 quarkus-mailer 模块(当前2023/02仅支持SMTP)
         */
        private @NotBlank @Default String protocol = "smtp";

        /**
         * Email(SMTP) server host.
         */
        private @NotBlank @Default String host = "smtp.exmail.qq.com";

        /**
         * Email(SMTP) server port.
         */
        private @NotBlank @Default Integer port = 465;

        /**
         * Login user of the Email(SMTP) server.
         */
        private @NotBlank String username;

        /**
         * Login password of the Email(SMTP) server.
         */
        private @NotBlank String password;

        /**
         * Additional JavaMail session details.
         */
        private @Nullable @Default Map<String, Object> properties = new HashMap<>();
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class DingtalkConfig extends NotifierDetailsBase {
        // The API authentication infomration.
        private @NotBlank String appKey;
        private @NotBlank String appSecret;
        // The event subscribe callback API encryption information.
        private String token;
        private String aesKey;
        // For DingTalk open platform enterprise internal applications, cropId
        // is appKey.
        private String corpId;

        // Note: Since it is only used for system-level notifications, there
        // will not be too much data, so the temporary design is saved to this
        // single object.
        private List<DingtalkUserInfo> users;
        private List<DingtalkScenesGroupInfo> scenesGroups;

        public DingtalkConfig validate(boolean checkCallbackAPIProperties) {
            hasTextOf(appKey, "appKey");
            hasTextOf(appSecret, "appSecret");
            if (checkCallbackAPIProperties) {
                hasTextOf(token, "token");
                hasTextOf(aesKey, "aesKey");
                hasTextOf(corpId, "corpId");
            }
            return this;
        }

        @Getter
        @Setter
        @SuperBuilder
        @ToString
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DingtalkUserInfo {
            @NotBlank
            String mobile;
            String userId;

            public DingtalkUserInfo validate(boolean checkForUserId) {
                hasTextOf(mobile, "mobile");
                if (checkForUserId) {
                    hasTextOf(userId, "userId");
                }
                return this;
            }
        }

        @Getter
        @Setter
        @SuperBuilder
        @ToString
        @NoArgsConstructor
        @AllArgsConstructor
        public static class DingtalkScenesGroupInfo {
            @NotBlank
            String title; // Scenes group display name.

            @NotBlank
            String templateId; // Scenes group create template

            String chatId;

            List<String> robotCodes;

            @NotBlank
            String openConversationId;

            @NotBlank
            String ownerUserId;

            List<String> userIds;

            List<String> adminUserIds;

            String uuid;

            String icon;

            String mentionAllAuthority;

            String showHistoryType;

            String validationType;

            String searchable;

            String chatVannedType;

            String managementType;

            String onlyAdminCanDing;

            String allMembersCanCreateMcsConf;

            String allMembersCanCreateCalendar;

            String groupEmailDisabled;

            String onlyAdminCanSetMsgTop;

            String addFriendForbidden;

            String groupLiveSwitch;

            String membersToAdminChat;

            public DingtalkScenesGroupInfo validate() {
                hasTextOf(title, "title");
                hasTextOf(templateId, "templateId");
                hasTextOf(openConversationId, "openConversationId");
                hasTextOf(ownerUserId, "ownerUserId");
                return this;
            }
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class AliyunSmsConfig extends NotifierDetailsBase {
        private @NotBlank String accessKey;
        private @NotBlank String accessSecret;
        private @NotBlank String regionId;
        private @NotBlank String signName;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class AliyunVmsConfig extends NotifierDetailsBase {
        private @NotBlank String accessKey;
        private @NotBlank String accessSecret;
        private @NotBlank String regionId;
        private @NotBlank String calledShowNumber;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class WeChatMpConfig extends NotifierDetailsBase {
        private @NotBlank String appId;
        private @NotBlank String appSecret;
    }

}
