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
package com.wl4g.rengine.evaluator.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notEmpty;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RULES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RULE_SCRIPTS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SCENESES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.UPLOADS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.WORKFLOWS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.WORKFLOW_GRAPHS;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.IteratorUtils;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Variable;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.lang.Exceptions;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.common.util.BsonUtils2;
import com.wl4g.rengine.evaluator.execution.ExecutionConfig;
import com.wl4g.rengine.evaluator.execution.LifecycleExecutionService;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService;
import com.wl4g.rengine.evaluator.repository.MongoRepository;
import com.wl4g.rengine.evaluator.service.EvaluatorService;

import io.smallrye.mutiny.Uni;
import lombok.CustomLog;

/**
 * {@link EvaluatorServiceImpl}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/resteasy-reactive#asyncreactive-support
 */
@CustomLog
@Singleton
public class EvaluatorServiceImpl implements EvaluatorService {

    @Inject
    ExecutionConfig config;

    @Inject
    EvaluatorMeterService meterService;

    @Inject
    LifecycleExecutionService lifecycleExecutionService;

    @Inject
    MongoRepository mongoRepository;

    @Override
    public Uni<RespBase<EvaluationResult>> evaluate(final @NotNull @Valid Evaluation evaluation) {
        return Uni.createFrom().item(() -> {
            RespBase<EvaluationResult> resp = RespBase.create();
            try {
                // Query the sceneses of cascade by scenesCode.
                final List<ScenesWrapper> sceneses = safeList(findScenesWorkflowGraphRules(evaluation.getScenesCodes(), 1));

                // Execution to workflow graphs.
                final EvaluationResult result = lifecycleExecutionService.execute(evaluation, sceneses);

                // Check for success completes.
                if (safeList(result.getResults()).stream().filter(res -> res.getSuccess()).count() == sceneses.size()) {
                    resp.setStatus(EvaluationResult.STATUS_ALL_SUCCESS);
                } else {
                    resp.setStatus(EvaluationResult.STATUS_PART_SUCCESS);
                }
                resp.setData(result);
            } catch (Throwable e) {
                final String errmsg = format("Could not to execution evaluate of requestId: '%s', reason: %s",
                        evaluation.getRequestId(), Exceptions.getRootCausesString(e, true));
                log.error(errmsg, e);
                resp.withCode(RetCode.SYS_ERR).withMessage(errmsg);
            }
            return resp.withRequestId(evaluation.getRequestId());
        });
    }

