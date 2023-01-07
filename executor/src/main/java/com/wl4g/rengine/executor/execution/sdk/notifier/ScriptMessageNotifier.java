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

import static com.wl4g.infra.common.lang.Assert2.notNull;

import java.util.Map;

import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.notification.MessageNotifier;
import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.rengine.common.entity.Notification;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link ScriptMessageNotifier}
 * 
 * @author James Wong
 * @version 2023-01-06
 * @since v1.0.0
 */
public interface ScriptMessageNotifier {

    MessageNotifier.NotifierKind kind();

    Object send(final @NotNull Map<String, Object> parameter);

    default RefreshedInfo getRequiredRefreshed() {
        return notNull(getRefreshed(),
                "Internal error! Should not be here, the current local cached refreshed is null, it should have been initialized before calling the notifier sending method.");
    }

    RefreshedInfo getRefreshed();

    void setRefreshed(RefreshedInfo refreshed);

    RefreshedInfo refresh(Notification notification);

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @ToString
    public static class RefreshedInfo {
        private NotifierKind notifierType;
        private String appKey;
        // private String appSecret;
        private String accessToken;
        private Integer expireSeconds;
        private Integer effectiveExpireSeconds;
    }
}
