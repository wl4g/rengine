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
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_MENU_ROLES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_ROLES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_USER_ROLES;
import static com.wl4g.rengine.common.util.BsonAggregateFilters.ROLE_MENU_LOOKUP_FILTERS;
import static com.wl4g.rengine.common.util.BsonAggregateFilters.ROLE_USER_LOOKUP_FILTERS;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.collections.IteratorUtils;
import org.bson.conversions.Bson;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.sys.Menu;
import com.wl4g.rengine.common.entity.sys.MenuRole;
import com.wl4g.rengine.common.entity.sys.Role;
import com.wl4g.rengine.common.entity.sys.User;
import com.wl4g.rengine.common.entity.sys.UserRole;
import com.wl4g.rengine.common.util.BeanSensitiveTransforms;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.service.RoleService;
import com.wl4g.rengine.service.impl.BasicServiceImpl;
import com.wl4g.rengine.service.model.sys.RoleAssignMenu;
import com.wl4g.rengine.service.model.sys.RoleAssignUser;
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
public class RoleServiceImpl extends BasicServiceImpl implements RoleService {

    @Override
    public PageHolder<Role> query(RoleQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getRoleId())))
                .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<Role> roles = mongoTemplate.find(query, Role.class, MongoCollectionDefinition.SYS_ROLES.getName());

        safeList(roles).parallelStream().forEach(r -> r.setUsers(findUsersByRoleIds(singletonList(r.getId()))));
        safeList(roles).parallelStream().forEach(r -> r.setMenus(findMenusByRoleIds(singletonList(r.getId()))));

        return new PageHolder<Role>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, SYS_ROLES.getName()))
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

        Role saved = mongoTemplate.save(role, SYS_ROLES.getName());
        return RoleSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public RoleDeleteResult delete(RoleDelete model) {
        return RoleDeleteResult.builder().deletedCount(doDeleteGracefully(model, SYS_ROLES)).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> findUsersByRoleIds(@NotEmpty List<Long> roleIds) {
        Assert2.notEmpty(roleIds, "roleIds");

        final var aggregates = new ArrayList<Bson>(2);
        aggregates.add(Aggregates.match(Filters.in("_id", roleIds)));
        ROLE_USER_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));

        try (var cursor = mongoTemplate.getCollection(SYS_ROLES.getName())
                .aggregate(aggregates)
                .map(userDoc -> BsonEntitySerializers.fromDocument(userDoc, User.class))
                .cursor();) {
            final List<User> users = IteratorUtils.toList(cursor);
            BeanSensitiveTransforms.transform(users);
            return users;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Menu> findMenusByRoleIds(@NotEmpty List<Long> roleIds) {
        Assert2.notEmpty(roleIds, "roleIds");

        final var aggregates = new ArrayList<Bson>(2);
        aggregates.add(Aggregates.match(Filters.in("_id", roleIds)));
        ROLE_MENU_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));

        try (var cursor = mongoTemplate.getCollection(SYS_ROLES.getName())
                .aggregate(aggregates)
                .map(menuDoc -> BsonEntitySerializers.fromDocument(menuDoc, Menu.class))
                .cursor();) {
            return IteratorUtils.toList(cursor);
        }
    }

    @Override
    public List<Long> assignUsers(RoleAssignUser model) {
        notNullOf(model, "model");
        notNullOf(model.getRoleId(), "model.roleId");
        Assert2.notEmpty(model.getUserIds(), "model.userIds");

        return model.getUserIds().parallelStream().map(userId -> {
            final UserRole userRole = UserRole.builder().roleId(model.getRoleId()).userId(userId).build();
            userRole.preInsert();
            return mongoTemplate.save(userRole, SYS_USER_ROLES.getName()).getId();
        }).collect(toList());
    }

    @Override
    public List<Long> assignMenus(RoleAssignMenu model) {
        notNullOf(model, "model");
        notNullOf(model.getRoleId(), "model.roleId");
        Assert2.notEmpty(model.getMenuIds(), "model.menuIds");

        return model.getMenuIds().parallelStream().map(menuId -> {
            final MenuRole menuRole = MenuRole.builder().roleId(model.getRoleId()).menuId(menuId).build();
            menuRole.preInsert();
            return mongoTemplate.save(menuRole, SYS_MENU_ROLES.getName()).getId();
        }).collect(toList());
    }

}