    // TODO using cache
    /**
     * Query collection dependencies with equivalent bson query codes example:
     * 
     * <pre>
     *    // Tools: online mongodb aggregate: https://mongoplayground.net/p/bPKYXCJwXdl
     *    //
     *    // Query collections dependencies:
     *    //                                  request evaluate scenesCodes:         —> sceneses
     *    // ↳ one-to-one (workflows.scenesId = sceneses._id)                       —> workflows
     *    // ↳ one-to-many (workflow_graphs.workflowId = workflows._id)             —> workflow_graphs
     *    // ↳ many-to-many (rules._id in workflow_graphs[].nodes[].ruleId)         —> rules
     *    // ↳ one-to-many (rule_scripts.rule_id = rules[]._id)                     —> rule_scripts
     *    // ↳ many-to-many (uploads._id in rule_scripts[].uploadIds)               —> uploads
     *    //
     *    db.getCollection('t_sceneses').aggregate([
     *        // 首先过滤 scenesCode (evaluator 接收 client(biz-app) 请求支持批量处理)
     *        { $match: { "scenesCode": { $in: ["ecommerce_trade_gift"] } } },
     *        { $match: { "enable": { $eq: 1 } } },
     *        { $match: { "delFlag": { $eq: 0 } } },
     *        { $project: { "_class": 0, "delFlag": 0 } }, // 控制 sceneses 集返回列(投射)
     *        { $lookup: {
     *            from: "t_workflows", // 关联 workflows 表
     *            let: { scenesId: { $toLong: "$_id" } }, // 定义外键关联变量
     *            pipeline: [
     *                { $match: { $expr: { $eq: [ "$scenesId",  "$$scenesId" ] } } }, // 外键等于关联
     *                { $match: { "enable": { $eq: 1 } } },
     *                { $match: { "delFlag": { $eq: 0 } } },
     *                { $project: { "_class": 0, "delFlag": 0 } },
     *                { $lookup: {
     *                    from: "t_workflow_graphs", // 继续关联 workflow_graphs 表
     *                    let: { workflowId: { $toLong: "$workflowId" } },
     *                    pipeline: [
     *                        { $match: { $expr: { $eq: [ "$workflowId", "$workflowId" ] } } },
     *                        { $match: { "enable": { $eq: 1 } } },
     *                        { $match: { "delFlag": { $eq: 0 } } },
     *                        { $sort: { "revision": -1 } }, // 倒序排序, 取 revision(version) 最大的 graph 即最新版
     *                        { $limit: 2 },
     *                        { $project: { "_class": 0, "delFlag": 0 } },
     *                        { $lookup: {
     *                            from: "t_rules",
     *                            // 定义外键关联变量, 并通过 $map 函数提取 ruleIds(int64) 列表
     *                            let: { ruleIds: { $map: { input: "$nodes", in: { $toLong: "$$this.ruleId" } } } },
     *                            pipeline: [
     *                                { $match: { $expr: { $in: [ "$_id",  "$$ruleIds" ] } } },
     *                                { $match: { "enable": { $eq: 1 } } },
     *                                { $match: { "delFlag": { $eq: 0 } } },
     *                                { $project: { "_class": 0, "delFlag": 0 } },
     *                                { $lookup: {
     *                                    from: "t_rule_scripts",
     *                                    let: { ruleId: { $toLong: "$_id" } },
     *                                    pipeline: [
     *                                        { $match: { $expr: { $eq: [ "$ruleId",  "$ruleId" ] } } },
     *                                        { $match: { "enable": { $eq: 1 } } },
     *                                        { $match: { "delFlag": { $eq: 0 } } },
     *                                        { $sort: { "revision": -1 } }, // 倒序排序, 取 revision(version) 最大的 ruleScript 即最新版
     *                                        { $limit: 2 },
     *                                        { $project: { "_class": 0, "delFlag": 0 } },
     *                                        { $lookup: {
     *                                            from: "t_uploads", // 继续关联 uploads 表
     *                                            // 定义外键关联变量 uploadIds(int64), 并通过 $map 函数进行类型转换以确保匹配安全
     *                                            let: { uploadIds: { $map: { input: "$uploadIds", in: { $toLong: "$$this"} } } },
     *                                            pipeline: [
     *                                                { $match: { $expr: { $in: [ "$_id",  "$$uploadIds" ] } } }, // 由于父级未使用 UNWIND 因此这里使用 IN 外键关联
     *                                                { $match: { "enable": { $eq: 1 } } },
     *                                                { $match: { "delFlag": { $eq: 0 } } },
     *                                                { $project: { "_class": 0, "delFlag": 0 } }
     *                                            ],
     *                                            as: "uploads"
     *                                            }
     *                                        }
     *                                    ],
     *                                    as: "scripts"
     *                                    }
     *                                }
     *                            ],
     *                            as: "rules"
     *                            }
     *                        }
     *                    ],
     *                    as: "graphs" // 倒序后第一个为最新版
     *                    }
     *                }
     *            ],
     *            as: "workflows"
     *            }
     *        }
     *    ])
     * </pre>
     * 
     * @see https://www.notion.so/scenesworkflow-rules-uploads-f8e5a6f14fb64f858479b6565fb52142
     * @see https://www.mongodb.com/docs/v4.2/tutorial/model-embedded-one-to-many-relationships-between-documents/
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<ScenesWrapper> findScenesWorkflowGraphRules(
            @NotEmpty List<String> scenesCodes,
            @Min(1) @Max(1024) int revisions) {
        notEmpty(scenesCodes, "scenesCodes");
        isTrue(revisions >= 1 && revisions <= 1024, "revision %s must >= 1 and <= 1024", revisions);

        final MongoCollection<Document> collection = mongoRepository.getCollection(SCENESES);

        // Common show projection.
        final Bson enableFilter = Aggregates.match(Filters.eq("enable", BaseBean.ENABLED));
        final Bson delFlagFilter = Aggregates.match(Filters.eq("delFlag", BaseBean.DEL_FLAG_NORMAL));
        final Bson project = Aggregates.project(Projections.fields(Projections.exclude("_class", "delFlag")));

        Bson uploadsLookup = Aggregates.lookup(UPLOADS.getName(), asList(new Variable<>("uploadIds", "$uploadIds")),
                asList(Aggregates
                        .match(Filters.expr(new Document("$in", asList(new BsonString("$_id"), new BsonString("$$uploadIds"))))),
                        enableFilter, delFlagFilter, project),
                "uploads");

        Bson ruleScriptsLookup = Aggregates.lookup(RULE_SCRIPTS.getName(),
                asList(new Variable<>("ruleId", BsonDocument.parse("{ $toLong: \"$_id\" }"))),
                asList(Aggregates.match(Filters.expr(Filters.eq("ruleId", "$ruleId"))), enableFilter, delFlagFilter,
                        Aggregates.sort(new Document("revision", -1)), Aggregates.limit(revisions), project, uploadsLookup),
                "scripts");

        Bson rulesLookup = Aggregates.lookup(RULES.getName(), asList(new Variable<>("ruleIds",
                BsonDocument.parse("{ $map: { input: \"$nodes\", in: { $toLong: \"$$this.ruleId\" } } }"))),
        // @formatter:off
        // $in 表达式匹配应直接使用 Document 对象? 否则:
        // Issue1: 若使用 Filters.in("$_id","$$ruleIds") 则会生成为: {$expr:{$in:["$$ruleIds"]}}} 它会报错至少需要2个参数
        // Issue2: 若使用 Filters.in("_id","$_id","$$ruleIds") 则会生成: {$expr:{"_id":{$in:["$_id","$$ruleIds"]}}}} 查询结果集不对, 即 rules[] 重复关联了uploads
        // @formatter:on
                asList(Aggregates
                        .match(Filters.expr(new Document("$in", asList(new BsonString("$_id"), new BsonString("$$ruleIds"))))),
                        enableFilter, delFlagFilter, project, ruleScriptsLookup),
                "rules");

        Bson workflowGraphLookup = Aggregates.lookup(WORKFLOW_GRAPHS.getName(),
                asList(new Variable<>("workflowId", BsonDocument.parse("{ $toLong: \"$workflowId\" }"))),
                asList(Aggregates.match(Filters.expr(Filters.eq("scenesId", "$$scenesId"))), enableFilter, delFlagFilter,
                        Aggregates.sort(new Document("revision", -1)), Aggregates.limit(revisions), project, rulesLookup),
                "graphs");

        Bson workflowLookup = Aggregates.lookup(WORKFLOWS.getName(), asList(new Variable<>("scenesId", "$_id")),
                asList(workflowGraphLookup), "workflows");

        final List<Bson> aggregates = Lists.newArrayList();
        aggregates.add(Aggregates.match(Filters.in("scenesCode", scenesCodes)));
        aggregates.add(enableFilter);
        aggregates.add(delFlagFilter);
        aggregates.add(project);
        aggregates.add(workflowLookup);
        // The temporary collections are automatically created.
        // aggregates.add(Aggregates.merge("_tmp_load_scenes_with_cascade"));

        final MongoCursor<ScenesWrapper> cursor = collection.aggregate(aggregates)
                .batchSize(config.maxQueryBatch())
                .map(scenesDoc -> {
                    log.debug("Found scenes object by scenesCodes: {} to json: {}", scenesCodes, scenesDoc.toJson());
                    final Map<String, Object> scenesMap = BsonUtils2.asMap(scenesDoc);
                    // Notice: When the manager uses spring-data-mongo to save
                    // the entity by default, it will set the id to '_id'
                    final String scenesJson = toJSONString(scenesMap).replaceAll("\"_id\":", "\"id\":");
                    final ScenesWrapper scenes = parseJSON(scenesJson, ScenesWrapper.class);
                    return ScenesWrapper.validate(scenes);
                })
                .iterator();
        try {
            return IteratorUtils.toList(cursor);
        } finally {
            cursor.close();
        }
    }

}
