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

import java.net.Proxy;
import java.time.Duration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.constants.RengineConstants;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.ConfigMapping.NamingStrategy;
import io.smallrye.config.WithDefault;

/**
 * {@link MinioConfig}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v3.0.0
 * @see https://quarkus.io/guides/config-mappings
 * @see https://quarkus.io/guides/config-extending-support#custom-config-source
 */
@StaticInitSafe
@ConfigMapping(prefix = "minio", namingStrategy = NamingStrategy.KEBAB_CASE)
public interface MinioConfig {

    @WithDefault(RengineConstants.DEF_MINIO_ENDPOINT)
    @NotBlank
    String endpoint();

    @WithDefault(RengineConstants.DEF_MINIO_REGION)
    @NotBlank
    String region();

    @WithDefault("rengine")
    @NotBlank
    String tenantAccessKey();

    @WithDefault("12345678")
    @NotBlank
    String tenantSecretKey();

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