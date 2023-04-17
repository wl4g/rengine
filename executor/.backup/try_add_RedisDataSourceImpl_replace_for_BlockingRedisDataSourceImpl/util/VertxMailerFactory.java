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
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.validation.constraints.NotNull;

import io.quarkus.mailer.Attachment;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.mailer.runtime.BlockingMailerImpl;
import io.quarkus.mailer.runtime.MailerSupport;
import io.quarkus.mailer.runtime.MockMailboxImpl;
import io.quarkus.mailer.runtime.MutinyMailerImpl;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailAttachment;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.impl.MailClientImpl;
import io.vertx.mutiny.ext.mail.MailClient;
import lombok.Getter;

/**
 * {@link VertxMailerFactory}
 * 
 * @author James Wong
 * @date 2023-02-07
 * @since v1.0.0
 */
@Getter
public class VertxMailerFactory {
    final @NotNull Vertx vertx;
    final @NotNull MailConfig mailConfig;

    @NotNull
    MailClient reactiveMailClient;
    @NotNull
    io.vertx.ext.mail.MailClient mailClient;

    @NotNull
    MutinyMailerImpl reactiveMailer;
    @NotNull
    BlockingMailerImpl mailer;

    public VertxMailerFactory(@NotNull Vertx vertx, @NotNull MailConfig mailConfig) {
        this.vertx = notNullOf(vertx, "vertx");
        this.mailConfig = notNullOf(mailConfig, "mailConfig");
        init();
    }

    private void init() {
        if (isNull(mailer)) {
            if (isNull(mailer)) {
                synchronized (this) {
                    final io.vertx.mutiny.core.Vertx _vertx = new io.vertx.mutiny.core.Vertx(notNullOf(vertx, "vertx"));

                    this.reactiveMailClient = MailClient.create(_vertx, notNullOf(mailConfig, "mailConfig"));
                    this.mailClient = new MailClientImpl(vertx, mailConfig, VertxMailerFactory.class.getSimpleName());

                    this.reactiveMailer = new MutinyMailerImpl();

                    final Field clientField = findField(MutinyMailerImpl.class, "client", MailClient.class);
                    setField(clientField, reactiveMailer, reactiveMailClient, true);

                    final Field vertxField = findField(MutinyMailerImpl.class, "vertx", io.vertx.mutiny.core.Vertx.class);
                    setField(vertxField, reactiveMailer, _vertx, true);

                    final MockMailboxImpl mockMailbox = new MockMailboxImpl();
                    final Field mockMailboxField = findField(MutinyMailerImpl.class, "mockMailbox", MockMailboxImpl.class);
                    setField(mockMailboxField, reactiveMailer, mockMailbox, true);

                    final MailerSupport mailerSupport = new MailerSupport(null, null, false);
                    final Field mailerSupportField = findField(MutinyMailerImpl.class, "mailerSupport", MailerSupport.class);
                    setField(mailerSupportField, reactiveMailer, mailerSupport, true);

                    this.mailer = new BlockingMailerImpl();
                    final Field mailerField = findField(BlockingMailerImpl.class, "mailer", ReactiveMailer.class);
                    setField(mailerField, mailer, reactiveMailer, true);
                }
            }
        }
    }

    /**
     * Notice: Corresponding to the method of the reactive class below, just
     * modify it to synchronous code (almost keep the logic unchanged). Because
     * the reactive mailer code cannot be used under the same graal.js context,
     * it will report an error: Multi threaded access requested by thread
     * Thread[vert.x-eventloop-thread-4,5,main] but is not allowed for language(
     * s) js.
     * 
     * @see {@link io.quarkus.mailer.runtime.MutinyMailerImpl#send(Mail)}
     * @see {@link io.quarkus.mailer.runtime.MutinyMailerImpl#toMailMessage(Mail)}
     */
    public static MailMessage toMailMessage(Mail mail) {
        final MailMessage message = new MailMessage();

        if (mail.getBounceAddress() != null) {
            message.setBounceAddress(mail.getBounceAddress());
        }
        if (mail.getFrom() != null) {
            message.setFrom(mail.getFrom());
        }
        message.setTo(mail.getTo());
        message.setCc(mail.getCc());
        message.setBcc(mail.getBcc());
        message.setSubject(mail.getSubject());
        message.setText(mail.getText());
        message.setHtml(mail.getHtml());
        message.setHeaders(toMultimap(mail.getHeaders()));
        if (mail.getReplyTo() != null) {
            // getReplyTo produces the comma-separated list.
            message.addHeader("Reply-To", mail.getReplyTo());
        }

        List<MailAttachment> attachments = new CopyOnWriteArrayList<>();
        List<MailAttachment> inline = new CopyOnWriteArrayList<>();
        for (Attachment attachment : mail.getAttachments()) {
            if (attachment.isInlineAttachment()) {
                inline.add(toMailAttachment(attachment));
            } else {
                attachments.add(toMailAttachment(attachment));
            }
        }

        message.setAttachment(attachments);
        message.setInlineAttachment(inline);
        return message;
    }

