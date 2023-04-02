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
package com.nextbreakpoint.flinkclient1_15.api;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.nextbreakpoint.flinkclient1_15.model.JarUploadResponseBody;

/**
 * {@link DefaultApiTests}
 * 
 * @author James Wong
 * @version 2023-04-03
 * @since v1.0.0
 */
public class DefaultApiTests {

    @Test
    public void testJarUpload() throws Throwable {
        final ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://localhost:8081");

        final JarUploadResponseBody upload = new DefaultApi(apiClient)
                .jarsUploadPost(new File("/tmp/__rengine_jars/rengine-job-base-1.0.0.jar-1680450371672.jar"));

        System.out.println(upload.getFilename());
    }

}
