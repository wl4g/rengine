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

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.User;
import com.wl4g.rengine.service.UserService;
import com.wl4g.rengine.service.config.MongoUserDetailsManager;
import com.wl4g.rengine.service.model.UserDelete;
import com.wl4g.rengine.service.model.UserDeleteResult;
import com.wl4g.rengine.service.model.UserQuery;
import com.wl4g.rengine.service.model.UserSave;
import com.wl4g.rengine.service.model.UserSaveResult;

import lombok.CustomLog;

/**
 * {@link UserServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@CustomLog
@Service
public class UserServiceImpl implements UserService {

    private @Autowired MongoTemplate mongoTemplate;
    private @Autowired(required = false) MongoUserDetailsManager userDetailsManager;

    @Override
    public PageHolder<User> query(UserQuery model) {
        final Query query = new Query(
                andCriteria(baseCriteria(model), isIdCriteria(model.getUserId()), isCriteria("email", model.getUserId())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<User> useres = mongoTemplate.find(query, User.class, MongoCollectionDefinition.SYS_USERS.getName());
        // Collections.sort(useres, (o1, o2) -> (o2.getUpdateDate().getTime()

        return new PageHolder<User>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.SYS_USERS.getName()))
                .withRecords(useres);
    }

    @Override
    public UserSaveResult save(UserSave model) {
        User user = model;
        notNullOf(user, "user");

        if (isNull(user.getId())) {
            user.preInsert();
        } else {
            user.preUpdate();
        }

        User saved = mongoTemplate.save(user, MongoCollectionDefinition.SYS_USERS.getName());
        return UserSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public UserDeleteResult delete(UserDelete model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        final DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.SYS_USERS.getName());
        return UserDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

    @Override
    public boolean changePassword(@NotBlank String oldPassword, @NotBlank String newPassword) {
        try {
            userDetailsManager.changePassword(oldPassword, newPassword);
        } catch (Throwable ex) {
            log.error(format("Failed to change password for %s", SecurityContextHolder.getContext().getAuthentication()), ex);
            return false;
        }
        return true;
    }

}
