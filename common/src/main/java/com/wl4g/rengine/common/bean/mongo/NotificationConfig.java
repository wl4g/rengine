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
package com.wl4g.rengine.common.bean.mongo;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link NotificationConfig}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public class NotificationConfig extends BeanBase {
    private EmailConfig email;
    private DingtalkConfig dingtalk;
    private WeComConfig wecom;
    private AliyunSmsConfig aliyunSms;
    private AliyunVmsConfig aliyunVms;
    private WebhookConfig webhook;

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class EmailConfig {
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
    public static class DingtalkConfig {
        private @NotBlank String appKey;
        private @NotBlank String appSecret;
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class WeComConfig {
    }

    @Getter
    @Setter
    @SuperBuilder
    @ToString
    @NoArgsConstructor
    public static class AliyunSmsConfig {
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
    public static class AliyunVmsConfig {
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
    public static class WebhookConfig {
        private List<String> urls;
    }

}
