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
package com.wl4g.rengine.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.orCriteria;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.common.validation.ValidForEntityMarker;
import com.wl4g.rengine.service.UploadService;
import com.wl4g.rengine.service.minio.MinioClientManager;
import com.wl4g.rengine.service.minio.MinioClientProperties;
import com.wl4g.rengine.service.model.DeleteUpload;
import com.wl4g.rengine.service.model.UploadDeleteResult;
import com.wl4g.rengine.service.model.UploadQuery;
import com.wl4g.rengine.service.model.UploadSave;
import com.wl4g.rengine.service.model.UploadSaveResult;

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
    private @Autowired MongoTemplate mongoTemplate;
    private @Autowired(required = false) MinioClientManager minioManager;

    @Override
    public PageHolder<UploadObject> query(UploadQuery model) {
        final Query query = new Query(orCriteria(isIdCriteria(model.getUploadId()),
                andCriteria(baseCriteria(model), isCriteria("uploadType", model.getUploadType()))))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<UploadObject> uploads = mongoTemplate.find(query, UploadObject.class,
                MongoCollectionDefinition.T_UPLOADS.getName());
        // Collections.sort(uploads, (o1, o2) ->
        // safeLongToInt(o2.getUpdateDate().getTime() -
        // o1.getUpdateDate().getTime()));

        return new PageHolder<UploadObject>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.T_UPLOADS.getName()))
                .withRecords(uploads);
    }

    public UploadSaveResult apply(UploadSave model) {
        // Authentication authentication =
        // SecurityContextHolder.getContext().getAuthentication();
        // System.out.println(authentication);

        UploadType uploadType = UploadType.of(model.getUploadType());
        // The precise object prefixes to ensure the creation of STS policy
        // with precise authorized write permissions.
        String objectPrefix = format("%s/%s/%s", RengineConstants.DEFAULT_MINIO_BUCKET, uploadType.getPrefix(),
                model.getFilename());
        UploadObject upload = UploadObject.builder()
                .uploadType(model.getUploadType())
                .id(IdGenUtils.nextLong())
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
            upload.setId(IdGenUtils.nextLong());
            upload.preInsert();
        } else {
            upload.preUpdate();
        }

        validator.validate(upload, ValidForEntityMarker.class);

        // Save metadata to mongo table.
        mongoTemplate.save(upload, MongoCollectionDefinition.T_UPLOADS.getName());

        // New create temporary STS credentials.
        try {
            Credentials credentials = minioManager.createSTSCredentials(objectPrefix);
            final MinioClientProperties config = minioManager.getConfig();
            return UploadSaveResult.builder()
                    .id(upload.getId())
                    .endpoint(config.getEndpoint())
                    .region(minioManager.getConfig().getRegion())
                    // .bucket(properties.getBucket())
                    .bucket(RengineConstants.DEFAULT_MINIO_BUCKET)
                    .accessKey(credentials.accessKey())
                    .secretKey(credentials.secretKey())
                    .sessionToken(credentials.sessionToken())
                    .partSize(minioManager.getConfig().getUserUpload().getLibraryPartSize().toBytes())
                    .fileLimitSize(minioManager.getConfig().getUserUpload().getLibraryFileLimitSize().toBytes())
                    .objectPrefix(objectPrefix)
                    .extensions(safeList(uploadType.getExtensions()).stream().map(t -> t.getSuffix()).collect(toList()))
                    .build();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Failed to create STS with assumeRole grant", e);
        }
    }

    @Override
    public UploadDeleteResult delete(DeleteUpload model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.T_UPLOADS.getName());
        return UploadDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
