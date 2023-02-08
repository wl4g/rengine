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

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.common.util.BeanSensitiveTransforms;
import com.wl4g.rengine.service.NotificationService;
import com.wl4g.rengine.service.model.NotificationQuery;
import com.wl4g.rengine.service.model.NotificationQueryResult;
import com.wl4g.rengine.service.model.NotificationSave;
import com.wl4g.rengine.service.model.NotificationSaveResult;

/**
 * {@link NotificationServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public NotificationQueryResult query(NotificationQuery model) {
        List<Notification> notifications = null;
        if (!isBlank(model.getType())) {
            final Criteria criteria = new Criteria().orOperator(Criteria.where("properties.type").is(model.getType()));
            notifications = mongoTemplate.find(new Query(criteria), Notification.class,
                    MongoCollectionDefinition.SYS_NOTIFICATIONS.getName());
        } else {
            notifications = mongoTemplate.findAll(Notification.class, MongoCollectionDefinition.SYS_NOTIFICATIONS.getName());
        }
        // Collections.sort(notifications, (o1, o2) ->
        // (o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()) > 0 ? 1
        // : -1);

        // Mask sensitive information.
        for (Notification notification : notifications) {
            BeanSensitiveTransforms.transform(notification.getProperties());
        }

        return NotificationQueryResult.builder().providers(notifications).build();
    }

    @Override
    public NotificationSaveResult save(NotificationSave model) {
        Notification provider = model.getProvider();
        if (isNull(provider.getId())) {
            provider.setId(IdGenUtils.nextLong());
            provider.preInsert();
        } else {
            provider.preUpdate();
        }
        Notification saved = mongoTemplate.save(provider, MongoCollectionDefinition.SYS_NOTIFICATIONS.getName());
        return NotificationSaveResult.builder().id(saved.getId()).build();
    }

}
