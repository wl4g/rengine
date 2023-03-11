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
package com.wl4g.rengine.common.util;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.infra.common.io.FileIOUtils.ReadTailFrame;

/**
 * {@link ScriptEngineUtil}
 * 
 * @author James Wong
 * @version 2023-01-08
 * @since v1.0.0
 */
public abstract class ScriptEngineUtil {

    public static String buildScriptLogFilePattern(
            final @NotBlank String logBaseDir,
            final @NotNull Long workflowId,
            final boolean isStdErr) {
        hasTextOf(logBaseDir, "logBaseDir");
        notNullOf(workflowId, "workflowId");
        return buildScriptLogDir(logBaseDir, workflowId).concat(File.separator)
                .concat(isStdErr ? DEFAULT_STDERR_PREFIX : DEFAULT_STDOUT_PREFIX);
    }

    public static String buildScriptLogDir(final @NotBlank String logBaseDir, final @NotNull Long workflowId) {
        hasTextOf(logBaseDir, "logBaseDir");
        notNullOf(workflowId, "workflowId");
        return logBaseDir.concat(File.separator).concat(valueOf(workflowId));
    }

    public static List<String> getAllLogDirs(final @NotBlank String logBaseDir, boolean isAbsolute) {
        return safeArrayToList(new File(logBaseDir).list(ScriptEngineUtil.defaultLogFilter)).stream()
                .map(fn -> isAbsolute ? logBaseDir.concat(File.separator).concat(fn) : fn)
                .collect(toList());
    }

    public static List<String> getAllLogFilenames(
            final @NotBlank String logBaseDir,
            final @NotNull Long workflowId,
            final boolean isStdErr) {
        final var logDir = new File(buildScriptLogDir(logBaseDir, workflowId));
        final String prefix = isStdErr ? DEFAULT_STDERR_PREFIX : DEFAULT_STDOUT_PREFIX;
        // for example:
        // ls -al /tmp/__rengine_script_log/6150868953448448/
        // stderr.log.0
        // stderr.log.0.1
        // stderr.log.0.1.lck
        // stderr.log.0.lck
        // stdout.log.0
        // stdout.log.0.1
        // stdout.log.0.1.lck
        // stdout.log.0.lck
        return safeArrayToList(logDir.list(defaultLogFilter)).stream()
                .filter(f -> !isBlank(f) && f.contains(prefix))
                .collect(toList());
    }

    public static String getLatestLogFile(
            final @NotBlank String logBaseDir,
            final @NotNull Long workflowId,
            final boolean isStdErr) {
        final var logDir = new File(buildScriptLogDir(logBaseDir, workflowId));
        final String prefix = isStdErr ? DEFAULT_STDERR_PREFIX : DEFAULT_STDOUT_PREFIX;

        final List<String> sortedLogFiles = getAllLogFilenames(logBaseDir, workflowId, isStdErr).stream()
                .filter(f -> !equalsAnyIgnoreCase(f, DEFAULT_STDOUT_PREFIX, DEFAULT_STDERR_PREFIX))
                .collect(toMap(f -> f, f -> {
                    final String withoutPrefix = f.substring(f.lastIndexOf(prefix) + 1 + prefix.length());
                    return Integer.parseInt(replace(withoutPrefix, ".", ""));
                }))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> e1.getValue() - e2.getValue())
                .map(e -> e.getKey())
                .collect(toList());

        if (CollectionUtils2.isEmpty(sortedLogFiles)) {
            return null;
        }

        return logDir.getAbsolutePath().concat(File.separator).concat(sortedLogFiles.get(sortedLogFiles.size() - 1));
    }

    public static ReadTailFrame getLogTail(
            final @NotBlank String latestLogFile,
            final boolean isStdErr,
            long startPos,
            int aboutLimit) {
        return FileIOUtils.seekReadLines(latestLogFile, startPos, aboutLimit, line -> startsWithIgnoreCase(line, "EOF"));
    }

    public static final String DEFAULT_STDOUT_PREFIX = "stdout.log";
    public static final String DEFAULT_STDERR_PREFIX = "stderr.log";

    public static final FilenameFilter defaultLogFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return !endsWithIgnoreCase(name, ".lck") && !dir.isHidden();
        }
    };

}
