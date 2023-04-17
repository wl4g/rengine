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
package com.wl4g.rengine.service;

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static java.lang.String.format;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.rengine.common.entity.sys.IdentityProvider;
import com.wl4g.rengine.common.entity.sys.IdentityProvider.IdPKind;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.service.model.sys.IdentityProviderDelete;
import com.wl4g.rengine.service.model.sys.IdentityProviderDeleteResult;
import com.wl4g.rengine.service.model.sys.IdentityProviderQuery;
import com.wl4g.rengine.service.model.sys.IdentityProviderSave;
import com.wl4g.rengine.service.model.sys.IdentityProviderSaveResult;

/**
 * {@link IdentityProviderService}
 * 
 * @author James Wong
 * @date 2022-08-29
 * @since v1.0.0
 */
public interface IdentityProviderService {

    default IdentityProvider getRegistrationId(@NotBlank String registrationId) {
        final var result = query(IdentityProviderQuery.builder()
                .enable(true)
                .type(IdPKind.OIDC.name())
                .registrationId(hasTextOf(registrationId, "registrationId"))
                .build());
        if (!CollectionUtils2.isEmpty(result.getRecords())) {
            if (result.getRecords().size() > 1) {
                throw new RengineException(format(
                        "More many IDP configurations found. Please make sure the backend mgmt configuration is correct. %s",
                        result.getRecords()));
            }
            return result.getRecords().get(0);
        }
        return null;
    }

    PageHolder<IdentityProvider> query(@NotNull IdentityProviderQuery model);

    IdentityProviderSaveResult save(@NotNull IdentityProviderSave model);

    IdentityProviderDeleteResult delete(IdentityProviderDelete model);

}
