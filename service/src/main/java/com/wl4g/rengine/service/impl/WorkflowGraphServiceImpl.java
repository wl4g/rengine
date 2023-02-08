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
import static com.wl4g.rengine.service.mongo.QueryHolder.orCriteria;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.infra.common.io.FileIOUtils.ReadTailFrame;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.common.util.ScriptEngineUtil;
import com.wl4g.rengine.service.WorkflowGraphService;
import com.wl4g.rengine.service.config.RengineServiceProperties;
import com.wl4g.rengine.service.model.WorkflowDeleteGraph;
import com.wl4g.rengine.service.model.WorkflowGraphDeleteResult;
import com.wl4g.rengine.service.model.WorkflowGraphLogfile;
import com.wl4g.rengine.service.model.WorkflowGraphLogfileResult;
import com.wl4g.rengine.service.model.WorkflowGraphQuery;
import com.wl4g.rengine.service.model.WorkflowGraphResultSave;
import com.wl4g.rengine.service.model.WorkflowGraphSave;
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
    RengineServiceProperties config;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    GlobalMongoSequenceService mongoSequenceService;

    // @Autowired
    // MinioClientManager minioClientManager;

    @Override
    public PageHolder<WorkflowGraph> query(WorkflowGraphQuery model) {
        final Query query = new Query(orCriteria(isIdCriteria(model.getGraphId()),
                andCriteria(baseCriteria(model), isCriteria("workflowId", model.getWorkflowId()))))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), defaultSort()));

        final List<WorkflowGraph> graphs = mongoTemplate.find(query, WorkflowGraph.class,
                MongoCollectionDefinition.T_WORKFLOW_GRAPHS.getName());

        return new PageHolder<WorkflowGraph>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.T_WORKFLOW_GRAPHS.getName()))
                .withRecords(graphs);
    }

    @Override
    public WorkflowGraphResultSave save(WorkflowGraphSave model) {
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
        //         mongoTemplate.find(query, Long.class, MongoCollectionDefinition.T_WORKFLOW_GRAPHS.getName())).stream()
        //                 .findFirst()
        //                 .orElseThrow(() -> new IllegalStateException(
        //                         format("Could not get max revision by workflowId: %s, orgCode: %s", graph.getWorkflowId(),
        //                                 graph.getOrgCode())));
        // graph.setRevision(1 + maxRevision);
        // @formatter:on

        graph.setRevision(mongoSequenceService.getNextSequence(GlobalMongoSequenceService.GRAPHS_REVISION_SEQ));

        WorkflowGraph saved = mongoTemplate.save(graph, MongoCollectionDefinition.T_WORKFLOW_GRAPHS.getName());
        return WorkflowGraphResultSave.builder().id(saved.getId()).build();
    }

    @Override
    public WorkflowGraphDeleteResult delete(WorkflowDeleteGraph model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.T_WORKFLOW_GRAPHS.getName());
        return WorkflowGraphDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

    @Override
    public WorkflowGraphLogfileResult logtail(@NotNull WorkflowGraphLogfile model) {
        /**
         * TODO The best way is to let the rengine executor write to OSS in real
         * time, but unfortunately MinIO/S3 does not support append writing
         * (although it supports object merging, but it is still difficult to
         * achieve), unless you use Alibaba Cloud OSS (supports real-time append
         * writing), but this not a neutral approach. Therefore, at present,
         * only direct reading and writing of disks is realized, and then shared
         * mounts such as juiceFS, s3fs-fuse, ossfs, etc. can be used to realize
         * clustering. see to:
         * {@link com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine#init}
         */
        //// @formatter:off
        //final GetObjectArgs args = GetObjectArgs.builder()
        //        // .bucket(config.bucket())
        //        // .region(config.region())
        //        // .object(objectPrefix)
        //        .build();
        //final File localFile = null;
        //try (GetObjectResponse result = minioClientManager.getMinioClient().getObject(args);) {
        //    int available = minioResult.available();
        //    isTrue(available <= DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT, "Maximum file object readable limit exceeded: %s",
        //            DEFAULT_EXECUTOR_S3_OBJECT_MAX_LIMIT);
        //    try (FileOutputStream out = new FileOutputStream(localFile, false);
        //            BufferedOutputStream bout = new BufferedOutputStream(out, DEFAULT_EXECUTOR_S3_OBJECT_READ_BUFFER);) {
        //        // ByteArrayOutputStream out = new ByteArrayOutputStream(4092);
        //        // result.transferTo(out);
        //        // out.toByteArray();
        //        minioResult.transferTo(bout);
        //        minioResult.skip(0);
        //        // return new ObjectResource(objectPrefix, binary, localFile,
        //        // available);
        //    }
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        //// @formatter:on

        final String latestLogFile = ScriptEngineUtil.getLatestLogFile(config.getScheduleJobLog().getBaseDir(),
                model.getWorkflowId(), false);
        if (isBlank(latestLogFile)) {
            throw new IllegalArgumentException(format("Could't to load workflow log file for %s.", model.getWorkflowId()));
        }

        final ReadTailFrame result = FileIOUtils.seekReadLines(latestLogFile, model.getStartPos(), model.getLimit(),
                line -> false);

        return WorkflowGraphLogfileResult.builder().frame(result).build();
    }

}
