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
package com.wl4g.rengine.service.impl;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.modCriteria;
import static java.util.Objects.isNull;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.SchedulingTrigger;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.SchedulingTriggerService;
import com.wl4g.rengine.service.model.DeleteSchedulingTrigger;
import com.wl4g.rengine.service.model.DeleteSchedulingTriggerResult;
import com.wl4g.rengine.service.model.QuerySchedulingTrigger;
import com.wl4g.rengine.service.model.SaveSchedulingTrigger;
import com.wl4g.rengine.service.model.SaveSchedulingTriggerResult;

/**
 * {@link SchedulingTriggerServiceImpl}
 * 
 * @author James Wong
 * @version 2023-01-08
 * @since v1.0.0
 */
@Service
public class SchedulingTriggerServiceImpl implements SchedulingTriggerService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<SchedulingTrigger> query(QuerySchedulingTrigger model) {
        final Query query = new Query(andCriteria(baseCriteria(model)))
                .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<SchedulingTrigger> triggeres = mongoTemplate.find(query, SchedulingTrigger.class,
                MongoCollectionDefinition.SCHEDULING_TRIGGER.getName());
        // Collections.sort(triggeres, (o1, o2) ->
        // safeLongToInt(o2.getUpdateDate().getTime() -
        // o1.getUpdateDate().getTime()));

        return new PageHolder<SchedulingTrigger>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.SCHEDULING_TRIGGER.getName()))
                .withRecords(triggeres);
    }

    @Override
    public List<SchedulingTrigger> findWithSharding(
            final @NotNull QuerySchedulingTrigger model,
            final @NotNull Integer divisor,
            final @NotNull Integer remainder) {

        final Query query = new Query(andCriteria(baseCriteria(model), isCriteria("_id", model.getTriggerId()),
                modCriteria("_id", divisor, remainder), isCriteria("properties.type", model.getType()))).with(defaultSort());

        List<SchedulingTrigger> triggers = mongoTemplate.find(query, SchedulingTrigger.class,
                MongoCollectionDefinition.SCHEDULING_TRIGGER.getName());
        Collections.sort(triggers, (o1, o2) -> safeLongToInt(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()));

        return triggers;
    }

    @Override
    public SaveSchedulingTriggerResult save(SaveSchedulingTrigger model) {
        SchedulingTrigger trigger = model;
        notNullOf(trigger, "trigger");

        if (isNull(trigger.getId())) {
            trigger.setId(IdGenUtils.nextLong());
            trigger.preInsert();
        } else {
            trigger.preUpdate();
        }

        SchedulingTrigger saved = mongoTemplate.insert(trigger, MongoCollectionDefinition.SCHEDULING_TRIGGER.getName());
        return SaveSchedulingTriggerResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteSchedulingTriggerResult delete(DeleteSchedulingTrigger model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.SCHEDULING_TRIGGER.getName());
        return DeleteSchedulingTriggerResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
