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

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_TENANTS;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.sys.Tenant;
import com.wl4g.rengine.service.TenantService;
import com.wl4g.rengine.service.impl.BasicServiceImpl;
import com.wl4g.rengine.service.model.sys.TenantDelete;
import com.wl4g.rengine.service.model.sys.TenantDeleteResult;
import com.wl4g.rengine.service.model.sys.TenantQuery;
import com.wl4g.rengine.service.model.sys.TenantSave;
import com.wl4g.rengine.service.model.sys.TenantSaveResult;

/**
 * {@link TenantServiceImpl}
 * 
 * @author James Wong
 * @date 2022-08-29
 * @since v1.0.0
 */
@Service
public class TenantServiceImpl extends BasicServiceImpl implements TenantService {

    @Override
    public PageHolder<Tenant> query(TenantQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getTenantId())))
                .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<Tenant> tenants = mongoTemplate.find(query, Tenant.class, SYS_TENANTS.getName());

        return new PageHolder<Tenant>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, SYS_TENANTS.getName()))
                .withRecords(tenants);
    }

    @Override
    public TenantSaveResult save(TenantSave model) {
        Tenant tenant = model;
        notNullOf(tenant, "tenant");

        if (isNull(tenant.getId())) {
            tenant.preInsert();
        } else {
            tenant.preUpdate();
        }

        Tenant saved = mongoTemplate.save(tenant, SYS_TENANTS.getName());
        return TenantSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public TenantDeleteResult delete(TenantDelete model) {
        return TenantDeleteResult.builder().deletedCount(doDeleteGracefully(model, SYS_TENANTS)).build();
    }

}
