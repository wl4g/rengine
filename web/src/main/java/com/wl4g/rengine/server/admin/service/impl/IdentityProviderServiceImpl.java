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

import com.wl4g.rengine.common.bean.mongo.IdentityProviderConfig;
import com.wl4g.rengine.server.admin.model.AddIdentityProvider;
import com.wl4g.rengine.server.admin.model.AddIdentityProviderResult;
import com.wl4g.rengine.server.admin.model.QueryIdentityProvider;
import com.wl4g.rengine.server.admin.model.QueryIdentityProviderResult;
import com.wl4g.rengine.server.admin.service.IdentityProviderService;
import com.wl4g.rengine.server.constants.RengineWebConstants.MongoCollectionDefinition;

/**
 * {@link IdentityProviderServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Service
public class IdentityProviderServiceImpl implements IdentityProviderService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public QueryIdentityProviderResult query(QueryIdentityProvider model) {
        List<IdentityProviderConfig> idpConfigs = mongoTemplate.findAll(IdentityProviderConfig.class,
                MongoCollectionDefinition.SYS_IDP_CONFIG.getName());
        if (isEmpty(idpConfigs)) {
            IdentityProviderConfig idpConfig = idpConfigs.get(0);
            return QueryIdentityProviderResult.builder().oauth2(idpConfig.getOauth2()).saml2(idpConfig.getSaml2()).build();
        }
        return QueryIdentityProviderResult.builder().build();
    }

    @Override
    public AddIdentityProviderResult save(AddIdentityProvider model) {
        IdentityProviderConfig idpConfig = IdentityProviderConfig.builder()
                .oauth2(model.getOauth2())
                .saml2(model.getSaml2())
                .build();
        IdentityProviderConfig saved = mongoTemplate.insert(idpConfig, MongoCollectionDefinition.SYS_IDP_CONFIG.getName());
        return AddIdentityProviderResult.builder().id(saved.getId()).build();
    }

}
