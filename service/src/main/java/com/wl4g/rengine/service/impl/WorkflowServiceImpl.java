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
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_WORKFLOWS;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.entity.Workflow;
import com.wl4g.rengine.service.WorkflowService;
import com.wl4g.rengine.service.model.WorkflowDelete;
import com.wl4g.rengine.service.model.WorkflowDeleteResult;
import com.wl4g.rengine.service.model.WorkflowQuery;
import com.wl4g.rengine.service.model.WorkflowSave;
import com.wl4g.rengine.service.model.WorkflowSaveResult;

/**
 * {@link WorkflowServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class WorkflowServiceImpl extends BasicServiceImpl implements WorkflowService {

    @Override
    public PageHolder<Workflow> query(WorkflowQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getWorkflowId()),
                isCriteria("engine", model.getEngine()), isCriteria("scenesId", model.getScenesId())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<Workflow> workflows = mongoTemplate.find(query, Workflow.class, RE_WORKFLOWS.getName());
        // Collections.sort(workflows, (o1, o2) -> (o2.getUpdateDate().getTime()
        // - o1.getUpdateDate().getTime()) > 0 ? 1 : -1);

        return new PageHolder<Workflow>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, RE_WORKFLOWS.getName()))
                .withRecords(workflows);
    }

    @Override
    public WorkflowSaveResult save(WorkflowSave model) {
        final Workflow workflow = model;
        // @formatter:off
        //final Workflow workflow = Workflow.builder()
        //        .id(model.getId())
        //        .name(model.getName())
        //        .orgCode(model.getOrgCode())
        //        .enable(model.getEnable())
        //        .labels(model.getLabels())
        //        .remark(model.getRemark())
        //        .build();
        // @formatter:on
        notNullOf(workflow, "workflow");
        notNullOf(workflow.getScenesId(), "scenesId");

        if (isNull(workflow.getId())) {
            workflow.preInsert();
        } else {
            workflow.preUpdate();
        }

        Workflow saved = mongoTemplate.save(workflow, RE_WORKFLOWS.getName());
        return WorkflowSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public WorkflowDeleteResult delete(WorkflowDelete model) {
        return WorkflowDeleteResult.builder().deletedCount(doDeleteGracefully(model, RE_WORKFLOWS)).build();
    }

}
