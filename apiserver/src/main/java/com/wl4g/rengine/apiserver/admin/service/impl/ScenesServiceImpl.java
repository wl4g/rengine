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
package com.wl4g.rengine.apiserver.admin.service.impl;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static com.wl4g.rengine.apiserver.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.apiserver.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.apiserver.mongo.QueryHolder.isCriteria;
import static java.util.Objects.isNull;

import java.util.Collections;
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
import com.wl4g.rengine.apiserver.admin.model.DeleteScenes;
import com.wl4g.rengine.apiserver.admin.model.DeleteScenesResult;
import com.wl4g.rengine.apiserver.admin.model.QueryScenes;
import com.wl4g.rengine.apiserver.admin.model.SaveScenes;
import com.wl4g.rengine.apiserver.admin.model.SaveScenesResult;
import com.wl4g.rengine.apiserver.admin.service.ScenesService;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.common.util.IdGenUtil;

/**
 * {@link ScenesServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class ScenesServiceImpl implements ScenesService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<Scenes> query(QueryScenes model) {
        Query query = new Query(andCriteria(baseCriteria(model), isCriteria("_id", model.getScenesId())));

        query.with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        List<Scenes> sceneses = mongoTemplate.find(query, Scenes.class, MongoCollectionDefinition.SCENESES.getName());
        Collections.sort(sceneses, (o1, o2) -> safeLongToInt(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()));

        // QueryScenesResult.builder()
        // .sceneses(safeList(scenes).stream()
        // .map(p -> Scenes.builder()
        // .id(p.getId())
        // .name(p.getName())
        // .orgCode(p.getorgCode())
        // .labels(p.getLabels())
        // .enable(p.getEnable())
        // .remark(p.getRemark())
        // .updateBy(p.getUpdateBy())
        // .updateDate(p.getUpdateDate())
        // .build())
        // .collect(toList()))
        // .build();

        return new PageHolder<Scenes>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.SCENESES.getName()))
                .withRecords(sceneses);
    }

    @Override
    public SaveScenesResult save(SaveScenes model) {
        Scenes scenes = model;
        // @formatter:off
        //Scenes scenes = Scenes.builder()
        //        .id(model.getId())
        //        .name(model.getName())
        //        .scenesCode(model.getScenesCode())
        //        .orgCode(model.getOrgCode())
        //        .labels(model.getLabels())
        //        .enable(model.getEnable())
        //        .remark(model.getRemark())
        //        .build();
        // @formatter:on
        notNullOf(scenes, "scenes");

        if (isNull(scenes.getId())) {
            scenes.setId(IdGenUtil.nextLong());
            scenes.preInsert();
        } else {
            scenes.preUpdate();
        }

        Scenes saved = mongoTemplate.insert(scenes, MongoCollectionDefinition.SCENESES.getName());
        return SaveScenesResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteScenesResult delete(DeleteScenes model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.SCENESES.getName());
        return DeleteScenesResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
