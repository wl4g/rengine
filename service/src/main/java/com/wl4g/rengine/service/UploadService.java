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
package com.wl4g.rengine.service;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;

import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.sys.UploadObject;
import com.wl4g.rengine.service.model.DeleteUpload;
import com.wl4g.rengine.service.model.sys.UploadApply;
import com.wl4g.rengine.service.model.sys.UploadApplyResult;
import com.wl4g.rengine.service.model.sys.UploadDeleteResult;
import com.wl4g.rengine.service.model.sys.UploadQuery;
import com.wl4g.rengine.service.model.sys.UploadSave;
import com.wl4g.rengine.service.model.sys.UploadSaveResult;

/**
 * {@link UploadService}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
public interface UploadService {

    default UploadObject get(@NotNull Long uploadId) {
        final PageHolder<UploadObject> result = query(UploadQuery.builder().uploadIds(singletonList(uploadId)).build());
        if (nonNull(result) && !safeList(result.getRecords()).isEmpty()) {
            return safeList(result.getRecords()).get(0);
        }
        return null;
    }

    PageHolder<UploadObject> query(@NotNull UploadQuery model);

    UploadApplyResult apply(@NotNull UploadApply model);

    UploadSaveResult save(@NotNull UploadSave model);

    UploadDeleteResult delete(@NotNull DeleteUpload model);
}
