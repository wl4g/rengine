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
package com.wl4g.rengine.common.bean;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.validation.EnumValue;
import com.wl4g.rengine.common.bean.Notification.AliyunSmsConfig;
import com.wl4g.rengine.common.bean.Notification.AliyunVmsConfig;
import com.wl4g.rengine.common.bean.Notification.DingtalkConfig;
import com.wl4g.rengine.common.bean.Notification.EmailConfig;
import com.wl4g.rengine.common.bean.Notification.WeComConfig;
import com.wl4g.rengine.common.bean.Notification.WebhookConfig;

import io.swagger.v3.oas.annotations.media.Schema;
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
 * @since v3.0.0
 */
// 1.多态参见:https://swagger.io/docs/specification/data-models/inheritance-and-polymorphism/
// 2.对于swagger3注解,父类必须是抽象的，否则swagger3页面请求参数schemas展开后会以父类名重复展示3个.
@Schema(oneOf = { EmailConfig.class, DingtalkConfig.class, WeComConfig.class, AliyunSmsConfig.class, AliyunVmsConfig.class,
        WebhookConfig.class }, discriminatorProperty = "@kind")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@kind", visible = true)
@JsonSubTypes({ @Type(value = EmailConfig.class, name = "email"), @Type(value = DingtalkConfig.class, name = "dingtalk"),
        @Type(value = WeComConfig.class, name = "wecom"), @Type(value = AliyunSmsConfig.class, name = "aliyunSms"),
        @Type(value = AliyunVmsConfig.class, name = "aliyunVms"), @Type(value = WebhookConfig.class, name = "webhook") })
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public abstract class Notification extends BaseBean {
    private static final long serialVersionUID = 1L;

    @Schema(name = "@kind", implementation = NotificationKind.class)
    @JsonProperty(value = "@kind")
    private @NotBlank @EnumValue(enumCls = NotificationKind.class) String kind;

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class EmailConfig extends Notification {
        private static final long serialVersionUID = 1L;
        private @NotBlank String smtpHost;
        private @NotNull @Min(0) Integer smtpPort;
        private @NotBlank String sendMail;
        private @NotBlank String username;
        private @NotBlank String password;
        private @NotNull Boolean useSSL;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class DingtalkConfig extends Notification {
        private static final long serialVersionUID = 1L;
        private @NotBlank String appKey;
        private @NotBlank String appSecret;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class WeComConfig extends Notification {
        private static final long serialVersionUID = 1L;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class AliyunSmsConfig extends Notification {
        private static final long serialVersionUID = 1L;
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
    public static class AliyunVmsConfig extends Notification {
        private static final long serialVersionUID = 1L;
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
    public static class WebhookConfig extends Notification {
        private static final long serialVersionUID = 1L;
        private List<String> urls;
    }

    public static enum NotificationKind {
        email, dingtalk, wecom, aliyunSms, aliyunVms, webhook
    }

}
