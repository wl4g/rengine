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
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.StringUtils2.getFilename;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_S3_OBJECT_READ_BUFFER;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_SCRIPT_CACHE_DIR;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.google.common.io.Resources;
import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.rengine.common.entity.sys.UploadObject.UploadType;
import com.wl4g.rengine.executor.minio.MinioConfig.IOkHttpClientConfig;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
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
            final @NotNull Long uploadId,
            final @NotNull UploadType uploadType,
            final @NotBlank String objectPrefix,
            final @NotNull Long workflowId,
            final boolean binary,
            final @Min(-1) Long objectCacheExpireMs)
            throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException,
            InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException, IOException {
        notNullOf(uploadId, "uploadId");
        notNullOf(uploadType, "uploadType");
        hasTextOf(objectPrefix, "objectPrefix");
        notNullOf(workflowId, "workflowId");
        isTrueOf(nonNull(objectCacheExpireMs) && objectCacheExpireMs >= -1, "objectCacheExpireMs >= -1");

        final File localFile = determineLocalFile(uploadType, objectPrefix, workflowId);

        // First get from local cached.
        if (objectCacheExpireMs > 0 && localFile.exists() && localFile.length() > 0) {
            // Check for expiration is valid.
            if ((currentTimeMillis() - localFile.lastModified()) < objectCacheExpireMs) {
                return new ObjectResource(uploadId, objectPrefix, binary, localFile, safeLongToInt(localFile.length()));
            }
            // Expired and clearup
            if (!localFile.delete()) {
                log.warn("Unable to remove expired script cached of {}", localFile);
            }
        }

        // Then gets from MinIO.
        final GetObjectArgs args = GetObjectArgs.builder()
                .bucket(config.bucket())
                .region(config.region())
                .object(objectPrefix)
                .build();
        try (GetObjectResponse result = minioClient.getObject(args);) {
            int available = result.available();
            isTrue(available <= DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT, "Maximum file object readable limit exceeded: %s",
                    DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT);

            try (FileOutputStream out = new FileOutputStream(localFile, false);
                    BufferedOutputStream bout = new BufferedOutputStream(out, DEFAULT_EXECUTOR_S3_OBJECT_READ_BUFFER);) {
                // ByteArrayOutputStream out = new ByteArrayOutputStream(4092);
                // result.transferTo(out);
                // out.toByteArray();
                result.transferTo(bout);
                return new ObjectResource(uploadId, objectPrefix, binary, localFile, available);
            }
        }
    }

    public ObjectWriteResponse putObject(
            final @NotBlank String objectPrefix,
            final @NotNull InputStream stream,
            final long objectSize,
            final long partSize) {
        hasTextOf(objectPrefix, "objectPrefix");
        notNullOf(stream, "stream");
        try {
            return minioClient.putObject(PutObjectArgs.builder()
                    .bucket(config.bucket())
                    .region(config.region())
                    .object(objectPrefix)
                    // see:io.minio.PutObjectBaseArgs.Builder#validateSizes
                    // see:io.minio.ObjectWriteArgs.MAX_PART_SIZE
                    .stream(stream, objectSize, partSize)
                    .build());
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public ObjectWriteResponse uploadObject(final @NotBlank String objectPrefix, final @NotNull String filename) {
        hasTextOf(objectPrefix, "objectPrefix");
        hasTextOf(filename, "filename");
        try {
            // UploadSnowballObjectsArgs
            return minioClient.uploadObject(UploadObjectArgs.builder()
                    .bucket(config.bucket())
                    .region(config.region())
                    .object(objectPrefix)
                    // see:io.minio.PutObjectBaseArgs.Builder#validateSizes
                    // see:io.minio.ObjectWriteArgs.MAX_PART_SIZE
                    .filename(filename)
                    .build());
        } catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
                | InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static File determineLocalFile(@NotNull UploadType uploadType, @NotBlank String objectPrefix, @NotNull Long workflowId)
            throws IOException {
        notNullOf(uploadType, "uploadType");
        hasTextOf(objectPrefix, "objectPrefix");
        notNullOf(workflowId, "workflowId");

        final File localFile = new File(DEFAULT_EXECUTOR_SCRIPT_CACHE_DIR.concat("/")
                .concat(uploadType.name())
                .concat("/")
                .concat(workflowId + "")
                .concat("/")
                .concat(getFilename(objectPrefix)));
        FileIOUtils.forceMkdirParent(localFile);
        return localFile;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class ObjectResource {
        private @NotNull Long uploadId;
        private @NotBlank String objectPrefix;
        private boolean binary;
        private @NotNull File localFile;
        private int available;

        public byte[] readToBytes() throws IOException {
            isTrue(available <= DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT, "Maximum file object readable limit exceeded: %s",
                    DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT);
            return Resources.toByteArray(localFile.toURI().toURL());
        }

        public String readToString() throws IOException {
            return new String(readToBytes(), UTF_8);
        }
    }

}
