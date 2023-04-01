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
package com.wl4g.rengine.executor.execution.sdk;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_EXECUTOR_SCRIPT_S3_OBJECT_PREFIX;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_failure;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_success;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_time;
import static com.wl4g.rengine.executor.meter.RengineExecutorMeterService.MetricsName.execution_sdk_client_total;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.executor.meter.MeterUtil;

import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.UploadObjectArgs;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

/**
 * {@link ScriptS3Client}
 * 
 * @author James Wong
 * @version 2022-09-25
 * @since v1.0.0
 */
@ToString
public class ScriptS3Client {
    final static String METHOD_DOWNLOAD_OBJECT = "downloadObject";
    final static String METHOD_UPLOAD_OBJECT = "uploadObject";
    final static String METHOD_REMOVE_OBJECT = "removeObject";
    final static String METHOD_GET_OBJECT_AS_STRING = "getObjectAsString";
    final static String METHOD_STAT_OBJECT = "statObject";

    final String endpoint;
    final String defaultRegion;
    final String defaultBucket;
    final String accessKey;
    final String accessSecret;
    final MinioClient minioClient;

    public @HostAccess.Export ScriptS3Client(@NotBlank String endpoint, @NotBlank String defaultRegion,
            @NotBlank String defaultBucket, @NotBlank String accessKey, @NotBlank String accessSecret) {
        this(endpoint, defaultRegion, defaultBucket, accessKey, accessSecret, 6000, 900000, 900000);
    }

    public @HostAccess.Export ScriptS3Client(@NotBlank String endpoint, @NotBlank String defaultRegion,
            @NotBlank String defaultBucket, @NotBlank String accessKey, @NotBlank String accessSecret,
            @NotNull @Min(DEFAULT_MIN_CONNECT_TIMEOUT) @Max(DEFAULT_MAX_CONNECT_TIMEOUT) Integer connectTimeout,
            @NotNull @Min(DEFAULT_MIN_READ_TIMEOUT) @Max(DEFAULT_MAX_READ_TIMEOUT) Integer readTimeout,
            @NotNull @Min(DEFAULT_MIN_WRITE_TIMEOUT) @Max(DEFAULT_MAX_WRITE_TIMEOUT) Integer writeTimeout) {
        notNullOf(connectTimeout, "connectTimeout");
        notNullOf(readTimeout, "readTimeout");
        notNullOf(writeTimeout, "writeTimeout");
        this.endpoint = hasTextOf(endpoint, "endpoint");
        this.defaultRegion = hasTextOf(defaultRegion, "defaultRegion");
        this.defaultBucket = hasTextOf(defaultBucket, "defaultBucket");
        this.accessKey = hasTextOf(accessKey, "accessKey");
        this.accessSecret = hasTextOf(accessSecret, "accessSecret");
        isTrueOf(connectTimeout >= DEFAULT_MIN_CONNECT_TIMEOUT && connectTimeout <= DEFAULT_MAX_CONNECT_TIMEOUT,
                format("connectTimeout>=%s and connectTimeout<=%s", DEFAULT_MIN_CONNECT_TIMEOUT, DEFAULT_MAX_CONNECT_TIMEOUT));
        isTrueOf(readTimeout >= DEFAULT_MIN_READ_TIMEOUT && readTimeout <= DEFAULT_MAX_READ_TIMEOUT,
                format("readTimeout>=%s and readTimeout<=%s", DEFAULT_MIN_READ_TIMEOUT, DEFAULT_MAX_READ_TIMEOUT));
        isTrueOf(writeTimeout >= DEFAULT_MIN_WRITE_TIMEOUT && writeTimeout <= DEFAULT_MAX_WRITE_TIMEOUT,
                format("writeTimeout>=%s and writeTimeout<=%s", DEFAULT_MIN_WRITE_TIMEOUT, DEFAULT_MAX_WRITE_TIMEOUT));

        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .region(defaultRegion)
                .credentials(accessKey, accessSecret)
                .httpClient(new OkHttpClient().newBuilder()
                        .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                        .writeTimeout(readTimeout, TimeUnit.MILLISECONDS)
                        .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                        .protocols(Arrays.asList(Protocol.HTTP_1_1))
                        .build())
                .build();
    }

