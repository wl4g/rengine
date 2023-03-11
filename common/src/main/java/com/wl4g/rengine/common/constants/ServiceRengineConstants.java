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
package com.wl4g.rengine.common.constants;

import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_MENUS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_MENU_ROLES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_ORANIZATIONS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_ROLES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_USER_ROLES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.T_RULES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.T_RULE_SCRIPTS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.T_UPLOADS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.T_WORKFLOWS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.T_WORKFLOW_GRAPHS;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.wl4g.infra.common.bean.BaseBean;

/**
 * {@link ServiceRengineConstants}
 * 
 * @author James Wong
 * @version 2023-02-20
 * @since v1.0.0
 */
public abstract class ServiceRengineConstants extends RengineConstants {

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
                    + "["
                    //+ "{ $match: { $expr: { $eq: [ \"$_id\",  \"$$rule_id\" ] } } },"
                    + "{ $match: { \"enable\": { $eq: 1 } } },"
                    + "{ $match: { \"delFlag\": { $eq: 0 } } },"
                    + "{ $project: { \"_class\": 0, \"delFlag\": 0 } },"
                    + "{ $sort: { \"revision\": -1 } },  "
                    + "{ $limit: 1 },"
                    + "{ $lookup: {"
                    + "    from: \"" + T_UPLOADS.getName() + "\",  "
                    + "    let: { upload_ids: { $map: { input: \"$uploadIds\", in: { $toLong: \"$$this\"} } } },"
                    + "    pipeline: ["
                    + "        { $match: { $expr: { $in: [ \"$_id\",  \"$$upload_ids\" ] } } }, "
                    + "        { $match: { \"enable\": { $eq: 1 } } },"
                    + "        { $match: { \"delFlag\": { $eq: 0 } } },"
                    + "        { $project: { \"_class\": 0, \"delFlag\": 0 } }"
                    + "    ],"
                    + "    as: \"uploads\""
                    + "    }"
                    + "}"
                    + "]");
    // @formatter:on

    // Workflow lookup filter.
    // @formatter:off
    public static final Bson WORKFLOW_LOOKUP_FILTER = BsonDocument.parse(""
            + "{ $lookup: {"
            + "    from: \"" + T_WORKFLOWS.getName() + "\",  "
            + "    let: { scenes_id: { $toLong: \"$_id\" } },  "
            + "    pipeline: ["
            + "        { $match: { $expr: { $eq: [ \"$scenesId\", \"$$scenes_id\" ] } } }, "
            + "        { $match: { \"enable\": { $eq: 1 } } },"
            + "        { $match: { \"delFlag\": { $eq: 0 } } },"
            + "        { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "        { $lookup: {"
            + "            from: \"" + T_WORKFLOW_GRAPHS.getName() + "\", "
            + "            let: { workflow_id: { $toLong: \"$_id\" } },"
            + "            pipeline: ["
            + "                { $match: { $expr: { $eq: [ \"$workflowId\", \"$$workflow_id\" ] } } },"
            + "                { $match: { \"enable\": { $eq: 1 } } },"
            + "                { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "                { $sort: { \"revision\": -1 } }, "
            + "                { $limit: 1 },"
            + "                { $lookup: {"
            + "                    from: \"" + T_RULES.getName() + "\","
            + "                    let: { rule_ids: { $map: { input: \"$nodes\", in: { $toLong: \"$$this.ruleId\" } } } },"
            + "                    pipeline: ["
            + "                        { $match: { $expr: { $in: [ \"$_id\",  \"$$rule_ids\" ] } } },"
            + "                        { $match: { \"enable\": { $eq: 1 } } },"
            + "                        { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                        { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "                        { $lookup: {"
            + "                            from: \"" + T_RULE_SCRIPTS.getName() + "\","
            + "                            let: { rule_id: { $toLong: \"$_id\" } },"
            + "                            pipeline: ["
            + "                                { $match: { $expr: { $eq: [ \"$ruleId\",  \"$$rule_id\" ] } } },"
            + "                                { $match: { \"enable\": { $eq: 1 } } },"
            + "                                { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                                { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "                                { $sort: { \"revision\": -1 } },  "
            + "                                { $limit: 1 },"
            + "                                { $lookup: {"
            + "                                    from: \"" + T_UPLOADS.getName() + "\",  "
            + "                                    let: { upload_ids: { $map: { input: \"$uploadIds\", in: { $toLong: \"$$this\"} } } },"
            + "                                    pipeline: ["
            + "                                        { $match: { $expr: { $in: [ \"$_id\",  \"$$upload_ids\" ] } } }, "
            + "                                        { $match: { \"enable\": { $eq: 1 } } },"
            + "                                        { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                                        { $project: { \"_class\": 0, \"delFlag\": 0 } }"
            + "                                    ],"
            + "                                    as: \"uploads\""
            + "                                    }"
            + "                                }"
            + "                            ],"
            + "                            as: \"scripts\""
            + "                            }"
            + "                        }"
            + "                    ],"
            + "                    as: \"rules\""
            + "                    }"
            + "                }"
            + "            ],"
            + "            as: \"graphs\"  "
            + "            }"
            + "        }"
            + "    ],"
            + "    as: \"workflows\""
            + "    }"
            + "}");
    // @formatter:on

    // User role organization menus lookup filter(deep cascade).
    // @formatter:off
    public static final BsonArray USER_ROLE_ORGAN_MENUS_LOOKUP_FILTER = BsonArray.parse("["
            // + "    { $match: { \"username\": { $in: [\"root\"] } } },"
            + "    { $match: { \"enable\": { $eq: 1 } } },"
            + "    { $match: { \"delFlag\": { $eq: 0 } } },"
            + "    { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "    { $lookup: {"
            + "        from: \"" + SYS_USER_ROLES.getName() + "\","
            + "        let: { user_id: { $toLong: \"$_id\" } },"
            + "        pipeline: ["
            + "            { $match: { $expr: { $eq: [ \"$userId\", \"$$user_id\" ] } } }, "
            + "            { $lookup: {"
            + "                from: \"" + SYS_ROLES.getName() + "\", "
            + "                let: { role_id: { $toLong: \"$roleId\" } },"
            + "                pipeline: ["
            + "                    { $match: { $expr: { $eq: [ \"$_id\", \"$$role_id\" ] } } },"
            + "                    { $match: { \"enable\": { $eq: 1 } } },"
            + "                    { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                    { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "                    { $lookup: {"
            + "                        from: \"" + SYS_MENU_ROLES.getName() + "\","
            + "                        pipeline: ["
            + "                            { $match: { $expr: { $eq: [ \"$roleId\", \"$$role_id\" ] } } },"
            + "                            { $lookup: {"
            + "                                from: \"" + SYS_MENUS.getName() + "\","
            + "                                let: { menu_id: { $toLong: \"$menuId\" } },"
            + "                                pipeline: ["
            + "                                    { $match: { $expr: { $eq: [ \"$_id\",  \"$$menu_id\" ] } } },"
            + "                                    { $match: { \"enable\": { $eq: 1 } } },"
            + "                                    { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                                    { $project: { \"_class\": 0, \"delFlag\": 0 } }"
            + "                                ],"
            + "                                as: \"menus\""
            + "                                }"
            + "                            }"
            + "                        ],"
            + "                        as: \"menuRoles\""
            + "                        }"
            + "                    },"
            + "                    {"
            + "                        $lookup: {"
            + "                            from: \"" + SYS_ORANIZATIONS.getName() + "\","
            + "                            localField: \"orgCode\","
            + "                            foreignField: \"orgCode\","
            + "                            as: \"org\""
            + "                        }"
            + "                    },"
            + "                    {"
            + "                        $unwind: {"
            + "                            path: \"$org\","
            + "                            preserveNullAndEmptyArrays: true"
            + "                        }"
            + "                    }"
            + "                ],"
            + "                as: \"roles\""
            + "                }"
            + "            },"
            + "        ],"
            + "        as: \"userRoles\""
            + "        }"
            + "    }"
            + "]");
    // @formatter:on

}
