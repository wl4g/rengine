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
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_SCENESES;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.service.ScenesService;
import com.wl4g.rengine.service.model.ScenesDelete;
import com.wl4g.rengine.service.model.ScenesDeleteResult;
import com.wl4g.rengine.service.model.ScenesQuery;
import com.wl4g.rengine.service.model.ScenesSave;
import com.wl4g.rengine.service.model.ScenesSaveResult;

/**
 * {@link ScenesServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class ScenesServiceImpl extends BasicServiceImpl implements ScenesService {

    @Override
    public PageHolder<Scenes> query(ScenesQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getScenesId())))
                .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<Scenes> sceneses = mongoTemplate.find(query, Scenes.class, RE_SCENESES.getName());
        // Collections.sort(sceneses, (o1, o2) -> (o2.getUpdateDate().getTime()
        // - o1.getUpdateDate().getTime()) > 0 ? 1 : -1);

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
                .withTotal(mongoTemplate.count(query, RE_SCENESES.getName()))
                .withRecords(sceneses);
    }

    @Override
    public ScenesSaveResult save(ScenesSave model) {
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
            scenes.preInsert();
        } else {
            scenes.preUpdate();
        }

        Scenes saved = mongoTemplate.save(scenes, RE_SCENESES.getName());
        return ScenesSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public ScenesDeleteResult delete(ScenesDelete model) {
        return ScenesDeleteResult.builder().deletedCount(doDeleteGracefully(model, RE_SCENESES)).build();
    }

}
