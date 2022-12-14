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
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.util.IdGenUtil;
import com.wl4g.rengine.manager.admin.model.DeleteWorkflowGraph;
import com.wl4g.rengine.manager.admin.model.DeleteWorkflowGraphResult;
import com.wl4g.rengine.manager.admin.model.QueryWorkflowGraph;
import com.wl4g.rengine.manager.admin.model.SaveWorkflowGraph;
import com.wl4g.rengine.manager.admin.model.SaveWorkflowGraphResult;
import com.wl4g.rengine.manager.admin.service.WorkflowGraphService;
import com.wl4g.rengine.manager.mongo.GlobalMongoSequenceService;

/**
 * {@link WorkflowGraphServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class WorkflowGraphServiceImpl implements WorkflowGraphService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    GlobalMongoSequenceService mongoSequenceService;

    @Override
    public PageHolder<WorkflowGraph> query(QueryWorkflowGraph model) {
        Query query = new Query(new Criteria().andOperator(Criteria.where("enable").is(BaseBean.ENABLED),
                Criteria.where("delFlag").is(BaseBean.DEL_FLAG_NORMAL),
                new Criteria().orOperator(Criteria.where("_id").is(model.getGraphId()),
                        Criteria.where("workflowId").is(model.getWorkflowId()), Criteria.where("enable").is(model.getEnable()),
                        Criteria.where("orgCode").is(model.getOrgCode()), Criteria.where("labels").in(model.getLabels()))));

        query.with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "revision", "updateDate")));

        List<WorkflowGraph> graphs = mongoTemplate.find(query, WorkflowGraph.class,
                MongoCollectionDefinition.WORKFLOW_GRAPHS.getName());

        // Collections.sort(graphs, (o1, o2) ->
        // safeLongToInt(o2.getUpdateDate().getTime()-o1.getUpdateDate().getTime()));

        return new PageHolder<WorkflowGraph>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.WORKFLOW_GRAPHS.getName()))
                .withRecords(graphs);
    }

    @Override
    public SaveWorkflowGraphResult save(SaveWorkflowGraph model) {
        WorkflowGraph graph = model;
        notNullOf(graph, "graph");
        notNullOf(graph.getWorkflowId(), "workflowId");

        // The workflow graph only increments the revision and does not allow
        // modification.
        // @formatter:off
        // if (isNull(graph.getId())) {
        //     graph.setId(IdGenUtil.nextLong());
        //     graph.preInsert();
        // } else {
        //     graph.preUpdate();
        // }
        // @formatter:on

        graph.setId(IdGenUtil.nextLong()); // ignore frontend model 'id'
        graph.preInsert();

        // Sets the current revision number, +1 according to the previous max
        // revision.
        // @formatter:off
        // final Query query = new Query(new Criteria().orOperator(Criteria.where("ruleId").is(graph.getWorkflowId()),
        //         Criteria.where("orgCode").is(model.getOrgCode()))).with(Sort.by(Direction.DESC, "revision")).limit(1);
        // final Long maxRevision = safeList(
        //         mongoTemplate.find(query, Long.class, MongoCollectionDefinition.WORKFLOW_GRAPHS.getName())).stream()
        //                 .findFirst()
        //                 .orElseThrow(() -> new IllegalStateException(
        //                         format("Could not get max revision by workflowId: %s, orgCode: %s", graph.getWorkflowId(),
        //                                 graph.getOrgCode())));
        // graph.setRevision(1 + maxRevision);
        // @formatter:on

        graph.setRevision(mongoSequenceService.getNextSequence(GlobalMongoSequenceService.GRAPHS_REVISION_SEQ));

        WorkflowGraph saved = mongoTemplate.insert(graph, MongoCollectionDefinition.WORKFLOW_GRAPHS.getName());
        return SaveWorkflowGraphResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteWorkflowGraphResult delete(DeleteWorkflowGraph model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.WORKFLOW_GRAPHS.getName());
        return DeleteWorkflowGraphResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
