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

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.wl4g.rengine.server.minio.MinioClientProperties.HttpClientConfig;

import io.minio.MinioClient;
import io.minio.MinioClient.Builder;
import io.minio.http.HttpUtils;
import okhttp3.OkHttpClient;

/**
 * {@link MinioClientAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v3.0.0
 * @see http://docs.minio.org.cn/docs/master/minio-sts-quickstart-guide
 */
public class MinioClientAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.minio")
    public MinioClientProperties minioClientProperties() {
        return new MinioClientProperties();
    }

    @Bean
    public MinioClient minioClient(MinioClientProperties config) {
        // see to: io.minio.MinioAsyncClient.Builder.build()#L3318
        HttpClientConfig hcc = config.getHttpClient();
        OkHttpClient httpClient = HttpUtils.newDefaultHttpClient(hcc.getConnectTimeout().toMillis(),
                hcc.getReadTimeout().toMillis(), hcc.getWriteTimeout().toMillis());

        Builder builder = MinioClient.builder()
                .endpoint(config.getEndpoint())
                /* .credentialsProvider(provider) */.credentials(config.getAccessKey(), config.getSecretKey())
                .httpClient(httpClient);
        if (isBlank(config.getRegion())) {
            builder.region(config.getRegion());
        }

        return builder.build();
    }

}
