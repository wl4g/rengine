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
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.defaultSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;

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
import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.WorkflowGraphService;
import com.wl4g.rengine.service.model.DeleteWorkflowGraph;
import com.wl4g.rengine.service.model.DeleteWorkflowGraphResult;
import com.wl4g.rengine.service.model.QueryWorkflowGraph;
import com.wl4g.rengine.service.model.SaveWorkflowGraph;
import com.wl4g.rengine.service.model.SaveWorkflowGraphResult;
import com.wl4g.rengine.service.mongo.GlobalMongoSequenceService;

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
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getGraphId()),
                isCriteria("workflowId", model.getWorkflowId())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<WorkflowGraph> graphs = mongoTemplate.find(query, WorkflowGraph.class,
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
        //     graph.setId(IdGenUtils.nextLong());
        //     graph.preInsert();
        // } else {
        //     graph.preUpdate();
        // }
        // @formatter:on

        graph.setId(IdGenUtils.nextLong()); // ignore frontend model 'id'
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
