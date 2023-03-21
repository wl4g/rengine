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
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_WORKFLOW_GRAPHS;
import static com.wl4g.rengine.service.mongo.QueryHolder.DEFAULT_FIELD_REVISION;
import static com.wl4g.rengine.service.mongo.QueryHolder.DEFAULT_FIELD_UPDATE_DATE;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.descSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.infra.common.io.FileIOUtils.ReadTailFrame;
import com.wl4g.infra.common.reflect.ParameterizedTypeReference;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.client.core.config.ClientConfig;
import com.wl4g.rengine.common.entity.graph.WorkflowGraph;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;
import com.wl4g.rengine.common.util.ScriptEngineUtil;
import com.wl4g.rengine.service.WorkflowGraphService;
import com.wl4g.rengine.service.model.WorkflowGraphDelete;
import com.wl4g.rengine.service.model.WorkflowGraphDeleteResult;
import com.wl4g.rengine.service.model.WorkflowGraphLogfile;
import com.wl4g.rengine.service.model.WorkflowGraphLogfileResult;
import com.wl4g.rengine.service.model.WorkflowGraphQuery;
import com.wl4g.rengine.service.model.WorkflowGraphResultSave;
import com.wl4g.rengine.service.model.WorkflowGraphSave;
import com.wl4g.rengine.service.mongo.GlobalMongoSequenceService;

import lombok.CustomLog;

/**
 * {@link WorkflowGraphServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@CustomLog
@Service
public class WorkflowGraphServiceImpl extends BasicServiceImpl implements WorkflowGraphService {

    @Override
    public PageHolder<WorkflowGraph> query(WorkflowGraphQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isIdCriteria(model.getGraphId()),
                isCriteria("workflowId", model.getWorkflowId())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(),
                                descSort(DEFAULT_FIELD_REVISION, DEFAULT_FIELD_UPDATE_DATE)));

        final List<WorkflowGraph> graphs = mongoTemplate.find(query, WorkflowGraph.class, RE_WORKFLOW_GRAPHS.getName());

        return new PageHolder<WorkflowGraph>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, RE_WORKFLOW_GRAPHS.getName()))
                .withRecords(graphs);
    }

    @Override
    public WorkflowGraphResultSave save(WorkflowGraphSave model) {
        WorkflowGraph graph = model;
        notNullOf(graph, "workflowGraph");
        graph.validate();

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

        graph.preInsert();

        // Sets the current revision number, +1 according to the previous max
        // revision.
        // @formatter:off
        // final Query query = new Query(new Criteria().orOperator(Criteria.where("ruleId").is(graph.getWorkflowId()),
        //         Criteria.where("orgCode").is(model.getOrgCode()))).with(Sort.by(Direction.DESC, "revision")).limit(1);
        // final Long maxRevision = safeList(
        //         mongoTemplate.find(query, Long.class, RE_WORKFLOW_GRAPHS.getName())).stream()
        //                 .findFirst()
        //                 .orElseThrow(() -> new IllegalStateException(
        //                         format("Could not get max revision by workflowId: %s, orgCode: %s", graph.getWorkflowId(),
        //                                 graph.getOrgCode())));
        // graph.setRevision(1 + maxRevision);
        // @formatter:on

        graph.setRevision(mongoSequenceService.getNextSequence(GlobalMongoSequenceService.GRAPHS_REVISION_SEQ));

        final WorkflowGraph saved = mongoTemplate.save(graph, RE_WORKFLOW_GRAPHS.getName());
        return WorkflowGraphResultSave.builder().id(saved.getId()).build();
    }

    @Override
    public WorkflowGraphDeleteResult delete(WorkflowGraphDelete model) {
        return WorkflowGraphDeleteResult.builder().deletedCount(doDeleteWithGracefully(model, RE_WORKFLOW_GRAPHS)).build();
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

        final String latestLogFile = ScriptEngineUtil.getLatestLogFile(config.getControllerLog().getBaseDir(),
                model.getWorkflowId(), false);
        if (isBlank(latestLogFile)) {
            throw new IllegalArgumentException(format("Could't to load workflow log file for %s.", model.getWorkflowId()));
        }

        final ReadTailFrame result = FileIOUtils.seekReadLines(latestLogFile, model.getStartPos(), model.getLimit(),
                line -> false);

        return WorkflowGraphLogfileResult.builder().frame(result).build();
    }

    @Override
    public RespBase<WorkflowExecuteResult> execute(@NotNull WorkflowExecuteRequest model) {
        final RespBase<WorkflowExecuteResult> resp = RespBase.create();
        notNullOf(model, "executeRequest");
        model.validate();

        log.info("Executing for {}, {}", config.getExecutorEndpoint(), model);
        try {
            final WorkflowExecuteResult result = RengineClient.builder()
                    .config(ClientConfig.builder()
                            .endpoint(config.getExecutorEndpoint())
                            .clientId(model.getClientId())
                            .clientSecret(model.getClientSecret())
                            .defaultTimeout(model.getTimeout())
                            .defaultBestEffort(model.getBestEffort())
                            .build())
                    .build()
                    .execute(model);

            log.info("Executed the result : {}", result);
            resp.withCode(RetCode.OK).withData(result);

        } catch (Throwable ex) {
            log.error("Failed to execute workflow graph.", ex);
            resp.withCode(RetCode.SYS_ERR).withMessage(ex.getMessage());
        }

        return resp;
    }

    static final ParameterizedTypeReference<RespBase<WorkflowExecuteResult>> WORKFLOW_EXECUTE_RESULT_TYPE = new ParameterizedTypeReference<>() {
    };

}
