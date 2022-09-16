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
package com.wl4g.rengine.server.admin.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.rengine.common.bean.mongo.Rule;
import com.wl4g.rengine.server.admin.model.AddRule;
import com.wl4g.rengine.server.admin.model.AddRuleResult;
import com.wl4g.rengine.server.admin.model.DeleteRule;
import com.wl4g.rengine.server.admin.model.DeleteRuleResult;
import com.wl4g.rengine.server.admin.model.QueryRule;
import com.wl4g.rengine.server.admin.model.QueryRuleResult;
import com.wl4g.rengine.server.admin.service.RuleService;
import com.wl4g.rengine.server.constants.RengineWebConstants.MongoCollectionDefinition;
import com.wl4g.rengine.server.util.IdGenUtil;

/**
 * {@link RuleServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Service
public class RuleServiceImpl implements RuleService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public QueryRuleResult query(QueryRule model) {
        // TODO use pagination

        Criteria criteria = new Criteria().orOperator(Criteria.where("name").is(model.getName()),
                Criteria.where("_id").is(model.getRuleId()), Criteria.where("labels").in(model.getLabels()));

        List<Rule> rules = mongoTemplate.find(new Query(criteria), Rule.class, MongoCollectionDefinition.RULES.getName());

        Collections.sort(rules, (o1, o2) -> safeLongToInt(o2.getUpdateDate().getTime() - o1.getUpdateDate().getTime()));

        return QueryRuleResult.builder()
                .rules(safeList(rules).stream()
                        .map(p -> Rule.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .labels(p.getLabels())
                                .enabled(p.getEnabled())
                                .remark(p.getRemark())
                                .updateBy(p.getUpdateBy())
                                .updateDate(p.getUpdateDate())
                                .build())
                        .collect(toList()))
                .build();
    }

    @Override
    public AddRuleResult save(AddRule model) {
        Rule rule = Rule.builder()
                .id(IdGenUtil.next())
                .name(model.getName())
                .labels(model.getLabels())
                .enabled(model.getEnabled())
                .remark(model.getRemark())
                .updateBy("admin")
                .updateDate(new Date())
                .build();
        Rule saved = mongoTemplate.insert(rule, MongoCollectionDefinition.RULES.getName());
        return AddRuleResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteRuleResult delete(DeleteRule model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.RULES.getName());
        return DeleteRuleResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
