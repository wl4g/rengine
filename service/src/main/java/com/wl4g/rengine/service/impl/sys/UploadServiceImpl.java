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
package com.wl4g.rengine.service.impl.sys;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_UPLOADS;
import static com.wl4g.rengine.common.constants.RengineConstants.TenantedHolder.getSlashKey;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.inCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.inIdsCriteria;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.entity.sys.UploadObject;
import com.wl4g.rengine.common.entity.sys.UploadObject.ExtensionType;
import com.wl4g.rengine.common.entity.sys.UploadObject.UploadType;
import com.wl4g.rengine.common.validation.ValidForEntityMarker;
import com.wl4g.rengine.service.UploadService;
import com.wl4g.rengine.service.impl.BasicServiceImpl;
import com.wl4g.rengine.service.minio.MinioClientManager;
import com.wl4g.rengine.service.minio.MinioClientProperties;
import com.wl4g.rengine.service.model.DeleteUpload;
import com.wl4g.rengine.service.model.sys.UploadApply;
import com.wl4g.rengine.service.model.sys.UploadApplyResult;
import com.wl4g.rengine.service.model.sys.UploadDeleteResult;
import com.wl4g.rengine.service.model.sys.UploadQuery;
import com.wl4g.rengine.service.model.sys.UploadSave;
import com.wl4g.rengine.service.model.sys.UploadSaveResult;

import io.minio.credentials.Credentials;

/**
 * {@link UploadServiceImpl}
 * 
 * @author James Wong
 * @date 2022-08-29
 * @since v1.0.0
 */
@Service
public class UploadServiceImpl extends BasicServiceImpl implements UploadService {

    private @Autowired(required = false) MinioClientManager minioManager;

    @Override
    public PageHolder<UploadObject> query(@NotNull UploadQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), inIdsCriteria(safeList(model.getUploadIds()).toArray()),
                inCriteria("uploadType", model.getUploadTypes())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<UploadObject> uploads = mongoTemplate.find(query, UploadObject.class, SYS_UPLOADS.getName());

        return new PageHolder<UploadObject>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, SYS_UPLOADS.getName()))
                .withRecords(uploads);
    }

    @Override
    public UploadApplyResult apply(@NotNull UploadApply model) {
        // Find the upload.
        final UploadObject upload = get(model.getUploadId());
        Assert2.notNull(upload, "Could't the find upload for %s", model.getUploadId());

        // New create temporary STS credentials.
        try {
            final Credentials credentials = minioManager.createSTSCredentials(upload.getObjectPrefix());
            final MinioClientProperties config = minioManager.getConfig();
            return UploadApplyResult.builder()
                    .endpoint(config.getEndpoint())
                    .region(minioManager.getConfig().getRegion())
                    // .bucket(details.getBucket())
                    .bucket(RengineConstants.DEFAULT_MINIO_BUCKET)
                    .accessKey(credentials.accessKey())
                    .secretKey(credentials.secretKey())
                    .sessionToken(credentials.sessionToken())
                    .partSize(minioManager.getConfig().getUserUpload().getLibraryPartSize().toBytes())
                    .fileLimitSize(minioManager.getConfig().getUserUpload().getLibraryFileLimitSize().toBytes())
                    .objectPrefix(upload.getObjectPrefix())
                    .extensions(safeList(UploadType.of(upload.getUploadType()).getExtensions()).stream()
                            .map(t -> t.getSuffix())
                            .collect(toList()))
                    .build();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Failed to create STS with assumeRole grant", ex);
        }
    }

    @Override
    public UploadSaveResult save(@NotNull UploadSave model) {
        hasTextOf(model.getFilename(), "filename");
        hasTextOf(model.getExtension(), "extension");
        final ExtensionType extType = ExtensionType.of(model.getExtension());

        final UploadType uploadType = UploadType.of(model.getUploadType());
        final UploadObject upload = UploadObject.builder()
                .id(model.getId())
                .uploadType(model.getUploadType())
                // .objectPrefix(objectPrefix)
                .filename(model.getFilename())
                .extension(model.getExtension())
                .tenantId(model.getTenantId())
                .labels(model.getLabels())
                .size(model.getSize())
                .enable(model.getEnable())
                .remark(model.getRemark())
                .build();

        // Any modification is newly inserted, and revision is incremented.
        upload.preInsert();

        // TODO check for duplicate filename?
        final long revision = mongoSequenceService.getNextSequence(UploadObject.class, model.getFilename());
        // see:com.wl4g.rengine.executor.execution.engine.AbstractScriptEngine#loadScriptResources()
        final String version = "v".concat(valueOf(revision));
        String versionedFilename = model.getFilename().concat("-").concat(version).concat(extType.getSuffix());
        final String objectPrefix = getSlashKey(
                format("%s/%s/%s", uploadType.getPrefix(), model.getFilename(), versionedFilename));
        upload.setObjectPrefix(objectPrefix);

        validator.validate(upload, ValidForEntityMarker.class);

        // Save metadata to mongo table.
        mongoTemplate.save(upload, SYS_UPLOADS.getName());

        // New create temporary STS credentials.
        // The precise object prefixes to ensure the creation of STS policy
        // with precise authorized write permissions.
        try {
            final Credentials credentials = minioManager.createSTSCredentials(upload.getObjectPrefix());
            final MinioClientProperties config = minioManager.getConfig();
            return UploadSaveResult.builder()
                    .id(upload.getId())
                    .endpoint(config.getEndpoint())
                    .region(minioManager.getConfig().getRegion())
                    // .bucket(details.getBucket())
                    .bucket(RengineConstants.DEFAULT_MINIO_BUCKET)
                    .accessKey(credentials.accessKey())
                    .secretKey(credentials.secretKey())
                    .sessionToken(credentials.sessionToken())
                    .partSize(minioManager.getConfig().getUserUpload().getLibraryPartSize().toBytes())
                    .fileLimitSize(minioManager.getConfig().getUserUpload().getLibraryFileLimitSize().toBytes())
                    .objectPrefix(upload.getObjectPrefix())
                    .extensions(safeList(uploadType.getExtensions()).stream().map(t -> t.getSuffix()).collect(toList()))
                    .build();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Failed to create STS with assumeRole grant", ex);
        }
    }

    @Override
    public UploadDeleteResult delete(@NotNull DeleteUpload model) {
        return UploadDeleteResult.builder().deletedCount(doDeleteGracefully(model, SYS_UPLOADS)).build();
    }

}
