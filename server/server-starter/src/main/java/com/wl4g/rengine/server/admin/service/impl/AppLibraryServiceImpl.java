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
package com.wl4g.rengine.server.admin.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wl4g.rengine.common.bean.mongo.AppLibrary;
import com.wl4g.rengine.server.admin.model.UploadApplyModel;
import com.wl4g.rengine.server.admin.model.UploadApplyResultModel;
import com.wl4g.rengine.server.admin.model.UploadApplyModel.Metadata;
import com.wl4g.rengine.server.admin.service.AppLibraryService;

import io.minio.MinioClient;
import io.minio.credentials.Provider;
import io.minio.credentials.WebIdentityProvider;

/**
 * {@link AppLibraryServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Service
public class AppLibraryServiceImpl implements AppLibraryService {

    private @Autowired MongoTemplate mongoTemplate;

    // see:http://docs.minio.org.cn/docs/master/minio-sts-quickstart-guide
    // see:https://github.com/minio/minio/blob/master/docs/sts/keycloak.md
    private @Autowired MinioClient minioClient;

    public UploadApplyResultModel apply(UploadApplyModel model) {
        // TODO
        AppLibrary applib = AppLibrary.builder().url("").accessMode("").extension("").owner("").group("").filename("").build();
        mongoTemplate.insert(applib, "app_library");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication);

        // new WebIdentityProvider(supplier, "stsEndpoint", durationSeconds,
        // policy, roleArn, roleSessionName, customHttpClient);

        return UploadApplyResultModel.builder()
                .metadata(Metadata.builder().accessMode("").extension("").owner("").group("").filename("").build())
                .build();
    }

}