    public @HostAccess.Export void downloadObject(@NotBlank String objectPrefix, @NotNull String downloadFile) {
        downloadObject(null, null, objectPrefix, downloadFile);
    }

    public @HostAccess.Export void downloadObject(
            @Nullable String region,
            @Nullable String bucket,
            @NotBlank String objectPrefix,
            @NotNull String downloadFile) {
        hasTextOf(objectPrefix, "objectPrefix");
        hasTextOf(downloadFile, "downloadFile");
        MeterUtil.counter(execution_sdk_client_total, ScriptS3Client.class, METHOD_DOWNLOAD_OBJECT);
        try {
            MeterUtil.timer(execution_sdk_client_time, ScriptS3Client.class, METHOD_DOWNLOAD_OBJECT, () -> {
                minioClient.downloadObject(DownloadObjectArgs.builder()
                        .region(isBlank(region) ? defaultRegion : region)
                        .bucket(isBlank(bucket) ? defaultBucket : bucket)
                        .object(wrapChrootPrefix(objectPrefix))
                        // .ssec(ssec)
                        .filename(downloadFile)
                        .build());
                return null;
            });
            MeterUtil.counter(execution_sdk_client_success, ScriptS3Client.class, METHOD_DOWNLOAD_OBJECT);
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptS3Client.class, METHOD_DOWNLOAD_OBJECT);
            throw ex;
        }
    }

    public @HostAccess.Export UploadObjectResp uploadObject(@NotBlank String objectPrefix, @NotNull String uploadFile) {
        return uploadObject(null, null, objectPrefix, uploadFile);
    }

    public @HostAccess.Export UploadObjectResp uploadObject(
            @Nullable String region,
            @Nullable String bucket,
            @NotBlank String objectPrefix,
            @NotNull String uploadFile) {
        hasTextOf(objectPrefix, "objectPrefix");
        hasTextOf(uploadFile, "uploadFile");
        MeterUtil.counter(execution_sdk_client_total, ScriptS3Client.class, METHOD_UPLOAD_OBJECT);
        final String _region = isBlank(region) ? defaultRegion : region;
        final String _bucket = isBlank(bucket) ? defaultBucket : bucket;
        try {
            final ObjectWriteResponse result = MeterUtil.timer(execution_sdk_client_time, ScriptS3Client.class,
                    METHOD_UPLOAD_OBJECT, () -> {
                        return minioClient.uploadObject(UploadObjectArgs.builder()
                                .region(_region)
                                .bucket(_bucket)
                                .object(wrapChrootPrefix(objectPrefix))
                                // see:io.minio.PutObjectBaseArgs.Builder#validateSizes
                                // see:io.minio.ObjectWriteArgs.MAX_PART_SIZE
                                .filename(uploadFile)
                                .build());
                    });
            MeterUtil.counter(execution_sdk_client_success, ScriptS3Client.class, METHOD_UPLOAD_OBJECT);
            return UploadObjectResp.builder()
                    .headers(result.headers().toMultimap())
                    .region(_region)
                    .bucket(_bucket)
                    .object(objectPrefix)
                    .etag(result.etag())
                    .versionId(result.versionId())
                    .build();
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptS3Client.class, METHOD_UPLOAD_OBJECT);
            throw ex;
        }
    }

    public @HostAccess.Export void removeObject(@NotBlank String objectPrefix) {
        removeObject(null, null, objectPrefix);
    }

    public @HostAccess.Export void removeObject(@Nullable String region, @Nullable String bucket, @NotBlank String objectPrefix) {
        hasTextOf(objectPrefix, "objectPrefix");
        MeterUtil.counter(execution_sdk_client_total, ScriptS3Client.class, METHOD_REMOVE_OBJECT);
        try {
            MeterUtil.timer(execution_sdk_client_time, ScriptS3Client.class, METHOD_REMOVE_OBJECT, () -> {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .region(isBlank(region) ? defaultRegion : region)
                        .bucket(isBlank(bucket) ? defaultBucket : bucket)
                        .object(wrapChrootPrefix(objectPrefix))
                        .build());
                return null;
            });
            MeterUtil.counter(execution_sdk_client_success, ScriptS3Client.class, METHOD_REMOVE_OBJECT);
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptS3Client.class, METHOD_REMOVE_OBJECT);
            throw ex;
        }
    }

    public @HostAccess.Export String getObjectAsString(@NotBlank String objectPrefix) {
        return getObjectAsString(null, null, objectPrefix);
    }

    public @HostAccess.Export String getObjectAsString(
            @Nullable String region,
            @Nullable String bucket,
            @NotBlank String objectPrefix) {
        hasTextOf(objectPrefix, "objectPrefix");
        MeterUtil.counter(execution_sdk_client_total, ScriptS3Client.class, METHOD_GET_OBJECT_AS_STRING);
        try {
            final var result = MeterUtil.timer(execution_sdk_client_time, ScriptS3Client.class, METHOD_GET_OBJECT_AS_STRING,
                    () -> {
                        try (GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                                .region(isBlank(region) ? defaultRegion : region)
                                .bucket(isBlank(bucket) ? defaultBucket : bucket)
                                .object(wrapChrootPrefix(objectPrefix))
                                .build());) {
                            int available = response.available();
                            if (available > DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT) {
                                throw new RengineException(format("Maximum file object readable limit exceeded: %s",
                                        DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT));
                            }
                            try (ByteArrayOutputStream out = new ByteArrayOutputStream(available);) {
                                response.transferTo(out);
                                return new String(out.toByteArray(), UTF_8);
                            }
                        }
                    });
            MeterUtil.counter(execution_sdk_client_success, ScriptS3Client.class, METHOD_GET_OBJECT_AS_STRING);
            return result;
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptS3Client.class, METHOD_GET_OBJECT_AS_STRING);
            throw ex;
        }
    }

    public @HostAccess.Export StatObjectResp statObject(@NotBlank String objectPrefix) {
        return statObject(null, null, objectPrefix);
    }

    public @HostAccess.Export StatObjectResp statObject(
            @Nullable String region,
            @Nullable String bucket,
            @NotBlank String objectPrefix) {
        hasTextOf(objectPrefix, "objectPrefix");
        final String _region = isBlank(region) ? defaultRegion : region;
        final String _bucket = isBlank(bucket) ? defaultBucket : bucket;
        MeterUtil.counter(execution_sdk_client_total, ScriptS3Client.class, METHOD_STAT_OBJECT);
        try {
            final StatObjectResponse result = MeterUtil.timer(execution_sdk_client_time, ScriptS3Client.class, METHOD_STAT_OBJECT,
                    () -> minioClient.statObject(StatObjectArgs.builder()
                            .region(_region)
                            .bucket(_bucket)
                            .object(wrapChrootPrefix(objectPrefix))
                            .build()));
            MeterUtil.counter(execution_sdk_client_success, ScriptS3Client.class, METHOD_STAT_OBJECT);
            return StatObjectResp.builder()
                    .headers(result.headers().toMultimap())
                    .region(_region)
                    .bucket(_bucket)
                    .object(objectPrefix)
                    .etag(result.etag())
                    .size(result.size())
                    .lastModified(nonNull(result.lastModified()) ? result.lastModified().toEpochSecond() : null)
                    .retentionMode(nonNull(result.retentionMode()) ? result.retentionMode().name() : null)
                    .retentionRetainUntilDate(
                            nonNull(result.retentionRetainUntilDate()) ? result.retentionRetainUntilDate().toString() : null)
                    .legalHold(nonNull(result.legalHold()) ? result.legalHold().status() : null)
                    .deleteMarker(result.deleteMarker())
                    .userMetadata(result.userMetadata())
                    .build();
        } catch (Throwable ex) {
            MeterUtil.counter(execution_sdk_client_failure, ScriptS3Client.class, METHOD_STAT_OBJECT);
            throw ex;
        }
    }

    /**
     * Wrap sandbox object prefix with chroot prefix.
     * 
     * @param objectPrefix
     * @return
     */
    public static final String wrapChrootPrefix(@NotBlank String objectPrefix) {
        hasTextOf(objectPrefix, "objectPrefix");
        return DEFAULT_EXECUTOR_SCRIPT_S3_OBJECT_PREFIX.concat("/").concat(objectPrefix);
    }

    /**
     * Unwap sandbox object prefix with chroot prefix.
     * 
     * @param file
     * @return
     */
    public static final String unwrapChrootPrefix(@NotBlank String objectPrefix) {
        notNullOf(objectPrefix, "objectPrefix");
        final int index = objectPrefix.indexOf(DEFAULT_EXECUTOR_SCRIPT_S3_OBJECT_PREFIX);
        if (index >= 0) {
            objectPrefix = objectPrefix.substring(index + DEFAULT_EXECUTOR_SCRIPT_S3_OBJECT_PREFIX.length());
        }
        return objectPrefix;
    }

    @SuperBuilder
    @NoArgsConstructor
    public static class UploadObjectResp extends ObjectRespBase {
        private String etag;
        private String versionId;

        public @HostAccess.Export String getEtag() {
            return etag;
        }

        public @HostAccess.Export String getVersionId() {
            return versionId;
        }
    }

    @SuperBuilder
    @NoArgsConstructor
    public static class StatObjectResp extends ObjectRespBase {
        private String etag;
        private long size;
        private long lastModified;
        private String retentionMode;
        private String retentionRetainUntilDate;
        private boolean legalHold;
        private boolean deleteMarker;
        private Map<String, String> userMetadata;

        public @HostAccess.Export String getEtag() {
            return etag;
        }

        public @HostAccess.Export long getSize() {
            return size;
        }

        public @HostAccess.Export long getLastModified() {
            return lastModified;
        }

        public @HostAccess.Export String getRetentionMode() {
            return retentionMode;
        }

        public @HostAccess.Export String getRetentionRetainUntilDate() {
            return retentionRetainUntilDate;
        }

        public @HostAccess.Export boolean getLegalHold() {
            return legalHold;
        }

        public @HostAccess.Export boolean isDeleteMarker() {
            return deleteMarker;
        }

        public @HostAccess.Export Map<String, String> getUserMetadata() {
            return userMetadata;
        }
    }

    @SuperBuilder
    @NoArgsConstructor
    public static class ObjectRespBase {
        private Map<String, List<String>> headers;
        private String bucket;
        private String region;
        private String object;

        public ObjectRespBase(Map<String, List<String>> headers, String bucket, String region, String object) {
            this.headers = headers;
            this.bucket = bucket;
            this.region = region;
            this.object = object;
        }

        public @HostAccess.Export Map<String, List<String>> headers() {
            return headers;
        }

        public @HostAccess.Export String bucket() {
            return bucket;
        }

        public @HostAccess.Export String region() {
            return region;
        }

        public @HostAccess.Export String object() {
            return object;
        }
    }

    public static final int DEFAULT_MIN_CONNECT_TIMEOUT = 100; // Default:min(100ms)
    public static final int DEFAULT_MAX_CONNECT_TIMEOUT = 5 * 60 * 1000; // Default:min(5min)
    public static final int DEFAULT_MIN_READ_TIMEOUT = 100; // Default:min(100ms)
    public static final int DEFAULT_MAX_READ_TIMEOUT = 15 * 60 * 1000; // Default:max(15min)
    public static final int DEFAULT_MIN_WRITE_TIMEOUT = 100; // Default:min(100ms)
    public static final int DEFAULT_MAX_WRITE_TIMEOUT = 15 * 60 * 1000; // Default:max(15min)
}
