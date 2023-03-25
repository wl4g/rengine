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

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_DATASOURCES;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.common.util.BeanSensitiveTransforms;
import com.wl4g.rengine.service.DataSourceService;
import com.wl4g.rengine.service.model.DataSourceDelete;
import com.wl4g.rengine.service.model.DataSourceDeleteResult;
import com.wl4g.rengine.service.model.DataSourceQuery;
import com.wl4g.rengine.service.model.DataSourceSave;
import com.wl4g.rengine.service.model.DataSourceSaveResult;

/**
 * {@link DataSourceServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class DataSourceServiceImpl extends BasicServiceImpl implements DataSourceService {

    @Override
    public PageHolder<DataSourceProperties> query(DataSourceQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getDataSourceId()),
                isCriteria("details.type", nonNull(model.getType()) ? model.getType().name() : null)))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<DataSourceProperties> dataSourceProperties = mongoTemplate.find(query, DataSourceProperties.class,
                RE_DATASOURCES.getName());

        // Mask sensitive information.
        for (DataSourceProperties ds : dataSourceProperties) {
            BeanSensitiveTransforms.transform(ds.getDetails());
        }

        return new PageHolder<DataSourceProperties>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, RE_DATASOURCES.getName()))
                .withRecords(dataSourceProperties);
    }

    @Override
    public DataSourceSaveResult save(DataSourceSave model) {
        DataSourceProperties dataSourceProperties = model;
        // @formatter:off
        //DataSourceProperties dataSource = DataSourceProperties.builder()
        //        .id(model.getId())
        //        .type(model.getType())
        //        .name(model.getName())
        //        .orgCode(model.getOrgCode())
        //        .labels(model.getLabels())
        //        .enable(model.getEnable())
        //        .remark(model.getRemark())
        //        .build();
        // @formatter:on
        notNullOf(dataSourceProperties, "datasource");

        if (isNull(dataSourceProperties.getId())) {
            dataSourceProperties.preInsert();
        } else {
            dataSourceProperties.preUpdate();
        }

        DataSourceProperties saved = mongoTemplate.save(dataSourceProperties, RE_DATASOURCES.getName());
        return DataSourceSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public DataSourceDeleteResult delete(DataSourceDelete model) {
        return DataSourceDeleteResult.builder().deletedCount(doDeleteGracefully(model, RE_DATASOURCES)).build();
    }

}
