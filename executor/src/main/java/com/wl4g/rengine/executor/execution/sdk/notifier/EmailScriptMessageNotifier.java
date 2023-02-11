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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_failure;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_success;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_time;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_notifier_total;
import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.entity.Notification.EmailConfig;
import com.wl4g.rengine.executor.meter.MeterUtil;
import com.wl4g.rengine.executor.util.VertxMailerFactory;

import io.quarkus.mailer.Mail;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.LoginOption;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailResult;
import io.vertx.ext.mail.StartTLSOptions;
import lombok.Getter;
import lombok.Setter;

/**
 * {@link EmailScriptMessageNotifier}
 * 
 * @author James Wong
 * @version 2023-01-06
 * @since v1.0.0
 * @see https://github.com/quarkusio/quarkus/issues/1840
 * @see https://quarkus.io/guides/mailer-reference
 * @see https://quarkus.io/guides/mailer#implement-the-http-endpoint
 */
@Getter
@Setter
@Singleton
public class EmailScriptMessageNotifier implements ScriptMessageNotifier {

    volatile VertxMailerFactory factory;
    volatile RefreshedInfo refreshed;

    @Override
    public NotifierKind kind() {
        return NotifierKind.EMAIL;
    }

    @Override
    public Object send(final @NotEmpty Map<String, Object> parameter) {
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_SEND);
            return MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_SEND, () -> {
                notNullOf(parameter, "parameter");
                final String subject = getStringParam(parameter, KEY_MAIL_SUBJECT, true);
                final List<String> to = getArrayParam(parameter, KEY_MAIL_TO_USERS, true);
                final List<String> replyTo = getArrayParam(parameter, KEY_MAIL_REPLYTO, false);
                final List<String> cc = getArrayParam(parameter, KEY_MAIL_CC, false);
                final List<String> bcc = getArrayParam(parameter, KEY_MAIL_BCC, false);
                final String msg = getStringParam(parameter, KEY_MAIL_MSG, true);

                final Mail sendMail = new Mail().setSubject(subject)
                        .setFrom(factory.getMailConfig().getUsername())
                        .setTo(to)
                        .setReplyTo(replyTo.toArray(new String[0]))
                        .setCc(cc)
                        .setBcc(bcc)
                        .setText(msg);

                final Future<MailResult> future = factory.getMailClient().sendMail(VertxMailerFactory.toMailMessage(sendMail));

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_SEND);
                return future;
            });
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_SEND);
            throw ex;
        }
    }

    @Override
    public RefreshedInfo refresh(@NotNull Notification notification) {
        notNullOf(notification, "notification");
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_REFRESH);
            return MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_REFRESH, () -> {
                final EmailConfig config = (EmailConfig) notification.getProperties();

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_REFRESH);
                return RefreshedInfo.builder()
                        .notifierType(kind())
                        .expireSeconds(86400) // 24h
                        .attributes(singletonMap(KEY_MAIL_CONFIG, toJSONString(config)))
                        .build();
            });
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_REFRESH);
            throw ex;
        }
    }

    @Override
    public void update(@NotNull RefreshedInfo refreshed, @NotNull Vertx vertx) {
        notNullOf(refreshed, "refreshed");
        notNullOf(vertx, "vertx");
        try {
            MeterUtil.counter(execution_sdk_notifier_total, kind(), METHOD_UPDATE);
            MeterUtil.timer(execution_sdk_notifier_time, kind(), METHOD_UPDATE, () -> {
                ScriptMessageNotifier.super.update(refreshed, vertx);

                // Initialze for engineConfig properties.
                final EmailConfig config = parseJSON((String) refreshed.getAttributes().get(KEY_MAIL_CONFIG), EmailConfig.class);
                notNull(config,
                        "Internal error! Please check the redis cache configuration data, email engineConfig json is required. refreshed: %s",
                        refreshed);

                // Initialze for sender.
                if (isNull(factory)) {
                    synchronized (this) {
                        if (isNull(factory)) {
                            this.factory = new VertxMailerFactory(vertx,
                                    new MailConfig(new JsonObject(safeMap(config.getProperties()))).setHostname(config.getHost())
                                            .setPort(config.getPort())
                                            .setStarttls(StartTLSOptions.REQUIRED)
                                            .setLogin(LoginOption.REQUIRED)
                                            .setSsl(true)
                                            .setUsername(config.getUsername())
                                            .setPassword(config.getPassword())
                                            .setConnectTimeout(6_000)
                                            .setSslHandshakeTimeout(10_000)
                                            // io.vertx.core.net.TCPSSLOptions#DEFAULT_IDLE_TIMEOUT
                                            .setIdleTimeout(0)
                                            .setSoLinger(-1)
                                            .setKeepAlive(false)
                                            .setTcpNoDelay(true));
                        }
                    }
                }

                MeterUtil.counter(execution_sdk_notifier_success, kind(), METHOD_UPDATE);
                return null;
            });
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_notifier_failure, kind(), METHOD_UPDATE);
            throw ex;
        }
    }

    public static final String KEY_MAIL_CONFIG = EmailConfig.class.getName();
    public static final String KEY_MAIL_SUBJECT = "subject";
    public static final String KEY_MAIL_TO_USERS = "to";
    public static final String KEY_MAIL_REPLYTO = "replyTo";
    public static final String KEY_MAIL_CC = "cc";
    public static final String KEY_MAIL_BCC = "bcc";
    public static final String KEY_MAIL_MSG = "msg";

}
