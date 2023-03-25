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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR ENGINE, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.executor.service.impl;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notEmpty;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Exceptions.getRootCausesString;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_RULE_SCRIPTS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_SCENESES;
import static com.wl4g.rengine.common.constants.RengineConstants.TenantedHolder.getColonKey;
import static com.wl4g.rengine.common.util.BsonAggregateFilters.RULE_SCRIPT_UPLOAD_LOOKUP_FILTERS;
import static com.wl4g.rengine.common.util.BsonAggregateFilters.WORKFLOW_GRAPH_RULE_SCRIPT_UPLOAD_LOOKUP_FILTERS;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.entity.RuleScript.RuleScriptWrapper;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.entity.Workflow.WorkflowEngine;
import com.wl4g.rengine.common.entity.Workflow.WorkflowWrapper;
import com.wl4g.rengine.common.entity.graph.StandardGraph;
import com.wl4g.rengine.common.entity.graph.StandardGraph.BaseNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.BootNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.NodeEdge;
import com.wl4g.rengine.common.entity.graph.StandardGraph.ProcessNode;
import com.wl4g.rengine.common.entity.graph.WorkflowGraph.WorkflowGraphWrapper;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.model.RuleScriptExecuteRequest;
import com.wl4g.rengine.common.model.RuleScriptExecuteResult;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;
import com.wl4g.rengine.common.model.WorkflowExecuteResult.ResultDescription;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.executor.execution.EngineConfig;
import com.wl4g.rengine.executor.execution.LifecycleExecutionService;
import com.wl4g.rengine.executor.meter.RengineExecutorMeterService;
import com.wl4g.rengine.executor.repository.MongoRepository;
import com.wl4g.rengine.executor.service.EngineExecutionService;

import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.string.ReactiveStringCommands;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.CustomLog;

/**
 * {@link ReactiveEngineExecutionServiceImpl}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/resteasy-reactive#asyncreactive-support
 */
@CustomLog
@Singleton
public class ReactiveEngineExecutionServiceImpl implements EngineExecutionService {

    @Inject
    EngineConfig engineConfig;

    @Inject
    RengineExecutorMeterService meterService;

    @Inject
    MongoRepository mongoRepository;

    @Inject
    LifecycleExecutionService lifecycleExecutionService;

    @Inject
    ReactiveRedisDataSource reactiveRedisDS; // RedisDataSource

    // see:https://quarkus.io/guides/redis#creating-the-maven-project
    ReactiveStringCommands<String, String> reactiveRedisStringCommands; // StringCommands

    @PostConstruct
    void init() {
        this.reactiveRedisStringCommands = reactiveRedisDS.string(String.class); // redisDS.string(String.class)
    }

