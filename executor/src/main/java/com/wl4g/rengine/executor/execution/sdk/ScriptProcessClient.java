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
import static java.lang.String.format;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.cli.ProcessUtils;
import com.wl4g.rengine.common.exception.ExecutionScriptException;

import lombok.ToString;

/**
 * {@link ScriptProcessClient}
 * 
 * @author James Wong
 * @version 2022-09-25
 * @since v1.0.0
 */
@ToString
public class ScriptProcessClient {

    public @HostAccess.Export ScriptProcessClient() {
    }

    public @HostAccess.Export String execute(@NotBlank String cmds) {
        return execute(cmds, DEFAULT_COMMAND_TIMEOUT_MS);
    }

    public @HostAccess.Export String execute(@NotBlank String cmds, @Min(1) long timeoutMs) {
        hasTextOf(cmds, "cmds");
        isTrueOf(timeoutMs >= 1, "timeoutMs>=1");
        try {
            return ProcessUtils.execSimpleString(cmds, timeoutMs);
        } catch (Exception e) {
            throw new ExecutionScriptException(format("Failed to exec cmds for : '%s'", cmds), e);
        }
    }

    public static final int DEFAULT_COMMAND_TIMEOUT_MS = 30 * 1000; // Default:30s

}
