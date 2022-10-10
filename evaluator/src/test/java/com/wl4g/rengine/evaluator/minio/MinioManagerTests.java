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
package com.wl4g.rengine.evaluator.minio;

import java.io.File;
import java.io.IOException;
import java.net.Proxy.Type;
import java.time.Duration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.junit.Test;

import com.wl4g.rengine.common.entity.UploadObject.UploadType;

/**
 * {@link MinioManagerTests}
 * 
 * @author James Wong
 * @version 2022-10-09
 * @since v3.0.0
 */
public class MinioManagerTests {

    @Test
    public void testDetermineLocalFile() throws IOException {
        File localFile = MinioManager.determineLocalFile(UploadType.USER_LIBRARY_WITH_JS,
                "/rengine/library/js/commons-lang-3.0.0.js", "iot_generic_temp_warning");
        System.out.println(localFile);
    }

    public static MinioManager createDefaultMinioManager() {
        MinioManager minioManager = new MinioManager();
        minioManager.config = new MinioConfig() {

            @Override
            public @NotBlank String tenantSecretKey() {
                return "rengine";
            }

            @Override
            public @NotBlank String tenantAccessKey() {
                return "12345678";
            }

            @Override
            public @NotBlank String region() {
                return "us-east-1";
            }

            @Override
            public @NotNull IOkHttpClientConfig httpClient() {
                return new IOkHttpClientConfig() {

                    @Override
                    public Duration writeTimeout() {
                        return Duration.ofSeconds(5);
                    }

                    @Override
                    public Duration readTimeout() {
                        return Duration.ofSeconds(30);
                    }

                    @Override
                    public Duration connectTimeout() {
                        return Duration.ofSeconds(3);
                    }

                    @Override
                    public IProxy proxy() {
                        return new IProxy() {

                            @Override
                            public Type type() {
                                return Type.DIRECT;
                            }

                            @Override
                            public int port() {
                                // TODO Auto-generated method stub
                                return 0;
                            }

                            @Override
                            public String address() {
                                // TODO Auto-generated method stub
                                return null;
                            }
                        };
                    }

                };
            }

            @Override
            public @NotBlank String endpoint() {
                return "http://localhost:9000";
            }

            @Override
            public @NotBlank String bucket() {
                return "rengine";
            }
        };

        minioManager.onStart(null);
        return minioManager;
    }

}
