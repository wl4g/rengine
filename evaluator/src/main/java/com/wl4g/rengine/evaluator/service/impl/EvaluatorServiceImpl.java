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
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RULES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SCENESES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.UPLOADS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.WORKFLOWS;
import static com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsName.evaluation_failure;
import static com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsName.evaluation_success;
import static com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsName.evaluation_total;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;

import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.common.collect.Lists;
import com.mongodb.Function;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Variable;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.rengine.common.entity.Rule;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.common.entity.UploadObject;
import com.wl4g.rengine.common.entity.Workflow;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.evaluator.execution.WorkflowExecution;
import com.wl4g.rengine.evaluator.execution.LifecycleExecutionFactory;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsTag;
import com.wl4g.rengine.evaluator.repository.MongoRepository;
import com.wl4g.rengine.evaluator.service.EvaluatorService;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EvaluatorServiceImpl}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/resteasy-reactive#asyncreactive-support
 */
// @ApplicationScoped
@Slf4j
@Singleton
public class EvaluatorServiceImpl implements EvaluatorService {

    @Inject
    EvaluatorMeterService meterService;

    @Inject
    LifecycleExecutionFactory lifecycleExecutionFactory;

    @Inject
    MongoRepository mongoRepository;

    @Override
    public Uni<RespBase<EvaluationResult>> evaluate(Evaluation evaluation) {
        return Uni.createFrom().item(() -> {
            RespBase<EvaluationResult> resp = RespBase.<EvaluationResult> create();

            // Load deep workflow scenes by code.
            final Scenes scenes = loadScenesWithCascade(evaluation.getScenesCode());
            final RuleEngine engine = scenes.getWorkflow().getRuleEngine();

            // Buried-point: total evaluation.
            meterService.counter(evaluation_total.getName(), evaluation_total.getHelp(), MetricsTag.CLIENT_ID,
                    evaluation.getClientId(), MetricsTag.SCENESCODE, evaluation.getScenesCode(), MetricsTag.ENGINE, engine.name())
                    .increment();

            try {
                WorkflowExecution execution = lifecycleExecutionFactory.getExecution(engine);
                notNull(execution, "Could not load execution rule engine via %s of '%s'", engine.name(),
                        evaluation.getClientId());
                resp.withData(execution.execute(evaluation, scenes));

                // Buried-point: success evaluation.
                meterService
                        .counter(evaluation_success.getName(), evaluation_success.getHelp(), MetricsTag.CLIENT_ID,
                                evaluation.getClientId(), MetricsTag.SCENESCODE, evaluation.getScenesCode(), MetricsTag.ENGINE,
                                engine.name())
                        .increment();
            } catch (Exception e) {
                String errmsg = format("Could not to execution evaluate of clientId: '%s', engine: '%s'. reason: %s",
                        evaluation.getClientId(), engine.name(), e.getMessage());
                log.error(errmsg, e);

                // Buried-point: failed evaluation.
                meterService
                        .counter(evaluation_failure.getName(), evaluation_failure.getHelp(), MetricsTag.CLIENT_ID,
                                evaluation.getClientId(), MetricsTag.SCENESCODE, evaluation.getScenesCode(), MetricsTag.ENGINE,
                                engine.name())
                        .increment();
                resp.withCode(RetCode.SYS_ERR).withMessage(errmsg);
            }

            return resp;
        });
    }

