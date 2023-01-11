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
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_notifier_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_notifier_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_notifier_time;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_notifier_total;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.notification.GenericNotifierParam;
import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.infra.common.notification.email.EmailNotifierProperties;
import com.wl4g.infra.common.notification.email.internal.EmailSenderAPI;
import com.wl4g.infra.common.notification.email.internal.JavaMailSender;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.entity.Notification.EmailConfig;
import com.wl4g.rengine.executor.metrics.MeterUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * {@link EmailScriptMessageNotifier}
 * 
 * @author James Wong
 * @version 2023-01-06
 * @since v1.0.0
 */
@Getter
@Setter
@Singleton
public class EmailScriptMessageNotifier implements ScriptMessageNotifier {

    EmailNotifierProperties usingConfig;
    volatile JavaMailSender mailSender;
    volatile RefreshedInfo refreshed;

    @Override
    public NotifierKind kind() {
        return NotifierKind.EMAIL;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object send(final @NotNull Map<String, Object> parameter) {
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_SEND);
            return MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_SEND, () -> {
                notNullOf(parameter, "parameter");
                final String content = (String) parameter.get(KEY_MAIL_MSG);
                hasTextOf(content, format("parameter['%s']", KEY_MAIL_MSG));

                final Object toUsers = parameter.get(KEY_MAIL_TO_USERS);
                notNullOf(toUsers, format("parameter['%s']", KEY_MAIL_TO_USERS));
                List<String> toUserList = null;
                if (toUsers instanceof List) {
                    toUserList = (List<String>) toUsers;
                } else if (toUsers instanceof String) {
                    toUserList = asList(split((String) toUsers, ","));
                } else {
                    throw new UnsupportedOperationException(
                            format("Unsupported toUsers parameter type, please check whether the parameters are correct, "
                                    + "only arrays or comma-separated strings are supported. toUsers: %s", toUsers));
                }

                EmailSenderAPI.send(mailSender, usingConfig,
                        new GenericNotifierParam().setToObjects(toUserList).addParameters(parameter), content);

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_SEND);
                return null;
            });
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_SEND);
            throw e;
        }
    }

    @Override
    public void update(@NotNull RefreshedInfo refreshed) {
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_UPDATE);
            MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_UPDATE, () -> {
                ScriptMessageNotifier.super.update(refreshed);

                // Initialze for config properties.
                final EmailConfig config = parseJSON((String) refreshed.getAttributes().get(KEY_MAIL_CONFIG), EmailConfig.class);
                notNull(config,
                        "Internal error! Please check the redis cache configuration data, email config json is required. refreshed: %s",
                        refreshed);

                usingConfig = new EmailNotifierProperties();
                usingConfig.setProtocol(config.getProtocol());
                usingConfig.setHost(config.getHost());
                usingConfig.setPort(config.getPort());
                usingConfig.setUsername(config.getUsername());
                usingConfig.setPassword(config.getPassword());
                usingConfig.setDefaultEncoding(config.getDefaultEncoding());
                usingConfig.setProperties(config.getProperties());

                // Initialze for sender.
                if (isNull(mailSender)) {
                    synchronized (this) {
                        if (isNull(mailSender)) {
                            mailSender = EmailSenderAPI.buildSender(usingConfig);
                        }
                    }
                }

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
                final EmailConfig config = (EmailConfig) notification.getProperties();

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_REFRESH);
                return RefreshedInfo.builder()
                        .notifierType(kind())
                        // .appKey(null)
                        // .appSecret(null)
                        // .accessToken(null)
                        // The accessToken is not actually needed, so it is set
                        // to never
                        // expire
                        .expireSeconds(Integer.MAX_VALUE)
                        .attributes(singletonMap(KEY_MAIL_CONFIG, toJSONString(config)))
                        .build();
            });
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_REFRESH);
            throw e;
        }
    }

    public static final String KEY_MAIL_CONFIG = EmailConfig.class.getName();
    public static final String KEY_MAIL_TO_USERS = "toUsers";
    public static final String KEY_MAIL_MSG = "msgContent";
}
