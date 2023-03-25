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
package com.wl4g.rengine.common.util;

import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_MENUS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_MENU_ROLES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_ROLES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_USERS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_USER_ROLES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_RULES;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_RULE_SCRIPTS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.SYS_UPLOADS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_WORKFLOWS;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_WORKFLOW_GRAPHS;

import org.bson.BsonArray;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.wl4g.infra.common.bean.BaseBean;

/**
 * {@link BsonAggregateFilters}
 * 
 * @author James Wong
 * @version 2023-02-20
 * @since v1.0.0
 */
public abstract class BsonAggregateFilters {

    // Basic filters.
    public static final Bson DEFAULT_ENABLE_FILTER = Aggregates.match(Filters.eq("enable", BaseBean.ENABLED));
    public static final Bson DEFAULT_DELFLAT_FILTER = Aggregates.match(Filters.eq("delFlag", BaseBean.DEL_FLAG_NORMAL));
    public static final Bson DEFAULT_PROJECT_FILTER = Aggregates
            .project(Projections.fields(Projections.exclude("_class", "delFlag")));
    public static final Bson DEFAULT_SORT = Aggregates.sort(new Document("revision", -1));
    public static final Bson DEFAULT_LIMIT = Aggregates.limit(1);

    // Lookup for rulescript(latest),uploads by rule filters.
    // @formatter:off
    public static final BsonArray RULE_SCRIPT_UPLOAD_LOOKUP_FILTERS = BsonArray.parse("["
                    //+ "{ $match: { $expr: { $eq: [ \"$_id\",  \"$$rule_id\" ] } } },"
                    + "{ $match: { \"enable\": { $eq: 1 } } },"
                    + "{ $match: { \"delFlag\": { $eq: 0 } } },"
                    + "{ $project: { \"_class\": 0, \"delFlag\": 0 } },"
                    + "{ $sort: { \"revision\": -1 } },  "
                    + "{ $limit: 1 },"
                    + "{ $lookup: {"
                    + "    from: \"" + SYS_UPLOADS.getName() + "\",  "
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

    // Lookup for workflow,workflowgraph(latest),rule,rulescript,uploads by
    // scenes filters.
    // @formatter:off
    public static final BsonArray WORKFLOW_GRAPH_RULE_SCRIPT_UPLOAD_LOOKUP_FILTERS = BsonArray.parse("["
            //+ "{ $match: { $expr: { $in: [ \"$scenesCode\",  \"test_script_sdk_example\" ] } } },"
            + "{ $match: { \"enable\": { $eq: 1 } } },"
            + "{ $match: { \"delFlag\": { $eq: 0 } } },"
            + "{ $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "{ $lookup: {"
            + "    from: \"" + RE_WORKFLOWS.getName() + "\",  "
            + "    let: { scenes_id: { $toLong: \"$_id\" } },  "
            + "    pipeline: ["
            + "        { $match: { $expr: { $eq: [ \"$scenesId\", \"$$scenes_id\" ] } } }, "
            + "        { $match: { \"enable\": { $eq: 1 } } },"
            + "        { $match: { \"delFlag\": { $eq: 0 } } },"
            + "        { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "        { $lookup: {"
            + "            from: \"" + RE_WORKFLOW_GRAPHS.getName() + "\", "
            + "            let: { workflow_id: { $toLong: \"$_id\" } },"
            + "            pipeline: ["
            + "                { $match: { $expr: { $eq: [ \"$workflowId\", \"$$workflow_id\" ] } } },"
            + "                { $match: { \"enable\": { $eq: 1 } } },"
            + "                { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "                { $sort: { \"revision\": -1 } }, "
            + "                { $limit: 1 },"
            + "                { $lookup: {"
            + "                    from: \"" + RE_RULES.getName() + "\","
            + "                    let: { rule_ids: { $map: { input: \"$details.nodes\", in: { $toLong: \"$$this.ruleId\" } } } },"
            + "                    pipeline: ["
            + "                        { $match: { $expr: { $in: [ \"$_id\",  \"$$rule_ids\" ] } } },"
            + "                        { $match: { \"enable\": { $eq: 1 } } },"
            + "                        { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                        { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "                        { $lookup: {"
            + "                            from: \"" + RE_RULE_SCRIPTS.getName() + "\","
            + "                            let: { rule_id: { $toLong: \"$_id\" } },"
            + "                            pipeline: ["
            + "                                { $match: { $expr: { $eq: [ \"$ruleId\",  \"$$rule_id\" ] } } },"
            + "                                { $match: { \"enable\": { $eq: 1 } } },"
            + "                                { $match: { \"delFlag\": { $eq: 0 } } },"
            + "                                { $project: { \"_class\": 0, \"delFlag\": 0 } },"
            + "                                { $sort: { \"revision\": -1 } },  "
            + "                                { $limit: 1 },"
            + "                                { $lookup: {"
            + "                                    from: \"" + SYS_UPLOADS.getName() + "\",  "
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
            + "}"
            + "]");
    // @formatter:on

    // Lookup for users by role filters.
    // 连接查询根据 roleCode 查询 role 下的 users.
    // @formatter:off
    public static final BsonArray ROLE_USER_LOOKUP_FILTERS = BsonArray.parse("["
            //+ "{ $match: { \"roleCode\": { $in: [\"r:admin\"] } } },"
            + "{ $match: { \"enable\": { $eq: " + BaseBean.ENABLED + " } } },"
            + "{ $match: { \"delFlag\": { $eq: " + BaseBean.DEL_FLAG_NORMAL + " } } },"
            // like e.g: left join sys_user_roles on u._id=ur.userId
            + "{ $lookup: {"
            + "    from: \"" + SYS_USER_ROLES.getName() + "\","
            + "    localField: \"_id\","
            + "    foreignField: \"roleId\","
            + "    as: \"userRoles\""
            + "  }"
            + "},"
            + "{ $unwind: \"$userRoles\" },"
            + "{ $group: { _id: \"$userRoles.userId\" } },"
            + "  { $lookup: {"
            + "          from: \"" + SYS_USERS.getName() + "\","
            + "          localField: \"_id\","
            + "          foreignField: \"_id\","
            + "          as: \"users\""
            + "      }"
            + "  },"
            + "{ $match: { \"users.enable\": { $eq: " + BaseBean.ENABLED + " } } },"
            + "{ $match: { \"users.delFlag\": { $eq: " + BaseBean.DEL_FLAG_NORMAL + " } } },"
            // 将子节点menus提取合并到上一层:https://www.mongodb.com/docs/manual/reference/operator/aggregation/mergeObjects/#-mergeobjects
            + "{ $replaceRoot: { newRoot: { $mergeObjects: [ { $arrayElemAt: [ \"$users\", 0 ] }, \"$$ROOT\" ] } } },"
            // 隐藏(不返回)子节点menus
            + "  { $project: { users: 0, delFlag: 0 } }"
            + "]");
    // @formatter:on

    // Lookup for menus by role filters.
    // 连接查询根据 roleCode 查询 role 下的 menus.
    // @formatter:off
    public static final BsonArray ROLE_MENU_LOOKUP_FILTERS = BsonArray.parse("["
            //+ "{ $match: { \"roleCode\": { $in: [\"r:admin\"] } } },"
            + "{ $match: { \"enable\": { $eq: " + BaseBean.ENABLED + " } } },"
            + "{ $match: { \"delFlag\": { $eq: " + BaseBean.DEL_FLAG_NORMAL + " } } },"
            // like e.g: left join sys_user_roles on u._id=ur.userId
            + "{ $lookup: {"
            + "    from: \"" + SYS_MENU_ROLES.getName() + "\","
            + "    localField: \"_id\","
            + "    foreignField: \"roleId\","
            + "    as: \"userRoles\""
            + "  }"
            + "},"
            + "{ $unwind: \"$userRoles\" },"
            + "{ $group: { _id: \"$userRoles.menuId\" } },"
            + "  { $lookup: {"
            + "          from: \"" + SYS_MENUS.getName() + "\","
            + "          localField: \"_id\","
            + "          foreignField: \"_id\","
            + "          as: \"menus\""
            + "      }"
            + "  },"
            + "{ $match: { \"menus.enable\": { $eq: " + BaseBean.ENABLED + " } } },"
            + "{ $match: { \"menus.delFlag\": { $eq: " + BaseBean.DEL_FLAG_NORMAL + " } } },"
            // 将子节点menus提取合并到上一层:https://www.mongodb.com/docs/manual/reference/operator/aggregation/mergeObjects/#-mergeobjects
            + "{ $replaceRoot: { newRoot: { $mergeObjects: [ { $arrayElemAt: [ \"$menus\", 0 ] }, \"$$ROOT\" ] } } },"
            // 隐藏(不返回)子节点menus
            + "  { $project: { menus: 0, delFlag: 0 } }"
            + "]");
    // @formatter:on

    // Lookup for menus by user filters.
    /**
     * 连接查询根据 username 查询 user 下的 roles 下的 menus. </br>
     * </br>
     * 优点: 交叉平面, 按需返回, 多层级结构不清晰, 适合只需提取子子节点的数据. (如:查询 user 下 roles 下所有去重的 menus,
     * 无需返回其他中间关联信息 user_roles,menu_roles 等) </br>
     * </br>
     * 将子节点 menus 提取合并到上一层:
     * https://www.mongodb.com/docs/manual/reference/operator/aggregation/mergeObjects/#-mergeobjects
     */
    // @formatter:off
    public static final BsonArray USER_MENU_LOOKUP_FILTERS = BsonArray.parse("["
            //+ "{ $match: { \"username\": { $in: [\"root\"] } } },"
            + "{ $match: { \"enable\": { $eq: " + BaseBean.ENABLED + " } } },"
            + "{ $match: { \"delFlag\": { $eq: " + BaseBean.DEL_FLAG_NORMAL + " } } },"
            + "{ $project: { \"_class\": 0, \"delFlag\": 0 } },"
            // like e.g: left join sys_user_roles on u._id=ur.userId
            + "{ $lookup: {"
            + "    from: \"" + SYS_USER_ROLES.getName() + "\","
            + "    localField: \"_id\","
            + "    foreignField: \"userId\","
            + "    as: \"userRoles\""
            + "  }"
            + "},"
            + "{ $unwind: \"$userRoles\" },"
            + "{ $group: { _id: \"$userRoles.roleId\" } },"
            // like e.g: left join sys_menu_roles on ur.roleId=mr.roleId
            + "  { $lookup: {"
            + "          from: \"" + SYS_MENU_ROLES.getName() + "\","
            + "          localField: \"_id\","
            + "          foreignField: \"roleId\","
            + "          as: \"menuRoles\""
            + "      }"
            + "  },"
            + "{ $unwind: \"$menuRoles\" },"
            + "{ $group: { _id: \"$menuRoles.menuId\" } },"
            // like e.g: left join sys_menus on mr.menuId=m._id
            + "  { $lookup: {"
            + "          from: \"" + SYS_MENUS.getName() + "\","
            + "          localField: \"_id\","
            + "          foreignField: \"_id\","
            + "          as: \"menus\""
            + "      }"
            + "  },"
            + "{ $match: { \"menus.enable\": { $eq: " + BaseBean.ENABLED + " } } },"
            + "{ $match: { \"menus.delFlag\": { $eq: " + BaseBean.DEL_FLAG_NORMAL + " } } },"
            // 将子节点menus提取合并到上一层:https://www.mongodb.com/docs/manual/reference/operator/aggregation/mergeObjects/#-mergeobjects
            + "  { $replaceRoot: { newRoot: { $mergeObjects: [ { $arrayElemAt: [ \"$menus\", 0 ] }, \"$$ROOT\" ] } } },"
            // 隐藏(不返回)子节点menus
            + "    { $project: { menus: 0, delFlag: 0 } }"
            + "]");
    // @formatter:on

    // Lookup for roles by user filters.
    /**
     * 连接查询根据 username 查询 user 下的 roles. </br>
     * </br>
     * 优点: 交叉平面, 按需返回, 多层级结构不清晰, 适合只需提取子子节点的数据. (如:查询 user 下所有去重的 roles,
     * 无需返回其他中间关联信息 user_roles 等) </br>
     * </br>
     * 将子节点 roles 提取合并到上一层:
     * https://www.mongodb.com/docs/manual/reference/operator/aggregation/mergeObjects/#-mergeobjects
     */
    // @formatter:off
    public static final BsonArray USER_ROLE_LOOKUP_FILTERS = BsonArray.parse("["
            //+ "{ $match: { \"username\": { $in: [\"root\"] } } },"
            + "{ $match: { \"enable\": { $eq: " + BaseBean.ENABLED + " } } },"
            + "{ $match: { \"delFlag\": { $eq: " + BaseBean.DEL_FLAG_NORMAL + " } } },"
            // like e.g: left join sys_user_roles on u._id=ur.userId
            + "{ $lookup: {"
            + "    from: \"" + SYS_USER_ROLES.getName() + "\","
            + "    localField: \"_id\","
            + "    foreignField: \"userId\","
            + "    as: \"userRoles\""
            + "  }"
            + "},"
            + "{ $unwind: \"$userRoles\" },"
            + "{ $group: { _id: \"$userRoles.roleId\" } },"
            + "  { $lookup: {"
            + "          from: \"" + SYS_ROLES.getName() + "\","
            + "          localField: \"_id\","
            + "          foreignField: \"_id\","
            + "          as: \"roles\""
            + "      }"
            + "  },"
            + "{ $match: { \"roles.enable\": { $eq: " + BaseBean.ENABLED + " } } },"
            + "{ $match: { \"roles.delFlag\": { $eq: " + BaseBean.DEL_FLAG_NORMAL + " } } },"
            // 将子节点menus提取合并到上一层:https://www.mongodb.com/docs/manual/reference/operator/aggregation/mergeObjects/#-mergeobjects
            + "{ $replaceRoot: { newRoot: { $mergeObjects: [ { $arrayElemAt: [ \"$roles\", 0 ] }, \"$$ROOT\" ] } } },"
            // 隐藏(不返回)子节点menus
            + "  { $project: { roles: 0, delFlag: 0 } }"
            + "]");
    // @formatter:on

    // Lookup for role,menus by user filters.
    /**
     * 深度嵌套子查询根据 username 查询 user 下的 roles 以及 menus. </br>
     * </br>
     * 优点: 深度嵌套, 数据完整, 多层级结构清晰, 适合查询关联的完整数据. (如:查询 user 下 roles 以及下的 menus,
     * 也需要返回其他中间关联信息 user_roles,menu_roles 等)</br>
     */
    // @formatter:off
    public static final BsonArray USER_ROLE_MENU_LOOKUP_FILTERS = BsonArray.parse("["
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
