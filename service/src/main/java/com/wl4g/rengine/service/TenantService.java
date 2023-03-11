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

import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.sys.Tenant;
import com.wl4g.rengine.service.model.sys.TenantDelete;
import com.wl4g.rengine.service.model.sys.TenantDeleteResult;
import com.wl4g.rengine.service.model.sys.TenantQuery;
import com.wl4g.rengine.service.model.sys.TenantSave;
import com.wl4g.rengine.service.model.sys.TenantSaveResult;

/**
 * {@link TenantService}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
public interface TenantService {

    PageHolder<Tenant> query(@NotNull TenantQuery model);

    TenantSaveResult save(@NotNull TenantSave model);

    TenantDeleteResult delete(@NotNull TenantDelete model);

}
