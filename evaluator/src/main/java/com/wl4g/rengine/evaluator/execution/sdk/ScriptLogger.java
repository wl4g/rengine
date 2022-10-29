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
package com.wl4g.rengine.evaluator.execution.sdk;

import org.graalvm.polyglot.HostAccess;

import lombok.AllArgsConstructor;
import lombok.CustomLog;

/**
 * {@link ScriptLogger}
 * 
 * @author James Wong
 * @version 2022-09-29
 * @since v1.0.0
 */
@CustomLog
@AllArgsConstructor
public class ScriptLogger {

    private final transient ScriptContext context;

    public @HostAccess.Export void trace(String format, Object... args) {
        write(1, format, args);
    }

    public @HostAccess.Export void debug(String format, Object... args) {
        write(2, format, args);
    }

    public @HostAccess.Export void info(String format, Object... args) {
        write(3, format, args);
    }

    public @HostAccess.Export void warn(String format, Object... args) {
        write(4, format, args);
    }

    public @HostAccess.Export void error(String format, Object... args) {
        write(5, format, args);
    }

    public @HostAccess.Export void fatal(String format, Object... args) {
        write(6, format, args);
    }

    void write(int level, String format, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug("Script logging write to MinIO. - level: %s, '%s'", level, String.format(format, args));
        }
        try {
            context.getMinioManager().writeObject(format);
        } catch (Exception e) {
            log.error(String.format("Unable not script logging write to MinIO. - level: %s, '%s'", level,
                    String.format(format, args)), e);
        }
    }

}
