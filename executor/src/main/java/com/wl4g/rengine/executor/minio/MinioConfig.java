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

import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_MINIO_BUCKET;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_MINIO_ENDPOINT;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_MINIO_REGION;

import java.net.Proxy;
import java.time.Duration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.executor.execution.EngineConfig;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.ConfigMapping.NamingStrategy;
import io.smallrye.config.WithDefault;

/**
 * {@link EngineConfig}
 * 
 * @author James Wong
 * @date 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/details-mappings
 * @see https://quarkus.io/guides/details-extending-support#custom-details-source
 */
@StaticInitSafe
@ConfigMapping(prefix = "minio", namingStrategy = NamingStrategy.KEBAB_CASE)
public interface MinioConfig {

    @WithDefault(DEFAULT_MINIO_ENDPOINT)
    @NotBlank
    String endpoint();

    @WithDefault(DEFAULT_MINIO_REGION)
    @NotBlank
    String region();

    @WithDefault(DEFAULT_MINIO_BUCKET)
    @NotBlank
    String bucket();

    @WithDefault("rengine")
    @NotBlank
    String accessKey();

    @WithDefault("12345678")
    @NotBlank
    String secretKey();

    @NotNull
    IOkHttpClientConfig httpClient();

    public static interface IOkHttpClientConfig {
        Duration connectTimeout();

        Duration writeTimeout();

        Duration readTimeout();

        // @WithConverter(ProxyConverter.class)
        IProxy proxy();
    }

    // public static class ProxyConverter implements Converter<Proxy> {
    // private static final long serialVersionUID = 9040916723004801304L;
    // @Override
    // public Proxy convert(final String value) {
    // return new Proxy(Proxy.Type.DIRECT, new InetSocketAddress("localhost",
    // 8889));
    // }
    // }

    public static interface IProxy {
        Proxy.Type type();

        String address();

        int port();
    }

}
