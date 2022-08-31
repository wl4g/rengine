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
package com.wl4g.rengine.server.minio;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.wl4g.rengine.server.swagger.SpringDocOASProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * {@link SpringDocOASProperties}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MinioClientProperties {

    private String endpoint = "http://localhost:9000";

    private String accessKey = "minioadmin";

    private String secretKey = "minioadmin";

    private String region = "us-east-1";

    private String bucket = "rengine";

    private HttpClientConfig httpClient = new HttpClientConfig();

    private UserUploadAssumeConfig userUpload = new UserUploadAssumeConfig();

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class HttpClientConfig {
        // private int maxIdleConnections = 1024;
        // private int idleConnectionCount = 10;
        // private Duration keepAliveDuration = Duration.ofMillis(15);
        private Duration connectTimeout = Duration.ofMinutes(5);
        private Duration writeTimeout = Duration.ofMinutes(5);
        private Duration readTimeout = Duration.ofMinutes(5);
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class UserUploadAssumeConfig {

        private String assumeAccessKey = "rengine_library";

        private String assumeSecretKey = "12345678";

        private String objectPrefix = "/library";

        private Duration expiredDuration = Duration.ofMinutes(5);

        private String assumePolicyName = "rengine_library_policy";

        private List<String> assumePolicyActions = new ArrayList<>() {
            private static final long serialVersionUID = 1L;
            {
                add("s3:PutObject");
            }
        };
    }

}