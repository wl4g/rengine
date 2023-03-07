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
import com.wl4g.rengine.common.entity.sys.Role;
import com.wl4g.rengine.service.RoleService;
import com.wl4g.rengine.service.model.sys.RoleDelete;
import com.wl4g.rengine.service.model.sys.RoleDeleteResult;
import com.wl4g.rengine.service.model.sys.RoleQuery;
import com.wl4g.rengine.service.model.sys.RoleSave;
import com.wl4g.rengine.service.model.sys.RoleSaveResult;

/**
 * {@link RoleServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class RoleServiceImpl implements RoleService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<Role> query(RoleQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getRoleId())))
                .with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        final List<Role> roles = mongoTemplate.find(query, Role.class, MongoCollectionDefinition.SYS_ROLES.getName());

        return new PageHolder<Role>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.SYS_ROLES.getName()))
                .withRecords(roles);
    }

    @Override
    public RoleSaveResult save(RoleSave model) {
        Role role = model;
        notNullOf(role, "role");

        if (isNull(role.getId())) {
            role.preInsert();
        } else {
            role.preUpdate();
        }

        Role saved = mongoTemplate.save(role, MongoCollectionDefinition.SYS_ROLES.getName());
        return RoleSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public RoleDeleteResult delete(RoleDelete model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.SYS_ROLES.getName());
        return RoleDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
