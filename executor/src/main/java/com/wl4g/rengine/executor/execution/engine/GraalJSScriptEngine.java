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

import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_SCRIPT_CACHE_DIR;

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
 * {@link GraalJSScriptEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@CustomLog
@Getter
@Singleton
public class GraalJSScriptEngine extends GraalBaseScriptEngine {

    @Override
    protected String getPermittedLanguages() {
        return "js";
    }

    // see:https://www.graalvm.org/22.2/reference-manual/js/
    @Override
    protected GraalPolyglotManager createGraalPolyglotManager() {
        try {
            log.info("Initialzing graal js script engine ...");
            final ScriptLogConfig scriptLogConfig = engineConfig.log();
            /**
             * TODO The best way is to let the rengine executor write to OSS in
             * real time, but unfortunately MinIO/S3 does not support append
             * writing (although it supports object merging, but it is still
             * difficult to achieve), unless you use Alibaba Cloud OSS (supports
             * real-time append writing), but this not a neutral approach.
             * Therefore, at present, only direct reading and writing of disks
             * is realized, and then shared mounts such as juiceFS, s3fs-fuse,
             * ossfs, etc. can be used to realize clustering. see to:
             * {@link com.wl4g.rengine.service.impl.ScheduleJobLogServiceImpl#logfile}
             */
            return GraalPolyglotManager.newDefaultGraalJS(DEFAULT_EXECUTOR_SCRIPT_CACHE_DIR, metadata -> {
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
