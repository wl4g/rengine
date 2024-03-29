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
package com.wl4g.rengine.executor.execution.sdk.tools;

import org.junit.Test;

/**
 * {@link FilesTests}
 * 
 * @author James Wong
 * @date 2023-04-01
 * @since v1.0.0
 */
public class FilesTests {

    @Test
    public void testCreateFile() {
        Files.DEFAULT.createFile("/1.txt");
    }

    @Test
    public void testMkdirs() {
        Files.DEFAULT.mkdirs("/dir1");
    }

    @Test
    public void testListFiles() {
        var result = Files.DEFAULT.listFiles("/");
        System.out.println(result);
    }

    @Test
    public void testWriteFromString() {
        Files.DEFAULT.writeFromString("/1.txt", "abcdefghijklmnopqrstyvwxyz");
    }

    @Test
    public void testReadToString() {
        var result = Files.DEFAULT.readToString("/1.txt");
        System.out.println(result);
    }

    @Test
    public void testForceDelete() {
        Files.DEFAULT.forceDelete("/1.txt");
        Files.DEFAULT.forceDelete("/dir1");
    }

}
