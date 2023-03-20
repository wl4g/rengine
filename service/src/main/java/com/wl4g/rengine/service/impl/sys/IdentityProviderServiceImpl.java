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

import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.sys.IdentityProvider;
import com.wl4g.rengine.common.util.BeanSensitiveTransforms;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.IdentityProviderService;
import com.wl4g.rengine.service.impl.BasicServiceImpl;
import com.wl4g.rengine.service.model.sys.IdentityProviderDelete;
import com.wl4g.rengine.service.model.sys.IdentityProviderDeleteResult;
import com.wl4g.rengine.service.model.sys.IdentityProviderQuery;
import com.wl4g.rengine.service.model.sys.IdentityProviderSave;
import com.wl4g.rengine.service.model.sys.IdentityProviderSaveResult;

/**
 * {@link IdentityProviderServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class IdentityProviderServiceImpl extends BasicServiceImpl implements IdentityProviderService {

    @Override
    public PageHolder<IdentityProvider> query(IdentityProviderQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getIdpId()),
                isCriteria("details.type", model.getType()), isCriteria("details.registrationId", model.getRegistrationId())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<IdentityProvider> idps = mongoTemplate.find(query, IdentityProvider.class,
                MongoCollectionDefinition.SYS_IDENTITY_PROVIDERS.getName());
        // Collections.sort(idpConfigs, (o1, o2) ->
        // (o2.getUpdateDate().getTime()
        // - o1.getUpdateDate().getTime()) > 0 ? 1 : -1);

        // Mask sensitive information.
        for (IdentityProvider idp : idps) {
            BeanSensitiveTransforms.transform(idp);
        }

        return new PageHolder<IdentityProvider>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.SYS_IDENTITY_PROVIDERS.getName()))
                .withRecords(idps);
    }

    @Override
    public IdentityProviderSaveResult save(IdentityProviderSave model) {
        IdentityProvider provider = model.getProvider();
        if (isNull(provider.getId())) {
            provider.setId(IdGenUtils.nextLong());
            provider.preInsert();
        } else {
            provider.preUpdate();
        }
        provider.setUpdateDate(new Date());
        IdentityProvider saved = mongoTemplate.save(provider, MongoCollectionDefinition.SYS_IDENTITY_PROVIDERS.getName());
        return IdentityProviderSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public IdentityProviderDeleteResult delete(IdentityProviderDelete model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.SYS_IDENTITY_PROVIDERS.getName());
        return IdentityProviderDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
