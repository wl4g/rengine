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
package com.wl4g.rengine.server.admin.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.rengine.common.bean.mongo.Workflow;
import com.wl4g.rengine.server.admin.model.AddWorkflow;
import com.wl4g.rengine.server.admin.model.QueryWorkflow;
import com.wl4g.rengine.server.admin.model.QueryWorkflowResult;
import com.wl4g.rengine.server.admin.service.WorkflowService;
import com.wl4g.rengine.server.constants.RengineWebConstants.MongoCollectionDefinition;

/**
 * {@link WorkflowServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Service
public class WorkflowServiceImpl implements WorkflowService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public QueryWorkflowResult query(QueryWorkflow model) {
        // TODO use pagination

        Criteria criteria = new Criteria().orOperator(Criteria.where("name").is(model.getName()),
                Criteria.where("workflowId").is(model.getWorkflowId()), Criteria.where("projectId").is(model.getProjectId()),
                Criteria.where("labels").in(model.getLabels()));

        List<Workflow> workflows = mongoTemplate.find(new Query(criteria), Workflow.class,
                MongoCollectionDefinition.WORKFLOW.getName());

        Collections.sort(workflows, (o1, o2) -> safeLongToInt(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()));

        return QueryWorkflowResult.builder()
                .workflows(safeList(workflows).stream()
                        .map(p -> Workflow.builder()
                                .workflowId(p.getWorkflowId())
                                .name(p.getName())
                                .labels(p.getLabels())
                                .status(p.getStatus())
                                .remark(p.getRemark())
                                .updateBy(p.getUpdateBy())
                                .updateDate(p.getUpdateDate())
                                .build())
                        .collect(toList()))
                .build();
    }

    @Override
    public String save(AddWorkflow model) {
        Workflow workflow = Workflow.builder()
                .workflowId(model.getWorkflowId())
                .name(model.getName())
                .labels(model.getLabels())
                .status(model.getStatus())
                .remark(model.getRemark())
                .updateBy(model.getUpdateBy())
                .updateDate(model.getUpdateDate())
                .build();
        Workflow saved = mongoTemplate.insert(workflow, MongoCollectionDefinition.WORKFLOW.getName());
        return saved.getWorkflowId();
    }

}
