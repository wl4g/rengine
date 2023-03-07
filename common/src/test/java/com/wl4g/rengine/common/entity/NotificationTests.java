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
package com.wl4g.rengine.common.entity;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonList;

import java.util.Date;

import org.junit.Test;

import com.wl4g.rengine.common.entity.sys.Notification;
import com.wl4g.rengine.common.entity.sys.Notification.DingtalkConfig;
import com.wl4g.rengine.common.entity.sys.Notification.DingtalkConfig.DingtalkScenesGroupInfo;
import com.wl4g.rengine.common.entity.sys.Notification.DingtalkConfig.DingtalkUserInfo;

/**
 * {@link NotificationTests}
 * 
 * @author James Wong
 * @version 2022-12-15
 * @since v1.0.0
 */
public class NotificationTests {

    @Test
    public void testNotificationSerialize() {
        final Notification notification = Notification.builder()
                .id(10101001L)
                .enable(1)
                .details(DingtalkConfig.builder()
                        .appKey("abcd323423")
                        .appSecret("abcdef")
                        .corpId("abcdef222")
                        .aesKey("abcdef11")
                        .users(singletonList(new DingtalkUserInfo("18007233345", "abcdef1111")))
                        .scenesGroups(singletonList(DingtalkScenesGroupInfo.builder()
                                .templateId("abcdef1111222")
                                .ownerUserId("abcdeftwer")
                                .adminUserIds(singletonList("abcdef3124512"))
                                .userIds(singletonList("abcdef123"))
                                .build()))
                        .build())
                .createDate(new Date())
                .build();

        String json = toJSONString(notification, true);
        System.out.println(json);

        Notification notification2 = parseJSON(json, Notification.class);
        System.out.println(notification2);

        assert notification2.getDetails() instanceof DingtalkConfig;
    }

}