    /**
     * Load query collection dependencies:
     * 
     * <pre>
     * sceneses —one-to-one(workflowId)—>  workflow —one-to-many(ruleIds)—> rules —one-to-many(uploadIds)—> uploads
     * </pre>
     * 
     * Equivalent bson query codes example:
     * 
     * <pre>
     *    // 在线 mongodb 查询模拟器: https://mongoplayground.net/p/bPKYXCJwXdl
     *    db.getCollection('sceneses').aggregate([
     *        // 首先过滤 scenes (rengine-evaluator 接收 biz-app 请求每次只能处理一个)
     *        { $match: { "scenesCode": "iot_generic_temp_warning" } },
     *        { $lookup: {
     *             from: "workflows", // 关联 workflows 表
     *             let: { workflowId: "$workflowId" }, // 定义外键关联变量
     *             pipeline: [
     *                  { $match: { $expr: { $eq: [ "$_id",  "$$workflowId" ] } } }, // 外键等于关联
     *                  //{ $unwind: { path: "$ruleIds", preserveNullAndEmptyArrays: true } }, // 不应该用平铺,否则会按左连接乘积的平面结构输出(期望是:深度结构)
     *                  { $lookup: {
     *                         from: "rules", // 继续关联 rules 表
     *                         let: { ruleIds: "$ruleIds" }, // 定义外键关联变量
     *                         pipeline: [
     *                              { $match: { $expr: { $in: [ "$_id",  "$$ruleIds" ] } } }, // 由于父级未使用 UNWIND 因此这里使用 IN 外键关联
     *                              //{ $unwind: { path: "$uploadIds", preserveNullAndEmptyArrays: true } }, // 不应该用平铺,否则会按左连接乘积的平面结构输出(期望是:深度结构)
     *                              { $lookup: {
     *                                       from: "uploads", // 继续关联 uploads 表
     *                                       let: { uploadIds: "$uploadIds" }, // 定义外键关联变量
     *                                       pipeline: [
     *                                              { $match: { $expr: { $in: [ "$_id",  "$$uploadIds" ] } } }, //由于父级未使用 UNWIND 因此这里使用 IN 外键关联
     *                                              { $project: { "_class": 0, "delFlag": 0 } } // 控制 uploads 集返回列(投射)
     *                                       ],
     *                                       as: "uploads"
     *                                  }
     *                              },
     *                              { $project: { "_class": 0, "delFlag": 0 } } // 控制 rules 集返回列(投射)
     *                         ],
     *                         as: "rules"
     *                    }
     *                 },
     *                 { $project: { "_class": 0, "delFlag": 0 } } // 控制 workflow 集返回列(投射)
     *             ],
     *             as: "workflow"
     *           }
     *        },
     *        { $project: { "_class": 0, "delFlag": 0 } } // 控制 sceneses 集返回列(投射)
     *    ])
     * </pre>
     * 
     * @see https://www.notion.so/scenesworkflow-rules-uploads-f8e5a6f14fb64f858479b6565fb52142
     * @see https://www.mongodb.com/docs/v4.2/tutorial/model-embedded-one-to-many-relationships-between-documents/
     */
    @Override
    public Scenes loadScenesWithCascade(@NotBlank String scenesCode) {
        MongoCollection<Document> collection = mongoRepository.getCollection(SCENESES);

        // Common exclude projection.
        Bson project = Aggregates.project(Projections.fields(Projections.exclude("_class", "delFlag")));

        List<Bson> aggregates = Lists.newArrayList();
        aggregates.add(Aggregates.match(Filters.eq("scenesCode", scenesCode)));
        aggregates
                .add(Aggregates.lookup(
                        WORKFLOWS.getName(), asList(new Variable<>("workflowId",
                                "$workflowId")),
                        asList(Aggregates.match(Filters.expr(Filters.eq("_id", "$$workflowId"))),
                                Aggregates.lookup(RULES.getName(), asList(new Variable<>("ruleIds", "$ruleIds")),
                                        // IN 匹配表达式应该直接创建Document对象? 否则:
                                        // issue1.若使用:Filters.in("$_id","$$ruleIds")=>则会生成为:{$expr:{$in:["$$ruleIds"]}}}=>报错至少2个参数
                                        // issue2.若使用:Filters.in("_id","$_id","$$ruleIds")=>则会生成为:{$expr:{"_id":{$in:["$_id","$$ruleIds"]}}}}=>查询结果集不对(rules[]重复关联了uploads)
                                        asList(Aggregates.match(Filters.expr(new Document("$in",
                                                asList(new BsonString("$_id"), new BsonString("$$ruleIds"))))), Aggregates
                                                        .lookup(UPLOADS.getName(),
                                                                asList(new Variable<>("uploadIds", "$uploadIds")), asList(
                                                                        Aggregates.match(Filters.expr(new Document("$in",
                                                                                asList(new BsonString("$_id"),
                                                                                        new BsonString("$$uploadIds"))))),
                                                                        project),
                                                                "uploads"),
                                                project),
                                        "rules"),
                                project),
                        "workflow"));
        aggregates.add(project);
        aggregates.add(Aggregates.merge("_tmp_load_scenes_with_cascade"));

        // Document scenesDoc = aggregateIt.first();
        return collection.aggregate(aggregates).batchSize(1024).map(new Function<Document, Scenes>() {
            @Override
            public Scenes apply(Document scenesDoc) {
                if (log.isDebugEnabled()) {
                    log.debug("Found scenes object by scenesCode: {} to json: {}", scenesCode, scenesDoc.toJson());
                }
                Scenes scenes = new Scenes();
                scenes.setId(scenesDoc.getLong("_id"));
                scenes.setName(scenesDoc.getString("name"));
                scenes.setScenesCode(scenesDoc.getString("scenesCode"));
                scenes.setWorkflowId(scenesDoc.getLong("workflowId"));
                scenes.setOrgCode(scenesDoc.getString("orgCode"));
                scenes.setEnable(scenesDoc.getInteger("enable"));
                scenes.setLabels(scenesDoc.getList("labels", String.class));
                scenes.setRemark(scenesDoc.getString("remark"));
                scenes.setCreateBy(scenesDoc.getLong("createBy"));
                scenes.setCreateDate(scenesDoc.getDate("createDate"));
                scenes.setUpdateBy(scenesDoc.getLong("updateBy"));
                scenes.setUpdateDate(scenesDoc.getDate("updateDate"));

                safeList(scenesDoc.getList("workflow", Document.class)).stream().findFirst().ifPresent(workflowsDoc -> {
                    scenes.setWorkflow(Workflow.builder()
                            .id(workflowsDoc.getLong("_id"))
                            .name(workflowsDoc.getString("name"))
                            .ruleIds(workflowsDoc.getList("ruleIds", Long.class))
                            .orgCode(workflowsDoc.getString("orgCode"))
                            .enable(workflowsDoc.getInteger("enable"))
                            .labels(workflowsDoc.getList("labels", String.class))
                            .remark(workflowsDoc.getString("remark"))
                            .createBy(workflowsDoc.getLong("createBy"))
                            .createDate(workflowsDoc.getDate("createDate"))
                            .updateBy(workflowsDoc.getLong("updateBy"))
                            .updateDate(workflowsDoc.getDate("updateDate"))
                            .build());
                    scenes.getWorkflow()
                            .setRules(safeList(workflowsDoc.getList("rules", Document.class)).stream().map(rulesDoc -> {
                                Rule rule = Rule.builder()
                                        .id(rulesDoc.getLong("_id"))
                                        .name(rulesDoc.getString("name"))
                                        .uploadIds(rulesDoc.getList("uploadIds", Long.class))
                                        .orgCode(rulesDoc.getString("orgCode"))
                                        .enable(rulesDoc.getInteger("enable"))
                                        .labels(rulesDoc.getList("labels", String.class))
                                        .remark(rulesDoc.getString("remark"))
                                        .createBy(rulesDoc.getLong("createBy"))
                                        .createDate(rulesDoc.getDate("createDate"))
                                        .updateBy(rulesDoc.getLong("updateBy"))
                                        .updateDate(rulesDoc.getDate("updateDate"))
                                        .build();
                                rule.setUploads(safeList(rulesDoc.getList("uploads", Document.class)).stream()
                                        .map(uploadsDoc -> UploadObject.builder()
                                                .id(uploadsDoc.getLong("_id"))
                                                .filename(uploadsDoc.getString("filename"))
                                                .uploadType(uploadsDoc.getString("uploadType"))
                                                .objectPrefix(uploadsDoc.getString("objectPrefix"))
                                                .extension(uploadsDoc.getString("extension"))
                                                .size(uploadsDoc.getLong("size"))
                                                .md5sum(uploadsDoc.getString("md5sum"))
                                                .sha1sum(uploadsDoc.getString("sha1sum"))
                                                .orgCode(uploadsDoc.getString("orgCode"))
                                                .enable(uploadsDoc.getInteger("enable"))
                                                .labels(uploadsDoc.getList("labels", String.class))
                                                .remark(uploadsDoc.getString("remark"))
                                                .createBy(uploadsDoc.getLong("createBy"))
                                                .createDate(uploadsDoc.getDate("createDate"))
                                                .updateBy(uploadsDoc.getLong("updateBy"))
                                                .updateDate(uploadsDoc.getDate("updateDate"))
                                                .build())
                                        .collect(toList()));
                                return rule;
                            }).collect(toList()));
                });
                return scenes;
            }
        }).first();
    }

}
