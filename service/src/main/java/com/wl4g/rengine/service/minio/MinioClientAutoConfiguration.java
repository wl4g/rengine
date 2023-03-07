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
package com.wl4g.rengine.service.minio;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.minio.MinioClient;
import okhttp3.OkHttpClient;

/**
 * {@link MinioClientAutoConfiguration}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 * @see http://docs.minio.org.cn/docs/master/minio-sts-quickstart-guide
 */
@ConditionalOnClass({ MinioClient.class })
public class MinioClientAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "minio")
    public MinioClientProperties minioClientProperties() {
        return new MinioClientProperties();
    }

    @Bean(BEAN_MINIO_OKHTTPCLIENT)
    @ConditionalOnMissingBean
    public OkHttpClient minioOkHttpClient(MinioClientProperties config) {
        // see to: io.minio.MinioAsyncClient.Builder.build()#L3318
        return config.getHttpClient().newOkHttpClient();
    }

    @Bean
    public MinioClient minioClient(
            MinioClientProperties properties,
            @Qualifier(BEAN_MINIO_OKHTTPCLIENT) OkHttpClient httpClient) {
        io.minio.MinioClient.Builder builder = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                // .credentialsProvider(provider)
                .credentials(properties.getTenantAccessKey(), properties.getTenantSecretKey())
                .httpClient(httpClient);
        if (isBlank(properties.getRegion())) {
            builder.region(properties.getRegion());
        }
        return builder.build();
    }

    // @Bean
    // public MinioAdminClient minioAdminClient(
    // MinioClientProperties details,
    // @Qualifier(BEAN_MINIO_OKHTTPCLIENT) OkHttpClient httpClient) {
    // MinioAdminClient.Builder builder = MinioAdminClient.builder()
    // .endpoint(details.getEndpoint())
    // //.credentialsProvider(provider)
    // .credentials(details.getTenantAccessKey(),
    // details.getTenantSecretKey())
    // .httpClient(httpClient);
    // if (isBlank(details.getRegion())) {
    // builder.region(details.getRegion());
    // }
    // return builder.build();
    // }

    @Bean
    public MinioClientManager minioClientManager(
            MinioClientProperties config,
            MinioClient minioClient,
            @Qualifier(BEAN_MINIO_OKHTTPCLIENT) OkHttpClient httpClient) {
        return new MinioClientManager(config, minioClient, httpClient);
    }

    public final static String BEAN_MINIO_OKHTTPCLIENT = "defaultMinioOkHttpClient";

}