    @Override
    public Uni<RespBase<ResultDescription>> execute(@NotNull RuleScriptExecuteRequest executeRequest) {
        final String notFoundErrmsg = format("Could't execute because not found rule script %s",
                executeRequest.getRuleScriptId());
        return findRuleScripts(singletonList(executeRequest.validate().getRuleScriptId())).map(rss -> {
            final RespBase<ResultDescription> resp = RespBase.create();
            resp.setRequestId(executeRequest.getRequestId());

            // Check for not found.
            if (CollectionUtils2.isEmpty(rss)) {
                log.warn(notFoundErrmsg);
                return resp.withCode(RetCode.NOT_FOUND_ERR)
                        .withStatus(WorkflowExecuteResult.STATUS_FAILED)
                        .withMessage(notFoundErrmsg);
            }

            final RuleWrapper rule = RuleWrapper.builder().engine(executeRequest.getEngine()).scripts(rss).build().validate();
            final Long ruleScriptId = rule.getEffectiveLatestScript().getId();

            // Notice: The purpose here is to execute a single Rule script (not
            // a real workflow), so you only need to generate a virtual
            // workflow objects to meet the parameters of the underlying API.
            rule.setId(ruleScriptId);
            final Long vGraphId = ruleScriptId;
            final Long vWorkflowId = ruleScriptId;

            // vistual workflow graph nodes.
            final List<BaseNode<?>> vNodes = new LinkedList<>();
            vNodes.add(new BootNode().withId("0").withName("The virtual Boot node"));
            vNodes.add(new ProcessNode().withId("1").withName("The virtual Process node").withRuleId(rule.getId()));

            // vistual workflow graph connections.
            final List<NodeEdge> vEdges = new LinkedList<>();
            vEdges.add(new NodeEdge("1", "0"));

            // vistual workflow graph.
            final WorkflowGraphWrapper vGraph = new WorkflowGraphWrapper();
            vGraph.setId(vGraphId);
            vGraph.setWorkflowId(vWorkflowId);
            vGraph.setRules(singletonList(rule));
            vGraph.setDetails(new StandardGraph(vNodes, vEdges));
            vGraph.validate();

            // vistual workflow.
            final WorkflowWrapper vWorkflow = WorkflowWrapper.builder()
                    .id(vWorkflowId)
                    // Notice: The current online script debugging execution
                    // only supports standard workflows.
                    .engine(WorkflowEngine.STANDARD_GRAPH)
                    .graphs(singletonList(vGraph))
                    .build();

            final ResultDescription result = lifecycleExecutionService.getExecution(vWorkflow.getEngine())
                    .execute(executeRequest, vWorkflow.validate(), false);
            log.debug("Executed virtual workflow result : {}", result);

            return resp.withCode(RetCode.OK)
                    .withStatus(WorkflowExecuteResult.STATUS_SUCCESS)
                    .withData(RuleScriptExecuteResult.builder()
                            .success(result.getSuccess())
                            .valueMap(result.getValueMap())
                            .reason(result.getReason())
                            .build());
        })
                .ifNoItem()
                .after(Duration.ofMillis((long) (executeRequest.getTimeout() * DEFAULT_LOAD_TIMEOUT_MAX_RATE)))
                .failWith(() -> new RengineException(notFoundErrmsg));
    }

    @Override
    public Uni<RespBase<WorkflowExecuteResult>> execute(@NotNull WorkflowExecuteRequest executeRequest) {
        return findScenesWorkflowGraphRulesWithCached(executeRequest.validate(), 1).chain(sceneses -> {
            final RespBase<WorkflowExecuteResult> resp = RespBase.create();
            resp.setRequestId(executeRequest.getRequestId());
            try {
                if (CollectionUtils2.isEmpty(sceneses)) {
                    return Uni.createFrom()
                            .item(() -> resp.withCode(RetCode.NOT_FOUND_ERR)
                                    .withStatus(WorkflowExecuteResult.STATUS_FAILED)
                                    .withMessage("Invalid scenesCodes"));
                }
                // Execution to workflow graphs.
                final WorkflowExecuteResult result = lifecycleExecutionService.execute(executeRequest, sceneses);
                log.debug("Executed workflow result : {}", result);

                // Check for success completion.
                if (sceneses.size() > 0 && result.errorCount() == sceneses.size()) {
                    resp.setStatus(WorkflowExecuteResult.STATUS_FAILED);
                } else if (result.errorCount() == 0) {
                    resp.setStatus(WorkflowExecuteResult.STATUS_SUCCESS);
                } else {
                    resp.setStatus(WorkflowExecuteResult.STATUS_PART_SUCCESS);
                }
                resp.setData(result);
            } catch (Throwable e) {
                final String errmsg = format("Could not to execution evaluate of requestId: '%s', reason: %s",
                        executeRequest.getRequestId(), getRootCausesString(e, true));
                log.error(errmsg, e);
                resp.withCode(RetCode.SYS_ERR).withMessage(errmsg);
            }
            return Uni.createFrom().item(() -> resp);
        });
    }

