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
package com.wl4g.rengine.manager.minio;

import static com.wl4g.infra.common.minio.S3Policy.Action.GetBucketLocationAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.GetBucketPolicyStatusAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.GetObjectAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.GetObjectLegalHoldAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.ListAllMyBucketsAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.ListBucketAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.ListBucketMultipartUploadsAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.ListMultipartUploadPartsAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.PutObjectAction;
import static com.wl4g.infra.common.minio.S3Policy.Action.PutObjectLegalHoldAction;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.springframework.util.unit.DataSize.ofMegabytes;

import java.time.Duration;
import java.util.List;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import com.wl4g.infra.common.minio.OkHttpClientConfig;
import com.wl4g.infra.common.minio.S3Policy;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.manager.swagger.SpringDocOASProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * {@link SpringDocOASProperties}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Validated
public class MinioClientProperties implements InitializingBean {

    private OkHttpClientConfig httpClient = new OkHttpClientConfig();

    private @NotBlank String endpoint = RengineConstants.DEF_MINIO_ENDPOINT;

    private @Nullable String region = RengineConstants.DEF_MINIO_REGION;

    private @NotBlank String tenantAccessKey = "rengine";

    private @NotBlank String tenantSecretKey = "12345678";

    private UserUploadAssumeConfig userUpload = new UserUploadAssumeConfig();

    @Override
    public void afterPropertiesSet() throws Exception {
        validate();
    }

    protected void validate() {
        // // S3 limit: Must not start with '/'
        // if (getUserUpload().getLibraryObjectPrefix().startsWith("/")) {
        // throw new IllegalArgumentException(
        // format("Object prefix must not be '/' due to s3 specification
        // restrictions, But now it is: %s",
        // getUserUpload().getLibraryObjectPrefix()));
        // }
        // if (getUserUpload().getTestsetObjectPrefix().startsWith("/")) {
        // throw new IllegalArgumentException(
        // format("Object prefix must not be '/' due to s3 specification
        // restrictions, But now it is: %s",
        // getUserUpload().getTestsetObjectPrefix()));
        // }

        // S3 limit: PartSize must be at least 5MB
        if (getUserUpload().getLibraryPartSize().toMegabytes() < 5) {
            throw new IllegalArgumentException(
                    format("The minimum partSize is 5MB due to S3 specification limitations, But now it is: %s",
                            getUserUpload().getLibraryPartSize()));
        }
        if (getUserUpload().getTestsetPartSize().toMegabytes() < 5) {
            throw new IllegalArgumentException(
                    format("The minimum partSize is 5MB due to S3 specification limitations, But now it is: %s",
                            getUserUpload().getTestsetPartSize()));
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class UserUploadAssumeConfig {

        private @NotEmpty List<S3Policy.Action> stsPolicyActions = asList(GetBucketLocationAction, GetBucketPolicyStatusAction,
                ListBucketAction, ListAllMyBucketsAction, ListBucketMultipartUploadsAction, ListMultipartUploadPartsAction,
                PutObjectAction, PutObjectLegalHoldAction, GetObjectAction, GetObjectLegalHoldAction);

        /**
         * The adjusting partSize will affect whether the pubObject() of the
         * minio-js SDK enables multipart upload, that is, the interface
         * corresponding to s3:ListBucketMultipartUploads will be called, but
         * this interface must have read permission of the bucket root
         * directory? See the source codes:
         * 
         * @see https://github.com/minio/minio-js/blob/7.0.32/src/main/minio.js#L1212
         * @see https://github.com/minio/minio-js/blob/7.0.32/src/main/object-uploader.js#L75
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/router.go#L95
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/object-handler.go#L345
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/auth-handler.go#L391
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/iam.go#L1754
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/iam.go#L1680
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/iam.go#L1723
         */
        private DataSize libraryPartSize = ofMegabytes(10);

        private DataSize libraryFileLimitSize = ofMegabytes(10);

        /**
         * The adjusting partSize will affect whether the pubObject() of the
         * minio-js SDK enables multipart upload, that is, the interface
         * corresponding to s3:ListBucketMultipartUploads will be called, but
         * this interface must have read permission of the bucket root
         * directory? See the source codes:
         * 
         * @see https://github.com/minio/minio-js/blob/7.0.32/src/main/minio.js#L1212
         * @see https://github.com/minio/minio-js/blob/7.0.32/src/main/object-uploader.js#L75
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/router.go#L95
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/object-handler.go#L345
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/auth-handler.go#L391
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/iam.go#L1754
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/iam.go#L1680
         * @see https://github.com/minio/minio/blob/RELEASE.2022-08-26T19-53-15Z/cmd/iam.go#L1723
         */
        private DataSize testsetPartSize = ofMegabytes(10);

        private DataSize testsetFileLimitSize = ofMegabytes(10);

        private Duration expiredDuration = Duration.ofMinutes(5);

    }

}