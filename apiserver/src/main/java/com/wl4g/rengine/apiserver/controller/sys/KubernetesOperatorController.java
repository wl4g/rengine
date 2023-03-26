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
package com.wl4g.rengine.apiserver.controller.sys;

import static com.wl4g.rengine.common.constants.RengineConstants.API_V1_KUBERNETES_OPERATOR_BASE_URI;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.sys.Tenant;
import com.wl4g.rengine.service.TenantService;
import com.wl4g.rengine.service.model.sys.TenantQuery;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link KubernetesOperatorController}
 * 
 * @author James Wong
 * @version 2023-03-27
 * @since v1.0.0
 */
@Slf4j
@RestController
@RequestMapping(API_V1_KUBERNETES_OPERATOR_BASE_URI)
public class KubernetesOperatorController {

    private @Autowired TenantService tenantService;

    @RequestMapping(path = { "tenants" }, produces = "application/json", method = { GET })
    public RespBase<PageHolder<Tenant>> tenants(@Validated TenantQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Tenant>> resp = RespBase.create();
        resp.setData(tenantService.query(model));
        return resp;
    }

}
