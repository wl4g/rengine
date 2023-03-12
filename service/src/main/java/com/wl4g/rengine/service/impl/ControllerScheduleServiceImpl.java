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
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_CONTROLLER_SCHEDULE;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.modIdCriteria;
import static java.util.Objects.isNull;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.ControllerSchedule;
import com.wl4g.rengine.service.ControllerScheduleService;
import com.wl4g.rengine.service.model.ControllerScheduleDelete;
import com.wl4g.rengine.service.model.ControllerScheduleDeleteResult;
import com.wl4g.rengine.service.model.ControllerScheduleQuery;
import com.wl4g.rengine.service.model.ControllerScheduleSaveResult;

/**
 * {@link ControllerScheduleServiceImpl}
 * 
 * @author James Wong
 * @version 2023-01-08
 * @since v1.0.0
 */
@Service
public class ControllerScheduleServiceImpl extends BasicServiceImpl implements ControllerScheduleService {

    @Override
    public PageHolder<ControllerSchedule> query(ControllerScheduleQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getScheduleId()),
                isCriteria("details.type", model.getType())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<ControllerSchedule> triggeres = mongoTemplate.find(query, ControllerSchedule.class,
                RE_CONTROLLER_SCHEDULE.getName());

        return new PageHolder<ControllerSchedule>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, RE_CONTROLLER_SCHEDULE.getName()))
                .withRecords(triggeres);
    }

    @Override
    public List<ControllerSchedule> findWithSharding(
            final @NotNull ControllerScheduleQuery model,
            final @NotNull Integer divisor,
            final @NotNull Integer remainder) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getScheduleId()),
                modIdCriteria(divisor, remainder), isCriteria("details.type", model.getType()))).with(defaultSort());

        final List<ControllerSchedule> schedules = mongoTemplate.find(query, ControllerSchedule.class,
                RE_CONTROLLER_SCHEDULE.getName());
        Collections.sort(schedules, (o1, o2) -> (o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()) > 0 ? 1 : -1);

        return schedules;
    }

    @Override
    public ControllerScheduleSaveResult save(ControllerSchedule model) {
        ControllerSchedule trigger = model;
        notNullOf(trigger, "trigger");

        if (isNull(trigger.getId())) {
            trigger.preInsert();
        } else {
            trigger.preUpdate();
        }

        ControllerSchedule saved = mongoTemplate.save(trigger, RE_CONTROLLER_SCHEDULE.getName());
        return ControllerScheduleSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public ControllerScheduleDeleteResult delete(ControllerScheduleDelete model) {
        return ControllerScheduleDeleteResult.builder()
                .deletedCount(doDeleteWithGracefully(model, RE_CONTROLLER_SCHEDULE))
                .build();
    }

}
