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

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getIntProperty;
import static com.wl4g.infra.common.lang.StringUtils2.getFilename;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

import com.google.common.io.Resources;
import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.executor.minio.MinioConfig.IOkHttpClientConfig;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.quarkus.runtime.StartupEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * {@link MinioManager}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 */
@Slf4j
@Getter
@Singleton
public class MinioManager {

    @Inject
    @NotNull
    MinioConfig config;

    @NotNull
    MinioClient minioClient;

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

    public ObjectResource loadObject(
            @NotNull UploadType uploadType,
            @NotBlank String objectPrefix,
            @NotBlank String scenesCode,
            boolean binary,
            boolean useCache) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
            InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {

        File localFile = determineLocalFile(uploadType, objectPrefix, scenesCode);

        // Gets from local cached.
        if (useCache && localFile.exists() && localFile.length() > 0) {
            return new ObjectResource(objectPrefix, binary, localFile, safeLongToInt(localFile.length()));
        }

        // Gets from MinIO brokers.
        GetObjectArgs args = GetObjectArgs.builder().bucket(config.bucket()).region(config.region()).object(objectPrefix).build();
        try (GetObjectResponse result = minioClient.getObject(args);) {
            int available = result.available();
            isTrue(available <= OBJECT_MAX_READABLE_SIZE, "Maximum file object readable limit exceeded: %s",
                    OBJECT_MAX_READABLE_SIZE);

            try (FileOutputStream out = new FileOutputStream(localFile, false);
                    BufferedOutputStream bout = new BufferedOutputStream(out, OBJECT_READ_BUFFER_SIZE);) {
                // ByteArrayOutputStream out = new ByteArrayOutputStream(4092);
                // result.transferTo(out);
                // out.toByteArray();
                result.transferTo(bout);
                return new ObjectResource(objectPrefix, binary, localFile, available);
            }
        }
    }

    public void writeObject(@NotBlank String objectPrefix) {
        try {
            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(config.bucket())
                    .region(config.region())
                    .object(objectPrefix)
                    .build();
            ObjectWriteResponse result = minioClient.putObject(args);

            // minio 不支持追加写??? 参考 zadig 的日志写入机制?

        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static File determineLocalFile(@NotNull UploadType uploadType, @NotBlank String objectPrefix, @NotBlank String scenesCode)
            throws IOException {
        notNullOf(uploadType, "uploadType");
        hasTextOf(objectPrefix, "objectPrefix");
        hasTextOf(scenesCode, "scenesCode");

        File localFile = new File("/tmp/__tmp_rengine_files_caches/".concat(uploadType.name())
                .concat("/")
                .concat(scenesCode)
                .concat("/")
                .concat(getFilename(objectPrefix)));
        FileIOUtils.forceMkdirParent(localFile);
        return localFile;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class ObjectResource {
        private @NotBlank String objectPrefix;
        private @NotNull boolean binary;
        private @NotNull File localFile;
        private @NotNull int available;

        public byte[] readToBytes() throws IOException {
            isTrue(available <= OBJECT_MAX_READABLE_SIZE, "Maximum file object readable limit exceeded: %s",
                    OBJECT_MAX_READABLE_SIZE);
            return Resources.toByteArray(localFile.toURI().toURL());
        }

        public String readToString() throws IOException {
            return new String(readToBytes(), UTF_8);
        }
    }

    public static final int OBJECT_READ_BUFFER_SIZE = getIntProperty("OBJECT_READ_BUFFER_SIZE", 4 * 1024);
    public static final int OBJECT_MAX_READABLE_SIZE = getIntProperty("OBJECT_MAX_READABLE_SIZE", 10 * 1024 * 1024);

}
