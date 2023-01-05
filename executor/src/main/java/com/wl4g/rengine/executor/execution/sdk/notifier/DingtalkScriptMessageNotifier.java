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
package com.wl4g.rengine.executor.execution.sdk.notifier;

import java.util.Map;

import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.AccessToken;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.AccessTokenResult;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.CreateSceneGroupV2;
import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.CreateSceneGroupV2Result;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.entity.Notification.DingtalkConfig;

import lombok.Getter;

/**
 * {@link DingtalkScriptMessageNotifier}
 * 
 * @author James Wong
 * @version 2023-01-06
 * @since v1.0.0
 */
@Getter
@Singleton
public class DingtalkScriptMessageNotifier implements ScriptMessageNotifier {

    volatile RefreshedInfo refreshed;

    @Override
    public NotifierKind kind() {
        return NotifierKind.DINGTALK;
    }

    @Override
    public Object send(Map<String, Object> parameter) {
        final String accessToken = refreshed().getAccessToken();
        return DingtalkAPI.sendRobotGroupMessages(accessToken, null);
    }

    public CreateSceneGroupV2Result createSceneGroupV2(final @NotNull CreateSceneGroupV2 request) {
        final String accessToken = refreshed().getAccessToken();
        return DingtalkAPI.createSceneGroupV2(accessToken, request);
    }

    @Override
    public RefreshedInfo refresh(Notification notification) {
        final DingtalkConfig config = (DingtalkConfig) notification;
        final AccessTokenResult result = DingtalkAPI
                .getAccessToken(AccessToken.builder().appKey(config.getAppKey()).appSecret(config.getAppSecret()).build());
        return (this.refreshed = RefreshedInfo.builder()
                .notifierType(kind())
                // .appKey(config.getAppKey())
                // .appSecret(config.getAppSecret())
                .accessToken(result.getAccessToken())
                .expireSeconds(result.getExpireIn())
                .build());
    }

}
