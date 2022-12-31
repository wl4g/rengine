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
package com.wl4g.rengine.executor.execution.sdk;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_client_failure;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_client_success;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_client_time;
import static com.wl4g.rengine.executor.metrics.ExecutorMeterService.MetricsName.execution_sdk_client_total;
import static java.lang.String.format;

import java.net.Socket;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.graalvm.polyglot.HostAccess;

import com.google.common.io.ByteStreams;
import com.wl4g.infra.common.codec.CodecSource;
import com.wl4g.rengine.common.exception.ExecutionScriptException;
import com.wl4g.rengine.executor.metrics.MeterUtil;

import lombok.ToString;

/**
 * {@link ScriptTCPClient}
 * 
 * @author James Wong
 * @version 2022-09-25
 * @since v1.0.0
 */
@ToString
public class ScriptTCPClient {
    final static String METHOD_EXECUTE = "execute";

    public @HostAccess.Export ScriptTCPClient() {
    }

    public @HostAccess.Export String execute(@NotBlank String host, @Min(1) int port, @NotBlank String base64Message) {
        return execute(host, port, base64Message, DEFAULT_COMMAND_TIMEOUT_MS);
    }

    public @HostAccess.Export String execute(
            @NotBlank String host,
            @Min(1) int port,
            @NotBlank String base64Message,
            @Min(1) long timeoutMs) {
        hasTextOf(host, "host");
        isTrueOf(port >= 1, "port>=1");
        isTrueOf(timeoutMs >= 1, "port>=1");
        MeterUtil.counter(execution_sdk_client_total, ScriptTCPClient.class, METHOD_EXECUTE);

        try (Socket socket = new Socket(host, port);) {
            final String result = MeterUtil.timer(execution_sdk_client_time, ScriptTCPClient.class, METHOD_EXECUTE, () -> {
                // Write message to server.
                socket.getOutputStream().write(CodecSource.fromBase64(base64Message).getBytes());
                // TODO watch timeout
                // Read message from server.
                return new CodecSource(ByteStreams.toByteArray(socket.getInputStream())).toBase64();
            });
            MeterUtil.counter(execution_sdk_client_success, ScriptTCPClient.class, METHOD_EXECUTE);
            return result;
        } catch (Exception e) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptTCPClient.class, METHOD_EXECUTE);
            throw new ExecutionScriptException(format(
                    "Failed to write to tcp channal for host: %s, port: %s, base64Message: '%s'", host, port, base64Message), e);
        }
    }

    public static final int DEFAULT_COMMAND_TIMEOUT_MS = 30 * 1000; // Default:30s

}