    @Override
    public Uni<List<RuleScriptWrapper>> findRuleScripts(@NotEmpty List<Long> ruleScriptIds) {
        notEmptyOf(ruleScriptIds, "ruleScriptIds");

        final List<Bson> aggregates = Lists.newArrayList();
        aggregates.add(Aggregates.match(Filters.in("_id", ruleScriptIds)));
        RULE_SCRIPT_UPLOAD_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));

        final ReactiveMongoCollection<Document> collection = mongoRepository.getReactiveCollection(RE_RULE_SCRIPTS);
        return collection.aggregate(aggregates)
                .map(ruleScriptDoc -> RuleScriptWrapper
                        .validate(BsonEntitySerializers.fromDocument(ruleScriptDoc, RuleScriptWrapper.class)))
                .collect()
                .asList();
    }

    /**
     * Query collection dependencies with equivalent bson codes example:
     * 
     * <pre>
     *  // Tools: online mongodb aggregate: https://mongoplayground.net/p/bPKYXCJwXdl
     *  //
     *  // Query collections dependencies:
     *  //                             request evaluate scenesCodes:                —> t_sceneses
     *  // ↳ one-to-many (t_workflows.scenesId = sceneses._id)                      —> t_workflows
     *  // ↳ one-to-many (t_workflow_graphs.workflowId = workflows._id)             —> t_workflow_graphs
     *  // ↳ many-to-many (t_rules._id in workflow_graphs[].nodes[].ruleId)         —> t_rules
     *  // ↳ one-to-many (t_rule_scripts.rule_id = rules[]._id)                     —> t_rule_scripts
     *  // ↳ many-to-many (t_uploads._id in rule_scripts[].uploadIds)               —> t_uploads
     *  //
     *  db.getCollection('t_sceneses').aggregate([
     *      // 首先过滤 scenesCode (evaluator 接收 client(biz-app) 请求支持批量处理)
     *      { $match: { "scenesCode": { $in: ["ecommerce_trade_gift", "vm_health_detect", "vm_process_watch_restart", "iot_temp_warning"] } } },
     *      { $match: { "enable": { $eq: 1 } } },
     *      { $match: { "delFlag": { $eq: 0 } } },
     *      { $project: { "_class": 0, "delFlag": 0 } }, // 控制 sceneses 集返回列(投射)
     *      { $lookup: {
     *          from: "t_workflows", // 关联 workflows 表
     *          let: { scenes_id: { $toLong: "$_id" } }, // 定义外键关联变量
     *          pipeline: [
     *              { $match: { $expr: { $eq: [ "$scenesId", "$$scenes_id" ] } } }, // 外键等于关联
     *              { $match: { "enable": { $eq: 1 } } },
     *              { $match: { "delFlag": { $eq: 0 } } },
     *              { $project: { "_class": 0, "delFlag": 0 } },
     *              { $lookup: {
     *                  from: "t_workflow_graphs", // 继续关联 workflow_graphs 表
     *                  let: { workflow_id: { $toLong: "$_id" } },
     *                  pipeline: [
     *                      { $match: { $expr: { $eq: [ "$workflowId", "$$workflow_id" ] } } },
     *                      { $match: { "enable": { $eq: 1 } } },
     *                      { $match: { "delFlag": { $eq: 0 } } },
     *                      { $project: { "_class": 0, "delFlag": 0 } },
     *                      { $sort: { "revision": -1 } }, // 倒序排序, 取 revision(version) 最大的 graph 即最新版
     *                      { $limit: 1 },
     *                      { $lookup: {
     *                          from: "t_rules",
     *                          // 定义外键关联变量, 并通过 $map 函数提取 ruleIds(int64) 列表
     *                          let: { rule_ids: { $map: { input: "$details.nodes", in: { $toLong: "$$this.ruleId" } } } },
     *                          pipeline: [
     *                              { $match: { $expr: { $in: [ "$_id",  "$$rule_ids" ] } } },
     *                              { $match: { "enable": { $eq: 1 } } },
     *                              { $match: { "delFlag": { $eq: 0 } } },
     *                              { $project: { "_class": 0, "delFlag": 0 } },
     *                              { $lookup: {
     *                                  from: "t_rule_scripts",
     *                                  let: { rule_id: { $toLong: "$_id" } },
     *                                  pipeline: [
     *                                      { $match: { $expr: { $eq: [ "$ruleId",  "$$rule_id" ] } } },
     *                                      { $match: { "enable": { $eq: 1 } } },
     *                                      { $match: { "delFlag": { $eq: 0 } } },
     *                                      { $project: { "_class": 0, "delFlag": 0 } },
     *                                      { $sort: { "revision": -1 } }, // 倒序排序, 取 revision(version) 最大的 ruleScript 即最新版
     *                                      { $limit: 1 },
     *                                      { $lookup: {
     *                                          from: "t_uploads", // 继续关联 uploads 表
     *                                          // 定义外键关联变量 uploadIds(int64), 并通过 $map 函数进行类型转换以确保匹配安全
     *                                          let: { upload_ids: { $map: { input: "$uploadIds", in: { $toLong: "$$this"} } } },
     *                                          pipeline: [
     *                                              { $match: { $expr: { $in: [ "$_id",  "$$upload_ids" ] } } }, // 由于父级未使用 UNWIND 因此这里使用 IN 外键关联
     *                                              { $match: { "enable": { $eq: 1 } } },
     *                                              { $match: { "delFlag": { $eq: 0 } } },
     *                                              { $project: { "_class": 0, "delFlag": 0 } }
     *                                          ],
     *                                          as: "uploads"
     *                                          }
     *                                      }
     *                                  ],
     *                                  as: "scripts"
     *                                  }
     *                              }
     *                          ],
     *                          as: "rules"
     *                          }
     *                      }
     *                  ],
     *                  as: "graphs" // 倒序后第一个为最新版
     *                  }
     *              }
     *          ],
     *          as: "workflows"
     *          }
     *      }
     *  ])
     * </pre>
     * 
     * @see https://www.notion.so/scenesworkflow-rules-uploads-f8e5a6f14fb64f858479b6565fb52142
     * @see https://www.mongodb.com/docs/v4.2/tutorial/model-embedded-one-to-many-relationships-between-documents/
     */
    @Override
    public Uni<List<ScenesWrapper>> findScenesWorkflowGraphRules(
            @NotEmpty List<String> scenesCodes,
            @Min(1) @Max(1024) int revisions) {
        notEmpty(scenesCodes, "scenesCodes");
        isTrue(revisions >= 1 && revisions <= 1024, "revision %s must >= 1 and <= 1024", revisions);

        // Notice: The following is almost completely just the Java version
        // translated into the corresponding mongo js version, but it seems that
        // the execution will report an error, such as not being able to
        // recognize $scenes_id??? Therefore, it is forced to use the method of
        // directly compiling the workflow lookup bson string.
        //
        //// @formatter:off
        //final Bson uploadsLookup = Aggregates.lookup(SYS_UPLOADS.getName(),
        //        asList(new Variable<>("upload_ids",
        //                BsonDocument.parse("{ $map: { input: \"$uploadIds\", in: { $toLong: \"$$this.ruleId\" } } }"))),
        //        asList(Aggregates
        //                .match(Filters.expr(new Document("$in", asList(new BsonString("$_id"), new BsonString("$$upload_ids"))))),
        //                DEFAULT_ENABLE_FILTER, DEFAULT_DELFLAT_FILTER, DEFAULT_PROJECT_FILTER),
        //        "uploads");
        //
        //final Bson ruleScriptsLookup = Aggregates.lookup(RE_RULE_SCRIPTS.getName(),
        //        asList(new Variable<>("rule_id", BsonDocument.parse("{ $toLong: \"$_id\" }"))),
        //        asList(Aggregates.match(Filters.expr(Filters.eq("$ruleId", "$$rule_id"))), DEFAULT_ENABLE_FILTER,
        //                DEFAULT_DELFLAT_FILTER, DEFAULT_PROJECT_FILTER, DEFAULT_SORT, DEFAULT_LIMIT, uploadsLookup),
        //        "scripts");
        //
        //final Bson rulesLookup = Aggregates.lookup(RE_RULES.getName(), asList(new Variable<>("rule_ids",
        //        BsonDocument.parse("{ $map: { input: \"$nodes\", in: { $toLong: \"$$this.ruleId\" } } }"))),
        //// $in 表达式匹配应直接使用 Document 对象? 否则:
        //// Issue1: 若使用 Filters.in("$_id","$$ruleIds") 则会生成为: {$expr:{$in:["$$ruleIds"]}}} 它会报错至少需要2个参数
        //// Issue2: 若使用 Filters.in("_id","$_id","$$ruleIds") 则会生成: {$expr:{"_id":{$in:["$_id","$$ruleIds"]}}}} 查询结果集不对, 即 rules[] 重复关联了uploads
        //        asList(Aggregates
        //                .match(Filters.expr(new Document("$in", asList(new BsonString("$_id"), new BsonString("$$rule_ids"))))),
        //                DEFAULT_ENABLE_FILTER, DEFAULT_DELFLAT_FILTER, DEFAULT_PROJECT_FILTER, ruleScriptsLookup),
        //        "rules");
        //
        //final Bson workflowGraphLookup = Aggregates.lookup(RE_WORKFLOW_GRAPHS.getName(),
        //        asList(new Variable<>("workflow_id", BsonDocument.parse("{ $toLong: \"$_id\" }"))),
        //        asList(Aggregates.match(Filters.expr(Filters.eq("$workflowId", "$$workflow_id"))), DEFAULT_ENABLE_FILTER,
        //                DEFAULT_DELFLAT_FILTER, DEFAULT_PROJECT_FILTER, DEFAULT_SORT, DEFAULT_LIMIT, rulesLookup),
        //        "graphs");
        //
        //final Bson workflowLookup = Aggregates.lookup(RE_WORKFLOWS.getName(), asList(new Variable<>("scenes_id", "$_id")),
        //        asList(Aggregates.match(Filters.expr(Filters.eq("$scenesId", "$$scenes_id"))), DEFAULT_ENABLE_FILTER,
        //                DEFAULT_DELFLAT_FILTER, DEFAULT_PROJECT_FILTER, workflowGraphLookup),
        //        "workflows");
        //// @formatter:on

        final List<Bson> aggregates = new ArrayList<>(2);
        aggregates.add(Aggregates.match(Filters.in("scenesCode", scenesCodes)));
        WORKFLOW_GRAPH_RULE_SCRIPT_UPLOAD_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));
        // aggregates.add(workflowLookup);
        // The temporary collections are automatically created.
        // aggregates.add(Aggregates.merge("_tmp_load_scenes_with_cascade"));

        final ReactiveMongoCollection<Document> collection = mongoRepository.getReactiveCollection(RE_SCENESES);
        final Multi<Document> scenesesMulti = collection.aggregate(aggregates);
        return scenesesMulti.map(scenesDoc -> {
            // Solution-1:
            // @formatter:off
                //    log.debug("Found scenes object by scenesCodes: {} to json: {}", scenesCodes, scenesDoc.toJson());
                //    final Map<String, Object> scenesMap = BsonUtils2.asMap(scenesDoc);
                //    // Notice: When the manager uses spring-data-mongo to save
                //    // the entity by default, it will set the id to '_id'
                //    final String scenesJson = toJSONString(scenesMap).replaceAll("\"_id\":", "\"id\":");
                //    final ScenesWrapper scenes = parseJSON(scenesJson, ScenesWrapper.class);
                //    return ScenesWrapper.validate(scenes);
            // @formatter:on
            // Solution-2:
            return BsonEntitySerializers.fromDocument(scenesDoc, ScenesWrapper.class).validate();
        }).collect().asList();
    }

    @SuppressWarnings("deprecation")
    Uni<List<ScenesWrapper>> findScenesWorkflowGraphRulesWithCached(
            final @NotNull WorkflowExecuteRequest executeRequest,
            final @Min(1) @Max(1024) int revisions) {
        executeRequest.validate();

        //
        // Notice: Since the bottom layer of ordinary blocking redisCommands is
        // also implemented using non-blocking redisReactiveCommands + await,
        // when the return type of the service main method is Uni, redisCommands
        // cannot be used, and an error will be reported such as: The current
        // thread cannot be blocked: vert.x-eventloop-thread-2.
        //
        // @formatter:off
        //final List<ScenesWrapper> cachedSceneses = safeList(executeRequest.getScenesCodes()).stream()
        //        .map(scenesCode -> parseJSON(redisStringCommands.get(getColonKey(engineConfig.scenesRulesCachedPrefix()).concat(scenesCode)),
        //                ScenesWrapper.class))
        //        .filter(s -> nonNull(s))
        //        .collect(toList());
        //
        //final List<String> cachedScenesCodes = cachedSceneses.stream().map(s -> s.getScenesCode()).collect(toList());
        //final List<String> uncachedScenesCodes = safeList(executeRequest.getScenesCodes()).stream()
        //        .filter(scenesCode -> !cachedScenesCodes.contains(scenesCode))
        //        .collect(toList());
        //
        //final List<ScenesWrapper> mergedSceneses = cachedSceneses;
        //if (!uncachedScenesCodes.isEmpty()) {
        //    final List<ScenesWrapper> sceneses = findScenesWorkflowGraphRules(uncachedScenesCodes, revisions);
        //    sceneses.stream()
        //            .forEach(s -> redisStringCommands.setex(getColonKey(engineConfig.scenesRulesCachedPrefix()).concat(s.getScenesCode()),
        //                    engineConfig.scenesRulesCachedExpire(), toJSONString(s)));
        //    mergedSceneses.addAll(sceneses);
        //}
        //
        //return mergedSceneses;
        // @formatter:on

        //
        // Notice: The due using to reactiveCommands, it currently only supports
        // batch querying of sceneses based on scenesCodes once, and does not
        // support splitting into multiple scenesCode for separate query.
        //
        // In fact, it can be split into multiple scenesCode to query separately
        // and then merged, so as to hit the cache more accurately, because each
        // request may have scenesCodes array intersect.
        //

        final String scenesCodesHash = Hashing.md5()
                .hashBytes(safeList(executeRequest.getScenesCodes()).stream().collect(joining("-")).getBytes(UTF_8))
                .toString();
        final String batchQueryingKey = getColonKey(engineConfig.scenesRulesCachedPrefix()).concat(scenesCodesHash);

        final Uni<List<ScenesWrapper>> scenesesUni = reactiveRedisStringCommands.get(batchQueryingKey).flatMap(scenesJsons -> {
            if (Objects.isNull(scenesJsons)) {
                // Querying from database.
                return findScenesWorkflowGraphRules(executeRequest.getScenesCodes(), revisions)
                        // Save to redis cache.
                        .chain(sceneses -> {
                            return reactiveRedisStringCommands.setex(batchQueryingKey,
                                    Duration.ofMillis(engineConfig.scenesRulesCachedExpire()).toSeconds(), toJSONString(sceneses))
                                    .map(res -> sceneses);
                        });
            }
            return Uni.createFrom().item(() -> parseJSON(scenesJsons, SCENES_TYPE_REF));
        })
                // If querying an workflow rules takes more than 85% of
                // the total timeout time, then the execution graph may
                // not have enough time to allow early abandonment of
                // execution.
                .ifNoItem()
                .after(Duration.ofMillis((long) (executeRequest.getTimeout() * DEFAULT_LOAD_TIMEOUT_MAX_RATE)))
                .fail();

        // Uni.combine().all().unis(scenesesUni).combinedWith(_sceneses->_sceneses);
        return scenesesUni;
    }

    public static final float DEFAULT_LOAD_TIMEOUT_MAX_RATE = 0.85f;

}
