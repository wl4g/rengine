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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.server.admin.service.impl;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.wl4g.rengine.common.bean.mongo.NotificationConfig;
import com.wl4g.rengine.server.admin.model.AddNotification;
import com.wl4g.rengine.server.admin.model.AddNotificationResult;
import com.wl4g.rengine.server.admin.model.QueryNotification;
import com.wl4g.rengine.server.admin.model.QueryNotificationResult;
import com.wl4g.rengine.server.admin.service.NotificationService;
import com.wl4g.rengine.server.constants.RengineWebConstants.MongoCollectionDefinition;

/**
 * {@link NotificationServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public QueryNotificationResult query(QueryNotification model) {
        List<NotificationConfig> notificationConfigs = mongoTemplate.findAll(NotificationConfig.class,
                MongoCollectionDefinition.SYS_NOTIFICATION_CONFIG.getName());
        if (isEmpty(notificationConfigs)) {
            NotificationConfig notificationConfig = notificationConfigs.get(0);
            return QueryNotificationResult.builder()
                    .email(notificationConfig.getEmail())
                    .dingtalk(notificationConfig.getDingtalk())
                    .wecom(notificationConfig.getWecom())
                    .aliyunSms(notificationConfig.getAliyunSms())
                    .aliyunVms(notificationConfig.getAliyunVms())
                    .webhook(notificationConfig.getWebhook())
                    .build();
        }
        return QueryNotificationResult.builder().build();
    }

    @Override
    public AddNotificationResult save(AddNotification model) {
        NotificationConfig notificationConfig = NotificationConfig.builder()
                .email(model.getEmail())
                .dingtalk(model.getDingtalk())
                .wecom(model.getWecom())
                .aliyunSms(model.getAliyunSms())
                .aliyunVms(model.getAliyunVms())
                .webhook(model.getWebhook())
                .build();
        NotificationConfig saved = mongoTemplate.insert(notificationConfig, MongoCollectionDefinition.SYS_NOTIFICATION_CONFIG.getName());
        return AddNotificationResult.builder().id(saved.getId()).build();
    }

}
