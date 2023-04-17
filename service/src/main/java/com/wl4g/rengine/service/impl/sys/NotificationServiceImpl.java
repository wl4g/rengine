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
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.AccessToken;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.sys.Notification;
import com.wl4g.rengine.common.entity.sys.Notification.DingtalkConfig;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.NotificationService;
import com.wl4g.rengine.service.impl.BasicServiceImpl;
import com.wl4g.rengine.service.model.sys.NotificationDingtalkUserIdExchage;
import com.wl4g.rengine.service.model.sys.NotificationDingtalkUserIdExchageResult;
import com.wl4g.rengine.service.model.sys.NotificationQuery;
import com.wl4g.rengine.service.model.sys.NotificationQueryResult;
import com.wl4g.rengine.service.model.sys.NotificationSave;
import com.wl4g.rengine.service.model.sys.NotificationSaveResult;

import lombok.CustomLog;

/**
 * {@link NotificationServiceImpl}
 * 
 * @author James Wong
 * @date 2022-08-29
 * @since v1.0.0
 */
@CustomLog
@Service
public class NotificationServiceImpl extends BasicServiceImpl implements NotificationService {

    @Override
    public NotificationQueryResult query(NotificationQuery model) {
        List<Notification> notifications = null;
        if (!isBlank(model.getType())) {
            final Criteria criteria = new Criteria().orOperator(Criteria.where("details.type").is(model.getType()));
            notifications = mongoTemplate.find(new Query(criteria), Notification.class,
                    MongoCollectionDefinition.SYS_NOTIFICATIONS.getName());
        } else {
            notifications = mongoTemplate.findAll(Notification.class, MongoCollectionDefinition.SYS_NOTIFICATIONS.getName());
        }
        // Collections.sort(notifications, (o1, o2) ->
        // (o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()) > 0 ? 1
        // : -1);

        // TODO Notice: This cannot be converted to a mask temporarily, because
        // the userId needs to be obtained according to the mobile when adding
        // (use appKey/appSecret to obtain accessToken).
        //
        // Notice: Unless the dingtalk basic(appkey etc) configuration is
        // separated from the API of user information and scene group
        // information later. Since the use of dingtalk only uses system
        // notifications, there is only a small amount of data information, so
        // in order to make the API lightweight, all are merged
        //
        // Mask sensitive information.
        // for (Notification notification : notifications) {
        // BeanSensitiveTransforms.transform(notification.getProperties());
        // }

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

    @Override
    public NotificationDingtalkUserIdExchageResult exchageDingtalkUserId(@NotNull NotificationDingtalkUserIdExchage model) {
        // First load userIds from DB.
        Map<String, String> existingUsers = null;
        final NotificationQueryResult result = query(NotificationQuery.builder().type(NotifierKind.DINGTALK.name()).build());
        if (nonNull(result) && !result.getProviders().isEmpty()) {
            final DingtalkConfig dingtalk = (DingtalkConfig) result.getProviders().get(0).getDetails();
            existingUsers = safeList(dingtalk.getUsers()).stream().collect(toMap(u -> u.getMobile(), u -> u.getUserId()));
        }

        // New obtain access token.
        String accessToken = model.getAccessToken();
        if (isBlank(accessToken)) {
            log.info("Obtainig the dingtalk access token of : {}", model.getAppKey());
            try {
                accessToken = DingtalkAPI.getInstance()
                        .getAccessToken(AccessToken.builder().appKey(model.getAppKey()).appSecret(model.getAppSecret()).build())
                        .getAccessToken();
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to get access token before obtain userId.", ex);
            }
        }

        log.info("Exchanging the dingtalk userIds by mobile numbers : {}", model.getMobiles());

        final Map<String, String> _existingUsers = existingUsers;
        return NotificationDingtalkUserIdExchageResult.builder()
                .appKey(model.getAppKey())
                .appSecret(model.getAppSecret())
                .accessToken(accessToken)
                .userIds(safeList(model.getMobiles()).parallelStream().map(mobile -> {
                    try {
                        final String userId = _existingUsers.get(mobile);
                        if (isBlank(userId)) {
                            return DingtalkAPI.getInstance().getUserIdByMobile(model.getAccessToken(), mobile).getUserid();
                        }
                        return userId;
                    } catch (Throwable ex) {
                        throw new IllegalStateException(format("Failed to exchage userId by mobile '%s'", mobile), ex);
                    }
                }).filter(userId -> !isBlank(userId)).collect(toList()))
                .build();
    }

}
