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

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.evaluator.minio.MinioConfig.IOkHttpClientConfig;
import com.wl4g.rengine.evaluator.minio.MinioConfig.IProxy;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * {@link MinioManager}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v3.0.0
 */
@Slf4j
@Getter
@ApplicationScoped
public class MinioManager {

    private @Inject MinioConfig config;

    private @NotNull MinioClient minioClient;

    @PostConstruct
    public void init() {
        IOkHttpClientConfig httpClient = config.httpClient();
        IProxy proxy = httpClient.proxy();
        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(config.endpoint())
                .credentials(config.tenantAccessKey(), config.tenantSecretKey())
                .httpClient(new OkHttpClient().newBuilder()
                        .connectTimeout(httpClient.connectTimeout().toMillis(), TimeUnit.MILLISECONDS)
                        .writeTimeout(config.httpClient().readTimeout().toMillis(), TimeUnit.MILLISECONDS)
                        .readTimeout(config.httpClient().writeTimeout().toMillis(), TimeUnit.MILLISECONDS)
                        .protocols(Arrays.asList(Protocol.HTTP_1_1))
                        .proxy(new Proxy(proxy.type(), new InetSocketAddress(proxy.address(), proxy.port())))
                        .build());
        if (isBlank(config.region())) {
            builder.region(config.region());
        }
        this.minioClient = builder.build();
        log.info("Initializated Minio Client: {}", minioClient);
    }

}
