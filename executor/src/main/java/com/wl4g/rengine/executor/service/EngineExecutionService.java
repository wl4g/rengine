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
package com.wl4g.rengine.executor.service;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.RuleScript.RuleScriptWrapper;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;
import com.wl4g.rengine.common.model.WorkflowExecuteResult.ResultDescription;
import com.wl4g.rengine.common.model.RuleExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;

import io.smallrye.mutiny.Uni;

/**
 * {@link EngineExecutionService}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 */
public interface EngineExecutionService {

    Uni<RespBase<ResultDescription>> execute(final @NotNull RuleExecuteRequest model);

    Uni<RespBase<WorkflowExecuteResult>> execute(final @NotNull WorkflowExecuteRequest model);

    Uni<List<RuleScriptWrapper>> findRuleScripts(@NotEmpty List<Long> ruleScriptIds);

    Uni<List<ScenesWrapper>> findScenesWorkflowGraphRules(@NotEmpty List<String> scenesCodes, @Min(1) @Max(1024) int revisions);

    //
    // --- Constants Definitions. ---
    //

    public static final TypeReference<List<ScenesWrapper>> SCENES_TYPE_REF = new TypeReference<List<ScenesWrapper>>() {
    };

    // Basic filters.
    public static final Bson DEFAULT_ENABLE_FILTER = Aggregates.match(Filters.eq("enable", BaseBean.ENABLED));
    public static final Bson DEFAULT_DELFLAT_FILTER = Aggregates.match(Filters.eq("delFlag", BaseBean.DEL_FLAG_NORMAL));
    public static final Bson DEFAULT_PROJECT_FILTER = Aggregates
            .project(Projections.fields(Projections.exclude("_class", "delFlag")));
    public static final Bson DEFAULT_SORT = Aggregates.sort(new Document("revision", -1));
    public static final Bson DEFAULT_LIMIT = Aggregates.limit(1);

    // Rule script lookup filter (Unit Run).
    // @formatter:off
    public static final BsonArray RULE_SCRIPT_LOOKUP_FILTER_WITH_UNIT_RUN = BsonArray.parse(""
                    + "[\n"
                    //+ "{ $match: { $expr: { $eq: [ \"$_id\",  \"$$rule_id\" ] } } },\n"
                    + "{ $match: { \"enable\": { $eq: 1 } } },\n"
                    + "{ $match: { \"delFlag\": { $eq: 0 } } },\n"
                    + "{ $project: { \"_class\": 0, \"delFlag\": 0 } },\n"
                    + "{ $sort: { \"revision\": -1 } },  \n"
                    + "{ $limit: 1 },\n"
                    + "{ $lookup: {\n"
                    + "    from: \"t_uploads\",  \n"
                    + "    let: { upload_ids: { $map: { input: \"$uploadIds\", in: { $toLong: \"$$this\"} } } },\n"
                    + "    pipeline: [\n"
                    + "        { $match: { $expr: { $in: [ \"$_id\",  \"$$upload_ids\" ] } } }, \n"
                    + "        { $match: { \"enable\": { $eq: 1 } } },\n"
                    + "        { $match: { \"delFlag\": { $eq: 0 } } },\n"
                    + "        { $project: { \"_class\": 0, \"delFlag\": 0 } }\n"
                    + "    ],\n"
                    + "    as: \"uploads\"\n"
                    + "    }\n"
                    + "}\n"
                    + "]");
    // @formatter:on

    // Workflow lookup filter.
    // @formatter:off
    public static final Bson WORKFLOW_LOOKUP_FILTER = BsonDocument.parse(""
            + "{ $lookup: {\n"
            + "    from: \"t_workflows\",  \n"
            + "    let: { scenes_id: { $toLong: \"$_id\" } },  \n"
            + "    pipeline: [\n"
            + "        { $match: { $expr: { $eq: [ \"$scenesId\", \"$$scenes_id\" ] } } }, \n"
            + "        { $match: { \"enable\": { $eq: 1 } } },\n"
            + "        { $match: { \"delFlag\": { $eq: 0 } } },\n"
            + "        { $project: { \"_class\": 0, \"delFlag\": 0 } },\n"
            + "        { $lookup: {\n"
            + "            from: \"t_workflow_graphs\", \n"
            + "            let: { workflow_id: { $toLong: \"$_id\" } },\n"
            + "            pipeline: [\n"
            + "                { $match: { $expr: { $eq: [ \"$workflowId\", \"$$workflow_id\" ] } } },\n"
            + "                { $match: { \"enable\": { $eq: 1 } } },\n"
            + "                { $match: { \"delFlag\": { $eq: 0 } } },\n"
            + "                { $project: { \"_class\": 0, \"delFlag\": 0 } },\n"
            + "                { $sort: { \"revision\": -1 } }, \n"
            + "                { $limit: 1 },\n"
            + "                { $lookup: {\n"
            + "                    from: \"t_rules\",\n"
            + "                    let: { rule_ids: { $map: { input: \"$nodes\", in: { $toLong: \"$$this.ruleId\" } } } },\n"
            + "                    pipeline: [\n"
            + "                        { $match: { $expr: { $in: [ \"$_id\",  \"$$rule_ids\" ] } } },\n"
            + "                        { $match: { \"enable\": { $eq: 1 } } },\n"
            + "                        { $match: { \"delFlag\": { $eq: 0 } } },\n"
            + "                        { $project: { \"_class\": 0, \"delFlag\": 0 } },\n"
            + "                        { $lookup: {\n"
            + "                            from: \"t_rule_scripts\",\n"
            + "                            let: { rule_id: { $toLong: \"$_id\" } },\n"
            + "                            pipeline: [\n"
            + "                                { $match: { $expr: { $eq: [ \"$ruleId\",  \"$$rule_id\" ] } } },\n"
            + "                                { $match: { \"enable\": { $eq: 1 } } },\n"
            + "                                { $match: { \"delFlag\": { $eq: 0 } } },\n"
            + "                                { $project: { \"_class\": 0, \"delFlag\": 0 } },\n"
            + "                                { $sort: { \"revision\": -1 } },  \n"
            + "                                { $limit: 1 },\n"
            + "                                { $lookup: {\n"
            + "                                    from: \"t_uploads\",  \n"
            + "                                    let: { upload_ids: { $map: { input: \"$uploadIds\", in: { $toLong: \"$$this\"} } } },\n"
            + "                                    pipeline: [\n"
            + "                                        { $match: { $expr: { $in: [ \"$_id\",  \"$$upload_ids\" ] } } }, \n"
            + "                                        { $match: { \"enable\": { $eq: 1 } } },\n"
            + "                                        { $match: { \"delFlag\": { $eq: 0 } } },\n"
            + "                                        { $project: { \"_class\": 0, \"delFlag\": 0 } }\n"
            + "                                    ],\n"
            + "                                    as: \"uploads\"\n"
            + "                                    }\n"
            + "                                }\n"
            + "                            ],\n"
            + "                            as: \"scripts\"\n"
            + "                            }\n"
            + "                        }\n"
            + "                    ],\n"
            + "                    as: \"rules\"\n"
            + "                    }\n"
            + "                }\n"
            + "            ],\n"
            + "            as: \"graphs\"  \n"
            + "            }\n"
            + "        }\n"
            + "    ],\n"
            + "    as: \"workflows\"\n"
            + "    }\n"
            + "}");
    // @formatter:on

}
