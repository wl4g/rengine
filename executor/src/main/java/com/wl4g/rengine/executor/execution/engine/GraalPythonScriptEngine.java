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
package com.wl4g.rengine.executor.execution.engine;

import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_SCRIPT_TMP_CACHE_DIR;

import java.io.File;
import java.util.logging.Level;

import javax.inject.Singleton;

import com.wl4g.infra.common.graalvm.polyglot.GraalPolyglotManager;
import com.wl4g.infra.common.graalvm.polyglot.JdkLoggingOutputStream;
import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.rengine.common.exception.ExecutionScriptException;
import com.wl4g.rengine.executor.execution.EngineConfig.ScriptLogConfig;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link GraalPythonScriptEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@CustomLog
@Getter
@Singleton
public class GraalPythonScriptEngine extends GraalBaseScriptEngine {

    @Override
    protected String getPermittedLanguages() {
        return "python";
    }

    protected GraalPolyglotManager createGraalPolyglotManager() {
        try {
            log.info("Initialzing graal js script engine ...");
            final ScriptLogConfig scriptLogConfig = engineConfig.log();
            // TODO
            // TODO using python languages options
            // TODO
            return GraalPolyglotManager.newDefaultGraalJS(DEFAULT_EXECUTOR_SCRIPT_TMP_CACHE_DIR, metadata -> {
                String filePattern = buildScriptLogFilePattern(scriptLogConfig.baseDir(), metadata, false);
                // Make sure to generate a log file during
                // initialization to solve the problem that there is no
                // output but an error is thrown when the script is
                // executed. At this time, the logtail loading log
                // interface will report an error that does not exist.
                FileIOUtils.ensureFile(new File(filePattern));
                return new JdkLoggingOutputStream(filePattern, Level.INFO, scriptLogConfig.fileMaxSize(),
                        scriptLogConfig.fileMaxCount(), scriptLogConfig.enableConsole(), false);
            }, metadata -> {
                String filePattern = buildScriptLogFilePattern(scriptLogConfig.baseDir(), metadata, true);
                FileIOUtils.ensureFile(new File(filePattern));
                return new JdkLoggingOutputStream(filePattern, Level.WARNING, scriptLogConfig.fileMaxSize(),
                        scriptLogConfig.fileMaxCount(), scriptLogConfig.enableConsole(), true);
            });
        } catch (Throwable ex) {
            throw new ExecutionScriptException("Failed to init graal js script engine.", ex);
        }
    }

}
