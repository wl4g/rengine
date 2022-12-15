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
package com.wl4g.rengine.manager.admin.service.impl;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.common.util.IdGenUtil;
import com.wl4g.rengine.manager.admin.model.DeleteDataSource;
import com.wl4g.rengine.manager.admin.model.DeleteDataSourceResult;
import com.wl4g.rengine.manager.admin.model.QueryDataSource;
import com.wl4g.rengine.manager.admin.model.SaveDataSource;
import com.wl4g.rengine.manager.admin.model.SaveDataSourceResult;
import com.wl4g.rengine.manager.admin.service.DataSourceService;

/**
 * {@link DataSourceServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class DataSourceServiceImpl implements DataSourceService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<DataSourceProperties> query(QueryDataSource model) {
        Query query = new Query(new Criteria().andOperator(Criteria.where("enable").is(1), Criteria.where("delFlag").is(0),
                new Criteria().orOperator(Criteria.where("_id").is(model.getDataSourceId()),
                        Criteria.where("type").is(model.getType().name()),
                        Criteria.where("name").regex(format("(%s)+", model.getName())),
                        Criteria.where("enable").is(model.getEnable()), Criteria.where("labels").in(model.getLabels()),
                        Criteria.where("orgCode").is(model.getOrgCode()))));

        query.with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        List<DataSourceProperties> dataSourceProperties = mongoTemplate.find(query, DataSourceProperties.class,
                MongoCollectionDefinition.DATASOURCES.getName());

        return new PageHolder<DataSourceProperties>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.DATASOURCES.getName()))
                .withRecords(dataSourceProperties);
    }

    @Override
    public SaveDataSourceResult save(SaveDataSource model) {
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
            dataSourceProperties.setId(IdGenUtil.nextLong());
            dataSourceProperties.preInsert();
        } else {
            dataSourceProperties.preUpdate();
        }

        DataSourceProperties saved = mongoTemplate.insert(dataSourceProperties, MongoCollectionDefinition.DATASOURCES.getName());
        return SaveDataSourceResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteDataSourceResult delete(DeleteDataSource model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.DATASOURCES.getName());
        return DeleteDataSourceResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
