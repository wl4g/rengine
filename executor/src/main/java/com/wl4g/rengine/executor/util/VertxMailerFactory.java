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
package com.wl4g.rengine.executor.util;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.findField;
import static com.wl4g.infra.common.reflect.ReflectionUtils2.setField;
import static java.util.Objects.isNull;

import java.lang.reflect.Field;

import javax.validation.constraints.NotNull;

import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.mailer.runtime.BlockingMailerImpl;
import io.quarkus.mailer.runtime.MailerSupport;
import io.quarkus.mailer.runtime.MockMailboxImpl;
import io.quarkus.mailer.runtime.MutinyMailerImpl;
import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailConfig;
import io.vertx.mutiny.ext.mail.MailClient;
import lombok.Getter;

/**
 * {@link VertxMailerFactory}
 * 
 * @author James Wong
 * @version 2023-02-07
 * @since v1.0.0
 */
@Getter
public class VertxMailerFactory {
    final @NotNull Vertx vertx;
    final @NotNull MailConfig mailConfig;
    @NotNull
    BlockingMailerImpl mailer;
    @NotNull
    MutinyMailerImpl reactiveMailer;

    public VertxMailerFactory(@NotNull Vertx vertx, @NotNull MailConfig mailConfig) {
        this.vertx = notNullOf(vertx, "vertx");
        this.mailConfig = notNullOf(mailConfig, "mailConfig");
        init();
    }

    private VertxMailerFactory init() {
        if (isNull(mailer)) {
            if (isNull(mailer)) {
                synchronized (this) {
                    final io.vertx.mutiny.core.Vertx _vertx = new io.vertx.mutiny.core.Vertx(notNullOf(vertx, "vertx"));

                    final MailClient mailClient = MailClient.create(_vertx, notNullOf(mailConfig, "mailConfig"));
                    final MutinyMailerImpl reactiveMailer = new MutinyMailerImpl();

                    final Field clientField = findField(MutinyMailerImpl.class, "client", MailClient.class);
                    setField(clientField, reactiveMailer, mailClient, true);

                    final Field vertxField = findField(MutinyMailerImpl.class, "vertx", io.vertx.mutiny.core.Vertx.class);
                    setField(vertxField, reactiveMailer, _vertx, true);

                    final MockMailboxImpl mockMailbox = new MockMailboxImpl();
                    final Field mockMailboxField = findField(MutinyMailerImpl.class, "mockMailbox", MockMailboxImpl.class);
                    setField(mockMailboxField, reactiveMailer, mockMailbox, true);

                    final MailerSupport mailerSupport = new MailerSupport(null, null, false);
                    final Field mailerSupportField = findField(MutinyMailerImpl.class, "mailerSupport", MailerSupport.class);
                    setField(mailerSupportField, reactiveMailer, mailerSupport, true);

                    final BlockingMailerImpl mailer = new BlockingMailerImpl();
                    final Field mailerField = findField(BlockingMailerImpl.class, "mailer", ReactiveMailer.class);
                    setField(mailerField, mailer, reactiveMailer, true);

                    this.mailer = mailer;
                    this.reactiveMailer = reactiveMailer;
                }
            }
        }
        return this;
    }

}
