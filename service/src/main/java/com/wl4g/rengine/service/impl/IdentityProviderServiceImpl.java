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

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.IdentityProvider;
import com.wl4g.rengine.common.util.BeanSensitiveTransforms;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.IdentityProviderService;
import com.wl4g.rengine.service.model.QueryIdentityProvider;
import com.wl4g.rengine.service.model.QueryIdentityProviderResult;
import com.wl4g.rengine.service.model.SaveIdentityProvider;
import com.wl4g.rengine.service.model.SaveIdentityProviderResult;

/**
 * {@link IdentityProviderServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class IdentityProviderServiceImpl implements IdentityProviderService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public QueryIdentityProviderResult query(QueryIdentityProvider model) {
        List<IdentityProvider> idpConfigs = null;
        if (!isBlank(model.getKind())) {
            Criteria criteria = new Criteria().orOperator(Criteria.where("kind").is(model.getKind()));
            idpConfigs = mongoTemplate.find(new Query(criteria), IdentityProvider.class,
                    MongoCollectionDefinition.SYS_IDENTITY_PROVIDERS.getName());
        } else {
            idpConfigs = mongoTemplate.findAll(IdentityProvider.class, MongoCollectionDefinition.SYS_IDENTITY_PROVIDERS.getName());
        }
        // Collections.sort(idpConfigs, (o1, o2) ->
        // safeLongToInt(o2.getUpdateDate().getTime() -
        // o1.getUpdateDate().getTime()));

        // Mask sensitive information.
        for (IdentityProvider idp : idpConfigs) {
            BeanSensitiveTransforms.transform(idp);
        }

        return QueryIdentityProviderResult.builder().providers(idpConfigs).build();
    }

    @Override
    public SaveIdentityProviderResult save(SaveIdentityProvider model) {
        IdentityProvider provider = model.getProvider();
        if (isNull(provider.getId())) {
            provider.setId(IdGenUtils.nextLong());
            provider.preInsert();
        } else {
            provider.preUpdate();
        }
        provider.setUpdateDate(new Date());
        IdentityProvider saved = mongoTemplate.save(provider, MongoCollectionDefinition.SYS_IDENTITY_PROVIDERS.getName());
        return SaveIdentityProviderResult.builder().id(saved.getId()).build();
    }

}
