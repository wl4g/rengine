// @formatter:off
///*
// * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.rengine.executor.execution.sdk;
//
//import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
//import static com.wl4g.infra.common.lang.Assert2.notNullOf;
//import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_LOGGING_PREFIX;
//import static java.lang.String.valueOf;
//
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//
//import org.graalvm.polyglot.HostAccess;
//
//import com.wl4g.rengine.executor.minio.MinioManager;
//
//import lombok.CustomLog;
//
///**
// * {@link ScriptLogger}
// * 
// * @author James Wong
// * @date 2022-09-29
// * @since v1.0.0
// */
//@CustomLog
//public class ScriptLogger {
//
//    final String logObjectPrefix;
//    final MinioManager minioManager;
//
//    public ScriptLogger(final @NotBlank String scenesCode, @NotBlank Long workflowId, final @NotNull MinioManager minioManager) {
//        this.logObjectPrefix = buildLogObjectPrefix(scenesCode, workflowId);
//        this.minioManager = notNullOf(minioManager, "minioManager");
//    }
//
//    public @HostAccess.Export void trace(String format, Object... args) {
//        write(1, format, args);
//    }
//
//    public @HostAccess.Export void debug(String format, Object... args) {
//        write(2, format, args);
//    }
//
//    public @HostAccess.Export void info(String format, Object... args) {
//        write(3, format, args);
//    }
//
//    public @HostAccess.Export void warn(String format, Object... args) {
//        write(4, format, args);
//    }
//
//    public @HostAccess.Export void error(String format, Object... args) {
//        write(5, format, args);
//    }
//
//    public @HostAccess.Export void fatal(String format, Object... args) {
//        write(6, format, args);
//    }
//
//    void write(int level, String format, Object... args) {
//        if (log.isDebugEnabled()) {
//            log.debug("Script logging write to MinIO. - level: %s, '%s'", level, String.format(format, args));
//        }
//        try {
//            minioManager.writeObject(logObjectPrefix);
//        } catch (Exception e) {
//            log.error(String.format("Unable not script logging write to MinIO. - level: %s, '%s'", level,
//                    String.format(format, args)), e);
//        }
//    }
//
//    public static String buildLogObjectPrefix(final @NotBlank String scenesCode, @NotBlank Long workflowId) {
//        hasTextOf(scenesCode, "scenesCode");
//        notNullOf(workflowId, "workflowId");
//        return DEFAULT_EXECUTOR_LOGGING_PREFIX.concat("/")
//                .concat(scenesCode)
//                .concat("/")
//                .concat(valueOf(workflowId))
//                .concat(".log");
//    }
//
//}
