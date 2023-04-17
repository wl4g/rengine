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
package com.wl4g.rengine.executor.minio;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.wl4g.rengine.common.entity.sys.UploadObject.UploadType;
import com.wl4g.rengine.executor.util.TestDefaultMinIOSetup;

/**
 * {@link MinioManagerIT}
 * 
 * @author James Wong
 * @date 2022-10-09
 * @since v1.0.0
 */
public class MinioManagerIT {

    @Test
    public void testDetermineLocalFile() throws IOException {
        File localFile = MinioManager.determineLocalFile(UploadType.LIBJS, "/rengine/library/js/commons-lang-3.0.0.js",
                101001001L);
        System.out.println(localFile);
    }

    public static MinioManager createDefaultMinioManager() {
        MinioManager minioManager = new MinioManager();
        minioManager.config = TestDefaultMinIOSetup.buildMinioConfigDefault();
        minioManager.onStart(null);
        return minioManager;
    }

}