    /**
     * Notice: Corresponding to the method of the reactive class below, just
     * modify it to synchronous code (almost keep the logic unchanged). Because
     * the reactive mailer code cannot be used under the same graal.js context,
     * it will report an error: Multi threaded access requested by thread
     * Thread[vert.x-eventloop-thread-4,5,main] but is not allowed for language(
     * s) js.
     * 
     * @see {@link io.quarkus.mailer.runtime.MutinyMailerImpl#toMailAttachment(Attachment)}
     */
    public static MailAttachment toMailAttachment(Attachment attachment) {
        MailAttachment attach = MailAttachment.create();
        attach.setName(attachment.getName());
        attach.setContentId(attachment.getContentId());
        attach.setDescription(attachment.getDescription());
        attach.setDisposition(attachment.getDisposition());
        attach.setContentType(attachment.getContentType());

        if ((attachment.getFile() == null && attachment.getData() == null) // No-content
                || (attachment.getFile() != null && attachment.getData() != null)) { // Too-much-content
            throw new IllegalArgumentException("An attachment must contain either a file or a raw data");
        }

        // TODO Converting reactive code logic to synchronous code has not yet
        // been implemented.
        // return getAttachmentStream(vertx,
        // attachment).onItem().transform(attach::setData);
        throw new UnsupportedOperationException(
                "Converting reactive code logic to synchronous code has not yet been implemented.");
    }

    /**
     * Notice: Corresponding to the method of the reactive class below, just
     * modify it to synchronous code (almost keep the logic unchanged). Because
     * the reactive mailer code cannot be used under the same graal.js context,
     * it will report an error: Multi threaded access requested by thread
     * Thread[vert.x-eventloop-thread-4,5,main] but is not allowed for language(
     * s) js.
     * 
     * @see {@link io.quarkus.mailer.runtime.MutinyMailerImpl#toMultimap(Map,List)}
     */
    public static MultiMap toMultimap(Map<String, List<String>> headers) {
        MultiMap mm = MultiMap.caseInsensitiveMultiMap();
        headers.forEach(mm::add);
        return mm;
    }

    //// @formatter:off
    ///**
    // * @see {@link io.quarkus.mailer.runtime.MutinyMailerImpl#getAttachmentStream(Vertx,Attachment)}
    // */
    //public static Uni<Buffer> getAttachmentStream(Vertx vertx, Attachment attachment) {
    //    if (attachment.getFile() != null) {
    //        Uni<AsyncFile> open = vertx.fileSystem()
    //                .open(attachment.getFile().getAbsolutePath(), new OpenOptions().setRead(true).setCreate(false));
    //        return open.flatMap(af -> af.toMulti()
    //                .map(io.vertx.mutiny.core.buffer.Buffer::getDelegate)
    //                .onTermination()
    //                .call((r, f) -> af.close())
    //                .collect()
    //                .in(Buffer::buffer, Buffer::appendBuffer));
    //    } else if (attachment.getData() != null) {
    //        Publisher<Byte> data = attachment.getData();
    //        return Multi.createFrom().publisher(data).collect().in(Buffer::buffer, Buffer::appendByte);
    //    } else {
    //        return Uni.createFrom().failure(new IllegalArgumentException("Attachment has no data"));
    //    }
    //}
    //// @formatter:on
}
