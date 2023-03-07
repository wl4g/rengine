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
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

import java.util.List;

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
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.sys.Organization;
import com.wl4g.rengine.service.OrganizationService;
import com.wl4g.rengine.service.model.sys.OrganizationDelete;
import com.wl4g.rengine.service.model.sys.OrganizationDeleteResult;
import com.wl4g.rengine.service.model.sys.OrganizationQuery;
import com.wl4g.rengine.service.model.sys.OrganizationSave;
import com.wl4g.rengine.service.model.sys.OrganizationSaveResult;

/**
 * {@link OranizationServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class OranizationServiceImpl implements OrganizationService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<Organization> query(OrganizationQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getOrganizationId())))
                .with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        final List<Organization> organizations = mongoTemplate.find(query, Organization.class,
                MongoCollectionDefinition.SYS_ORANIZATIONS.getName());

        return new PageHolder<Organization>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.SYS_ORANIZATIONS.getName()))
                .withRecords(organizations);
    }

    @Override
    public OrganizationSaveResult save(OrganizationSave model) {
        Organization organization = model;
        notNullOf(organization, "organization");

        if (isNull(organization.getId())) {
            organization.preInsert();
        } else {
            organization.preUpdate();
        }

        Organization saved = mongoTemplate.save(organization, MongoCollectionDefinition.SYS_ORANIZATIONS.getName());
        return OrganizationSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public OrganizationDeleteResult delete(OrganizationDelete model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.SYS_ORANIZATIONS.getName());
        return OrganizationDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
