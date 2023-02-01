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
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.ScheduleJob;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.ScheduleJobService;
import com.wl4g.rengine.service.model.DeleteScheduleJob;
import com.wl4g.rengine.service.model.DeleteScheduleJobResult;
import com.wl4g.rengine.service.model.QueryScheduleJob;
import com.wl4g.rengine.service.model.SaveScheduleJobResult;

/**
 * {@link ScheduleJobServiceImpl}
 * 
 * @author James Wong
 * @version 2023-01-08
 * @since v1.0.0
 */
@Service
public class ScheduleJobServiceImpl implements ScheduleJobService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<ScheduleJob> query(QueryScheduleJob model) {
        final Query query = new Query(
                andCriteria(baseCriteria(model), isIdCriteria(model.getJobId()), isCriteria("triggerId", model.getTriggerId())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<ScheduleJob> jobes = mongoTemplate.find(query, ScheduleJob.class,
                MongoCollectionDefinition.SCHEDULE_JOBS.getName());

        return new PageHolder<ScheduleJob>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.SCHEDULE_JOBS.getName()))
                .withRecords(jobes);
    }

    @Override
    public SaveScheduleJobResult save(ScheduleJob model) {
        ScheduleJob job = model;
        notNullOf(job, "job");

        if (isNull(job.getId())) {
            job.setId(IdGenUtils.nextLong());
            job.preInsert();
        } else {
            job.preUpdate();
        }

        final ScheduleJob saved = mongoTemplate.save(job, MongoCollectionDefinition.SCHEDULE_JOBS.getName());
        return SaveScheduleJobResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteScheduleJobResult delete(DeleteScheduleJob model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        final DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.SCHEDULE_JOBS.getName());
        return DeleteScheduleJobResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
