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

import static com.wl4g.infra.common.lang.FastTimeClock.currentTimeMillis;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.wl4g.infra.common.minio.S3Policy;
import com.wl4g.infra.common.minio.S3Policy.EffectType;
import com.wl4g.infra.common.minio.S3Policy.Statement;
import com.wl4g.infra.common.minio.v8_4.MinioAdminClient;
import com.wl4g.infra.common.minio.v8_4.UserInfo.Status;
import com.wl4g.rengine.server.minio.MinioClientProperties.UserUploadAssumeConfig;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.credentials.AssumeRoleProvider;
import io.minio.credentials.Credentials;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

/**
 * {@link MinioClientManager}
 * 
 * @author James Wong
 * @version 2022-08-31
 * @since v3.0.0
 */
@Slf4j
@AllArgsConstructor
public class MinioClientManager implements ApplicationRunner {

    private final MinioClientProperties config;
    private final OkHttpClient httpClient;
    private final MinioClient minioClient;
    private final MinioAdminClient adminClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initBucket();
        initSTSPolicy();
        initSTSUser();
    }

    public Credentials createUploadingLibrarySTSWithAssumeRole() throws NoSuchAlgorithmException {
        UserUploadAssumeConfig uploadConfig = config.getUserUpload();
        String roleArn = format("arn:aws:s3:::%s", uploadConfig.getObjectPrefix()); // TODO+suffix
        return createSTSCredentialsWithAssumeRole(config.getEndpoint(), uploadConfig.getAssumeAccessKey(),
                uploadConfig.getAssumeSecretKey(), roleArn, safeLongToInt(uploadConfig.getExpiredDuration().toSeconds()), null);
    }

    public Credentials createSTSCredentialsWithAssumeRole(
            String stsEndpoint,
            String accessKey,
            String secretKey,
            String roleArn,
            int durationSeconds,
            String externalId) throws NoSuchAlgorithmException {
        S3Policy applyPolicy = S3Policy.builder()
                .version(S3Policy.DEFAULT_POLICY_VERSION)
                .statement(singletonList(Statement.builder()
                        .effect(EffectType.Allow)
                        .action(singletonList("s3:PutObject"))
                        .resource(singletonList(roleArn))
                        .build()))
                .build();
        String region = "us-east-1";
        String roleSessionName = "rengine-" + currentTimeMillis();

        return new AssumeRoleProvider(stsEndpoint, accessKey, secretKey, durationSeconds, toJSONString(applyPolicy), region,
                roleArn, roleSessionName, externalId, httpClient).fetch();
    }

    private void initBucket() throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(config.getBucket()).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(config.getBucket()).build());
            log.info("Initialization created bucket: {}", config.getBucket());
        } else {
            log.info("Initialization already bucket: {}", config.getBucket());
        }
    }

    private void initSTSPolicy() throws InvalidKeyException, NoSuchAlgorithmException, InvalidCipherTextException, IOException {
        String assumeResourceName = "arn:aws:s3:::".concat(config.getBucket())
                .concat("/")
                .concat(config.getUserUpload().getObjectPrefix())
                .concat("/*")
                // e.g: bucket1//subdir1 => bucket1/subdir1
                .replaceAll("\\/\\/", "\\/");
        S3Policy assumePolicy = S3Policy.builder()
                .version(S3Policy.DEFAULT_POLICY_VERSION)
                .statement(singletonList(Statement.builder()
                        .effect(EffectType.Allow)
                        .action(config.getUserUpload().getAssumePolicyActions())
                        .resource(singletonList(assumeResourceName))
                        .build()))
                .build();
        adminClient.addCannedPolicy(config.getUserUpload().getAssumePolicyName(), toJSONString(assumePolicy));
        log.info("Initialization created assume policy for: {}", toJSONString(config.getUserUpload()));
    }

    private void initSTSUser() throws InvalidKeyException, NoSuchAlgorithmException, InvalidCipherTextException, IOException {
        adminClient.addUser(config.getUserUpload().getAssumeAccessKey(), Status.ENABLED,
                config.getUserUpload().getAssumeSecretKey(), config.getUserUpload().getAssumePolicyName(), emptyList());
        log.info("Initialization created assume user: {}", config.getUserUpload().getAssumeAccessKey());
    }

}
