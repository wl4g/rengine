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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.infra.common.io.FileIOUtils.ReadTailFrame;

/**
 * {@link ScriptEngineUtilTests}
 * 
 * @author James Wong
 * @date 2023-01-11
 * @since v1.0.0
 */
public class ScriptEngineUtilTests {

    static String logBaseDir = "/tmp/__rengine_script_log";
    static Long workflowId = 101000101L;

    @BeforeClass
    public static void init() throws IOException {
        final var logFilePattern = ScriptEngineUtil.buildScriptLogFilePattern(logBaseDir, workflowId, false);
        FileIOUtils.touch(new File(logFilePattern + ".0"));
        FileIOUtils.touch(new File(logFilePattern + ".0.1"));
        FileIOUtils.touch(new File(logFilePattern + ".0.2"));
        FileIOUtils.touch(new File(logFilePattern + ".1.1"));
        final File file = new File(logFilePattern + ".1.2");
        // try {
        // FileIOUtils.forceDelete(file);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        FileIOUtils.touch(file);
        for (int i = 0; i < 100; i++) {
            FileIOUtils.writeALineFile(file, "The data xxxx of " + i);
        }
    }

    @Test
    public void testGetAllLogFilenames() throws IOException {
        final List<String> allLogFilenames = ScriptEngineUtil.getAllLogFilenames(logBaseDir, workflowId, false);
        System.out.println(allLogFilenames);
    }

    @Test
    public void testGetLatestLogFile() throws IOException {
        final String latestLogFile = ScriptEngineUtil.getLatestLogFile(logBaseDir, workflowId, false);
        System.out.println(latestLogFile);
        assert latestLogFile.equals("/tmp/__rengine_script_log/101000101/stdout.log.1.2");
    }

    @Test
    public void testGetLogTail() throws IOException {
        final String latestLogFile = ScriptEngineUtil.getLatestLogFile(logBaseDir, workflowId, false);
        final ReadTailFrame tail = ScriptEngineUtil.getLogTail(latestLogFile, false, 0, 100);
        System.out.println(tail);
    }

}
