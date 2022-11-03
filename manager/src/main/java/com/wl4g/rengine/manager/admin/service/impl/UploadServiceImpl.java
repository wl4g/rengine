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
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.UploadObject;
import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.common.util.IdGenUtil;
import com.wl4g.rengine.common.validation.ValidForEntityMarker;
import com.wl4g.rengine.manager.admin.model.DeleteUpload;
import com.wl4g.rengine.manager.admin.model.DeleteUploadResult;
import com.wl4g.rengine.manager.admin.model.QueryUpload;
import com.wl4g.rengine.manager.admin.model.SaveUpload;
import com.wl4g.rengine.manager.admin.model.SaveUploadResult;
import com.wl4g.rengine.manager.admin.service.UploadService;
import com.wl4g.rengine.manager.minio.MinioClientManager;
import com.wl4g.rengine.manager.minio.MinioClientProperties;

import io.minio.credentials.Credentials;

/**
 * {@link UploadServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class UploadServiceImpl implements UploadService {

    private @Autowired Validator validator;
    private @Autowired MinioClientProperties config;
    private @Autowired MongoTemplate mongoTemplate;
    private @Autowired MinioClientManager minioManager;

    @Override
    public PageHolder<UploadObject> query(QueryUpload model) {
        Query query = new Query(new Criteria().orOperator(Criteria.where("_id").is(model.getUploadId()),
                Criteria.where("scenesId").is(model.getScenesId()),
                Criteria.where("name").regex(format("(%s)+", model.getName())), Criteria.where("enable").is(model.getEnable()),
                Criteria.where("orgCode").is(model.getOrgCode()), Criteria.where("labels").in(model.getLabels()),
                Criteria.where("UploadType").is(model.getUploadType())));

        query.with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        List<UploadObject> uploads = mongoTemplate.find(query, UploadObject.class, MongoCollectionDefinition.UPLOADS.getName());

        Collections.sort(uploads, (o1, o2) -> safeLongToInt(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()));

        return new PageHolder<UploadObject>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.UPLOADS.getName()))
                .withRecords(uploads);
    }

    public SaveUploadResult apply(SaveUpload model) {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // System.out.println(authentication);

        UploadType uploadType = UploadType.of(model.getUploadType());
        // The precise object prefixes to ensure the creation of STS policy
        // with precise authorized write permissions.
        String objectPrefix = format("%s/%s/%s", RengineConstants.DEF_MINIO_BUCKET, uploadType.getPrefix(), model.getFilename());
        UploadObject upload = UploadObject.builder()
                .uploadType(model.getUploadType())
                .id(IdGenUtil.nextLong())
                .objectPrefix(objectPrefix)
                .filename(model.getFilename())
                .extension(model.getExtension())
                .orgCode(model.getOrgCode())
                .labels(model.getLabels())
                .size(model.getSize())
                .enable(model.getEnable())
                .remark(model.getRemark())
                .build();

        if (isNull(upload.getId())) {
            upload.setId(IdGenUtil.nextLong());
            upload.preInsert();
        } else {
            upload.preUpdate();
        }

        validator.validate(upload, ValidForEntityMarker.class);

        // Save metadata to mongo table.
        mongoTemplate.insert(upload, MongoCollectionDefinition.UPLOADS.getName());

        // New create temporary STS credentials.
        try {
            Credentials credentials = minioManager.createSTSCredentials(objectPrefix);
            return SaveUploadResult.builder()
                    .id(upload.getId())
                    .endpoint(config.getEndpoint())
                    .region(config.getRegion())
                    // .bucket(config.getBucket())
                    .bucket(RengineConstants.DEF_MINIO_BUCKET)
                    .accessKey(credentials.accessKey())
                    .secretKey(credentials.secretKey())
                    .sessionToken(credentials.sessionToken())
                    .partSize(config.getUserUpload().getLibraryPartSize().toBytes())
                    .fileLimitSize(config.getUserUpload().getLibraryFileLimitSize().toBytes())
                    .objectPrefix(objectPrefix)
                    .extensions(safeList(uploadType.getExtensions()).stream().map(t -> t.getSuffix()).collect(toList()))
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
