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

import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.ControllerLog;
import com.wl4g.rengine.service.model.ControllerLogDeleteResult;
import com.wl4g.rengine.service.model.ControllerLogDelete;
import com.wl4g.rengine.service.model.ControllerLogQuery;
import com.wl4g.rengine.service.model.ControllerLogSaveResult;

/**
 * {@link ControllerLogService}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
public interface ControllerLogService {

    default ControllerLog get(@NotNull Long id) {
        final var result = query(ControllerLogQuery.builder().controllerLogId(id).build());
        return safeList(result.getRecords()).isEmpty() ? null : result.getRecords().get(0);
    }

    PageHolder<ControllerLog> query(@NotNull ControllerLogQuery model);

    ControllerLogSaveResult save(@NotNull ControllerLog model);

    ControllerLogDeleteResult delete(@NotNull ControllerLogDelete model);

}
