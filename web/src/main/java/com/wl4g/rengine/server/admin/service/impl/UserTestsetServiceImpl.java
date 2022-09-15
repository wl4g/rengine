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

import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.wl4g.rengine.common.bean.mongo.UserLibrary;
import com.wl4g.rengine.server.admin.model.STSInfo;
import com.wl4g.rengine.server.admin.model.UploadApply;
import com.wl4g.rengine.server.admin.model.UploadApplyResult;
import com.wl4g.rengine.server.admin.service.UserTestsetService;
import com.wl4g.rengine.server.constants.RengineWebConstants.MongoCollectionDefinition;
import com.wl4g.rengine.server.minio.MinioClientManager;
import com.wl4g.rengine.server.minio.MinioClientProperties;

import io.minio.credentials.Credentials;

/**
 * {@link UserTestsetServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Service
public class UserTestsetServiceImpl implements UserTestsetService {

    private @Autowired MinioClientProperties config;
    private @Autowired MongoTemplate mongoTemplate;
    private @Autowired MinioClientManager minioManager;

    public UploadApplyResult apply(UploadApply model) {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // System.out.println(authentication);

        // Save metadata to mongo table.
        String objectPrefix = config.getUserUpload().getTestsetObjectPrefix() + "/" + model.getFilename();
        UserLibrary userlib = UserLibrary.builder()
                .objectPrefix(objectPrefix)
                .extension(model.getExtension())
                .size(model.getSize())
                .status(model.getStatus())
                .build();
        mongoTemplate.insert(userlib, MongoCollectionDefinition.TEST_DATASET.getName());

        // New create temporary STS credentials.
        try {
            Credentials credentials = minioManager.createSTSCredentials(objectPrefix);
            return UploadApplyResult.builder()
                    .sts(STSInfo.builder()
                            .accessKey(credentials.accessKey())
                            .secretKey(credentials.secretKey())
                            .sessionToken(credentials.sessionToken())
                            .region(config.getRegion())
                            .extension(config.getUserUpload().getLibraryExtensions())
                            .partSize(config.getUserUpload().getLibraryPartSize().toBytes())
                            .fileLimitSize(config.getUserUpload().getLibraryFileLimitSize().toBytes())
                            .build())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to create STS with assumeRole grant", e);
        }
    }

}
