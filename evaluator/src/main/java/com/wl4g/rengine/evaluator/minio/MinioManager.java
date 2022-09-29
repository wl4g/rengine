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

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.evaluator.minio.MinioConfig.IOkHttpClientConfig;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.quarkus.runtime.StartupEvent;
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
// @ApplicationScoped
@Singleton
public class MinioManager {

    private @Inject MinioConfig config;

    private @NotNull MinioClient minioClient;

    void onStart(@Observes StartupEvent event) {
        IOkHttpClientConfig httpClient = config.httpClient();
        Proxy proxy = httpClient.proxy().type() == Type.DIRECT ? Proxy.NO_PROXY
                : new Proxy(httpClient.proxy().type(),
                        new InetSocketAddress(httpClient.proxy().address(), httpClient.proxy().port()));

        MinioClient.Builder builder = MinioClient.builder()
                .endpoint(config.endpoint())
                .credentials(config.tenantAccessKey(), config.tenantSecretKey())
                .httpClient(new OkHttpClient().newBuilder()
                        .connectTimeout(httpClient.connectTimeout().toMillis(), TimeUnit.MILLISECONDS)
                        .writeTimeout(config.httpClient().readTimeout().toMillis(), TimeUnit.MILLISECONDS)
                        .readTimeout(config.httpClient().writeTimeout().toMillis(), TimeUnit.MILLISECONDS)
                        .protocols(Arrays.asList(Protocol.HTTP_1_1))
                        .proxy(proxy)
                        .build());
        if (isBlank(config.region())) {
            builder.region(config.region());
        }
        this.minioClient = builder.build();
        log.info("Initializated Minio Client: {}", minioClient);
    }

    public String getObjectAsText(@NotNull UploadType type, @NotBlank String objectName)
            throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
            InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
        notNullOf(type, "uploadType");
        return getObjectAsText(type.getPrefix().concat("/").concat(objectName));
    }

    public String getObjectAsText(@NotBlank String objectPrefix)
            throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
            InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
        hasTextOf(objectPrefix, "objectPrefix");
        try (GetObjectResponse result = minioClient.getObject(
                GetObjectArgs.builder().bucket(config.bucket()).region(config.region()).object(objectPrefix).build());) {
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            result.transferTo(out);
            return out.toString(UTF_8);
        }
    }

}
