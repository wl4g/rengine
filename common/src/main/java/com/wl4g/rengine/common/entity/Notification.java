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
package com.wl4g.rengine.common.entity;

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
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.infra.common.validation.EnumValue;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class Notification extends BaseBean {
    private static final long serialVersionUID = 1L;

    @NotNull
    NotifierConfigPropertiesBase properties;

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
    public static abstract class NotifierConfigPropertiesBase {
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
    public static class EmailConfig extends NotifierConfigPropertiesBase {

        /**
         * Protocol used by the Email(SMTP) server.
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
         * Default MimeMessage encoding.
         */
        private @NotBlank @Default String defaultEncoding = "UTF-8";

        /**
         * Additional JavaMail session properties.
         */
        private @Nullable @Default Map<String, String> properties = new HashMap<>();
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class DingtalkConfig extends NotifierConfigPropertiesBase {
        private String agentId;
        private @NotBlank String appKey;
        private @NotBlank String appSecret;
        private @Nullable String defaultOpenConversationId;
        private @Nullable String defaultRobotCode;
        private @Nullable String defaultScenesGroupV2TemplateId;
        private @Nullable List<String> defaultScenesGroupV2AdminUserIds;
        private @Nullable List<String> defaultScenesGroupV2UserIds;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class AliyunSmsConfig extends NotifierConfigPropertiesBase {
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
    public static class AliyunVmsConfig extends NotifierConfigPropertiesBase {
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
    public static class WeChatMpConfig extends NotifierConfigPropertiesBase {
        private @NotBlank String appId;
        private @NotBlank String appSecret;
    }

}
