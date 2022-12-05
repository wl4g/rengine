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
package com.wl4g.rengine.evaluator.health;

import static java.lang.String.format;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.evaluator.minio.MinioManager;

import io.minio.BucketExistsArgs;

/**
 * {@link MinioReadyHealthCheck}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/smallrye-health
 */
@Readiness
@Singleton
public class MinioReadyHealthCheck implements HealthCheck {

    /**
     * Date formatter, the same as used by Quarkus. This enables users to
     * quickly compare the date printed by the probe with the logs.
     */
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS")
            .withZone(ZoneId.systemDefault());

    @Inject
    MinioManager minioManager;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Rengine evaluator MinIO connections health check").up();
        try {
            if (minioManager.getMinioClient()
                    .bucketExists(BucketExistsArgs.builder()
                            // .extraHeaders(Collections.emptyMap())
                            .bucket(RengineConstants.DEF_MINIO_BUCKET)
                            .build())) {
                builder.up();
            } else {
                builder.down().withData("message", format("No found MinIO bucket: %s", RengineConstants.DEF_MINIO_BUCKET));
            }
        } catch (Exception e) {
            builder.down().withData("message", format("Failed to found MinIO bucket: %s", RengineConstants.DEF_MINIO_BUCKET));
        }
        return builder.build();
    }

}