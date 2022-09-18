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
package com.wl4g.rengine.manager.admin.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.rengine.common.bean.UploadObject;
import com.wl4g.rengine.common.bean.UploadObject.BizType;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.manager.admin.model.AddUpload;
import com.wl4g.rengine.manager.admin.model.AddUploadResult;
import com.wl4g.rengine.manager.admin.model.DeleteUpload;
import com.wl4g.rengine.manager.admin.model.DeleteUploadResult;
import com.wl4g.rengine.manager.admin.model.QueryUpload;
import com.wl4g.rengine.manager.admin.model.QueryUploadResult;
import com.wl4g.rengine.manager.admin.service.UploadService;
import com.wl4g.rengine.manager.minio.MinioClientManager;
import com.wl4g.rengine.manager.minio.MinioClientProperties;
import com.wl4g.rengine.manager.util.IdGenUtil;

import io.minio.credentials.Credentials;

/**
 * {@link UploadServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Service
public class UploadServiceImpl implements UploadService {

    private @Autowired MinioClientProperties config;
    private @Autowired MongoTemplate mongoTemplate;
    private @Autowired MinioClientManager minioManager;

    @Override
    public QueryUploadResult query(QueryUpload model) {
        // TODO use pagination

        Criteria criteria = new Criteria().orOperator(Criteria.where("name").is(model.getFilename()),
                Criteria.where("bizType").is(model.getBizType()), Criteria.where("status").is(model.getStatus()),
                Criteria.where("labels").in(model.getLabels()));

        List<UploadObject> uploads = mongoTemplate.find(new Query(criteria), UploadObject.class,
                MongoCollectionDefinition.UPLOADS.getName());

        Collections.sort(uploads, (o1, o2) -> safeLongToInt(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()));

        return QueryUploadResult.builder()
                .uploads(safeList(uploads).stream()
                        .map(p -> UploadObject.builder()
                                .bizType(p.getBizType())
                                .id(p.getId())
                                .prefix(p.getPrefix())
                                .filename(p.getFilename())
                                .extension(p.getExtension())
                                .labels(p.getLabels())
                                .size(p.getSize())
                                .sha1sum(p.getSha1sum())
                                .md5sum(p.getMd5sum())
                                .enabled(p.getEnabled())
                                .remark(p.getRemark())
                                .updateBy(p.getUpdateBy())
                                .updateDate(p.getUpdateDate())
                                .build())
                        .collect(toList()))
                .build();
    }

    public AddUploadResult apply(AddUpload model) {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // System.out.println(authentication);

        // The precise object prefixes to ensure the creation of STS policy
        // with precise authorized write permissions.
        String objectPrefix = format("%s/%s/%s", RengineConstants.DEF_MINIO_BUCKET, BizType.of(model.getBizType()).getValue(),
                model.getFilename());
        UploadObject upload = UploadObject.builder()
                .bizType(model.getBizType())
                .id(IdGenUtil.next())
                .prefix(objectPrefix)
                .filename(model.getFilename())
                .extension(model.getExtension())
                .labels(model.getLabels())
                .size(model.getSize())
                .enabled(model.getEnabled())
                .remark(model.getRemark())
                .updateBy("admin")
                .updateDate(new Date())
                .build();
        // Save metadata to mongo table.
        mongoTemplate.insert(upload, MongoCollectionDefinition.UPLOADS.getName());

        // New create temporary STS credentials.
        try {
            Credentials credentials = minioManager.createSTSCredentials(objectPrefix);
            return AddUploadResult.builder()
                    .endpoint(config.getEndpoint())
                    .region(config.getRegion())
                    // .bucket(config.getBucket())
                    .bucket(RengineConstants.DEF_MINIO_BUCKET)
                    .accessKey(credentials.accessKey())
                    .secretKey(credentials.secretKey())
                    .sessionToken(credentials.sessionToken())
                    .partSize(config.getUserUpload().getLibraryPartSize().toBytes())
                    .id(upload.getId())
                    .fileLimitSize(config.getUserUpload().getLibraryFileLimitSize().toBytes())
                    .prefix(objectPrefix)
                    .extension(config.getUserUpload().getLibraryExtensions())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to create STS with assumeRole grant", e);
        }
    }

    @Override
    public DeleteUploadResult delete(DeleteUpload model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.UPLOADS.getName());
        return DeleteUploadResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
