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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.FastTimeClock.currentTimeMillis;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

import java.security.NoSuchAlgorithmException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.infra.common.io.ByteStreamUtils;
import com.wl4g.infra.common.minio.S3Policy;
import com.wl4g.infra.common.minio.S3Policy.EffectType;
import com.wl4g.infra.common.minio.S3Policy.Statement;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.service.minio.MinioClientProperties.UserUploadAssumeConfig;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.credentials.AssumeRoleProvider;
import io.minio.credentials.Credentials;
import lombok.AllArgsConstructor;
import lombok.Getter;
import okhttp3.OkHttpClient;

/**
 * {@link MinioClientManager}
 * 
 * @author James Wong
 * @version 2022-08-31
 * @since v1.0.0
 */
@Getter
@AllArgsConstructor
public class MinioClientManager implements ApplicationRunner {
    private final MinioClientProperties config;
    private final MinioClient minioClient;
    private final OkHttpClient httpClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // log.info("Initialization ...");
    }

    public byte[] getObjectToByteArray(final String objectPrefix) throws Exception {
        try (GetObjectResponse response = getMinioClient().getObject(GetObjectArgs.builder()
                .bucket(RengineConstants.DEFAULT_MINIO_BUCKET)
                .region(getConfig().getRegion())
                .object(objectPrefix)
                .build());) {
            return ByteStreamUtils.copyToByteArray(response);
        }
    }

    public Credentials createSTSCredentials(String fullObjectPrefix) throws NoSuchAlgorithmException {
        hasTextOf(fullObjectPrefix, "fullObjectPrefix");

        // e.g: bucket01//sub01/1.txt => bucket1/sub01/1.txt
        String stsRoleArn = format("arn:aws:s3:::%s", fullObjectPrefix).replaceAll("\\/\\/", "\\/");
        UserUploadAssumeConfig uploadConfig = config.getUserUpload();
        String roleSessionName = "rengine-" + currentTimeMillis();

        return createSTSCredentialsWithAssumeRole(config.getEndpoint(), config.getTenantAccessKey(), config.getTenantSecretKey(),
                config.getRegion(), stsRoleArn, safeLongToInt(uploadConfig.getExpiredDuration().getSeconds()), roleSessionName,
                null);
    }

    /**
     * Create STS credentials via assumeRole provider.
     * 
     * @param stsEndpoint
     * @param tenantAccessKey
     * @param tenantSecretKey
     * @param region
     * @param stsRoleArn
     * @param durationSeconds
     * @param roleSessionName
     * @param externalId
     * @return
     * @throws NoSuchAlgorithmException
     */
    public Credentials createSTSCredentialsWithAssumeRole(
            String stsEndpoint,
            String tenantAccessKey,
            String tenantSecretKey,
            String region,
            String stsRoleArn,
            int durationSeconds,
            String roleSessionName,
            String externalId) throws NoSuchAlgorithmException {
        return new AssumeRoleProvider(stsEndpoint, tenantAccessKey, tenantSecretKey, durationSeconds,
                toJSONString(S3Policy.builder()
                        .version(S3Policy.DEFAULT_POLICY_VERSION)
                        .statement(singletonList(Statement.builder()
                                .effect(EffectType.Allow)
                                .action(config.getUserUpload().getStsPolicyActions())
                                .resource(singletonList(stsRoleArn))
                                .build()))
                        .build()),
                region, null, null, null, httpClient).fetch();
    }

}
