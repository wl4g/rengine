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
package com.wl4g.rengine.service.deploy;

import static com.wl4g.infra.common.lang.EnvironmentUtil.getBooleanProperty;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getStringProperty;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wl4g.rengine.service.minio.RengineMinioPolicyTool;
import com.wl4g.rengine.service.minio.RengineMinioUploadTool;

import lombok.CustomLog;

/**
 * {@link MinioDeploymentInitializer}
 * 
 * @author James Wong
 * @version 2023-04-17
 * @since v1.0.0
 */
@CustomLog
@Component
public class MinioDeploymentInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (!getBooleanProperty("DEPLOY_MINIO_INIT_ENALBED", false)) {
            log.info("Disabled minio deployment initialzer!");
            return;
        }
        initMinioAccessPolicy();
        initMinioRuleScriptObjects();
    }

    static void initMinioAccessPolicy() throws Exception {
        log.info("Deployment initial minio policy ...");

        // @formatter:off
        RengineMinioPolicyTool.main(new String[] {
                "--endpoint",
                getStringProperty("DEPLOY_MINIO_INIT_ENDPOINT", "http://minio:9000"),
                "--region",
                getStringProperty("DEPLOY_MINIO_INIT_REGION", "us-east-1"),
                "--tenantBucket",
                getStringProperty("DEPLOY_MINIO_INIT_TENANT_BUCKET", "rengine"),
                "--adminAccessKey",
                getStringProperty("DEPLOY_MINIO_INIT_ADMIN_ACCESS_KEY", "minioadmin"),
                "--adminSecretKey",
                getStringProperty("DEPLOY_MINIO_INIT_ADMIN_SECRET_KEY", "minioadmin"),
                "--accessKey",
                getStringProperty("DEPLOY_MINIO_INIT_ACCESS_KEY", "rengine"),
                "--secretKey",
                getStringProperty("DEPLOY_MINIO_INIT_SECRET_KEY", "12345678")
           });
        // @formatter:on
    }

    static void initMinioRuleScriptObjects() throws Exception {
        log.info("Deployment initial minio rulescript objects ...");

        // @formatter:off
        RengineMinioUploadTool.main(new String[] {
                "--endpoint",
                getStringProperty("DEPLOY_MINIO_INIT_ENDPOINT", "http://minio:9000"),
                "--region",
                getStringProperty("DEPLOY_MINIO_INIT_REGION", "us-east-1"),
                "--bucket",
                getStringProperty("DEPLOY_MINIO_INIT_BUCKET", "rengine"),
                "--accessKey",
                getStringProperty("DEPLOY_MINIO_INIT_ACCESS_KEY", "rengine"),
                "--secretKey",
                getStringProperty("DEPLOY_MINIO_INIT_SECRET_KEY", "12345678"),
                "--locationPatterns",
                getStringProperty("DEPLOY_MINIO_INIT_LOCATION_PATTERNS", "classpath*:/example/rulescript/0/*/*.*"),
                "--fromLocationPath",
                getStringProperty("DEPLOY_MINIO_INIT_FROM_LOCATION_PATH", "/example/rulescript/"),
                "--prefix",
                getStringProperty("DEPLOY_MINIO_INIT_PREFIX", "rengine/libjs")
           });
        // @formatter:on
    }

}
