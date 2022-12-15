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
package com.wl4g.rengine.evaluator.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.cli.ssh2.SSH2Holders;
import com.wl4g.infra.common.cli.ssh2.SSH2Holders.Ssh2ExecResult;
import com.wl4g.rengine.common.exception.ExecutionScriptRengineException;

import lombok.ToString;

/**
 * {@link ScriptSSHClient}
 * 
 * @author James Wong
 * @version 2022-09-25
 * @since v1.0.0
 */
@ToString
public class ScriptSSHClient {

    public @HostAccess.Export ScriptSSHClient() {
    }

    public @HostAccess.Export Ssh2ExecResult execute(
            @NotBlank String host,
            @NotBlank String user,
            String password,
            @NotBlank String command) {
        return execute(host, 22, user, null, password, command, DEFAULT_COMMAND_TIMEOUT_MS);
    }

    public @HostAccess.Export Ssh2ExecResult execute(
            @NotBlank String host,
            @Min(1) int port,
            @NotBlank String user,
            String pemPrivateKey,
            String password,
            @NotBlank String command,
            @Min(1) long timeoutMs) {
        hasTextOf(host, "host");
        isTrueOf(port >= 1, "port>=1");
        hasTextOf(user, "user");
        isTrueOf(timeoutMs >= 1, "port>=1");
        try {
            final char[] pemPrivateKeyChars = nonNull(pemPrivateKey) ? pemPrivateKey.toCharArray() : null;
            return SSH2Holders.getDefault()
                    .execWaitForResponse(host, port, user, pemPrivateKeyChars, password, command, timeoutMs);
        } catch (Exception e) {
            throw new ExecutionScriptRengineException(
                    format("Failed to execute ssh command for equivalent : ssh -p %s %s@%s '%s'", port, user, host, command), e);
        }
    }

    public static final int DEFAULT_COMMAND_TIMEOUT_MS = 30 * 1000; // Default:30s
}
