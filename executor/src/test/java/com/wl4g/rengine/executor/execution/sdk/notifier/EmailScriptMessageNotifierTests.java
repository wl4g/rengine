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

import static com.wl4g.infra.common.lang.EnvironmentUtil.getIntProperty;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getStringProperty;
import static java.util.Collections.singletonList;

import org.junit.BeforeClass;
import org.junit.Test;

import com.wl4g.rengine.executor.util.TestDefaultBaseSetup;
import com.wl4g.rengine.executor.util.VertxMailerFactory;

import io.quarkus.mailer.Mail;
import io.vertx.ext.mail.LoginOption;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.StartTLSOptions;

/**
 * {@link EmailScriptMessageNotifierTests}
 * 
 * @author James Wong
 * @date 2023-02-07
 * @since v1.0.0
 */
public class EmailScriptMessageNotifierTests {

    static String test_serveHost = getStringProperty("TEST_SERVE_HOST", "smtp.exmail.qq.com");
    static int test_servePort = getIntProperty("TEST_SERVE_PORT", 465);
    static String test_fromUser = getStringProperty("TEST_FROM_USER", "sysnotification01@xxxx.com");
    static String test_password = getStringProperty("TEST_PASSWORD", "CAGd9ZHZMCUHXEVV");
    static String test_toUser = getStringProperty("TEST_TO_USER", "983708408@qq.com");

    static final MailConfig mailConfig = new MailConfig().setHostname(test_serveHost)
            .setPort(test_servePort)
            .setStarttls(StartTLSOptions.REQUIRED)
            .setLogin(LoginOption.REQUIRED)
            .setSsl(true)
            .setUsername(test_fromUser)
            .setPassword(test_password)
            .setConnectTimeout(6_000)
            .setSslHandshakeTimeout(10_000)
            // io.vertx.core.net.TCPSSLOptions#DEFAULT_IDLE_TIMEOUT
            .setIdleTimeout(0)
            .setSoLinger(-1)
            .setKeepAlive(false)
            .setTcpNoDelay(true);

    static VertxMailerFactory factory;

    @BeforeClass
    public static void init() {
        factory = new VertxMailerFactory(TestDefaultBaseSetup.buildCoreVertxDefault(), mailConfig);
    }

    // see: https://github.com/quarkusio/quarkus/issues/1840
    // see: https://quarkus.io/guides/mailer-reference
    // see: https://quarkus.io/guides/mailer#implement-the-http-endpoint
    @Test
    public void sendMimeEmail() {
        final Mail mail = new Mail().setSubject("Testing Sender(mime)")
                .setFrom(test_fromUser)
                .setTo(singletonList(test_toUser))
                .setText("<h1>This testing mime email message!!!</h1></br><p><font color=red>It's a red word.</font></p>");
        factory.getMailer().send(mail);
    }

}
