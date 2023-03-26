/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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
package com.wl4g.rengine.common.entity.sys;

import com.wl4g.rengine.common.entity.BaseEntity;
import com.wl4g.rengine.common.entity.quota.ResourceQuota;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Tenant}
 * 
 * @author James Wong
 * @version 2023-03-08
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class Tenant extends BaseEntity {
    private static final long serialVersionUID = 381411777614066880L;

    private @Default ServiceSettings serviceSettings = new ServiceSettings();
    private @Default ResourceQuota executorQuota = new ResourceQuota();
    private @Default ResourceQuota controllerQuota = new ResourceQuota();

    @Getter
    @Setter
    @SuperBuilder
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class ServiceSettings {
        private @Default Boolean useSharedDefaultMongo = true;
        private @Default Boolean useSharedDefaultRedis = true;
        private @Default Boolean useSharedDefaultMinIO = true;
    }

}