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
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.util.Objects.isNull;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.DeleteResult;
import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.Rule;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.RuleService;
import com.wl4g.rengine.service.model.RuleDelete;
import com.wl4g.rengine.service.model.RuleDeleteResult;
import com.wl4g.rengine.service.model.RuleQuery;
import com.wl4g.rengine.service.model.RuleSave;
import com.wl4g.rengine.service.model.RuleSaveResult;

/**
 * {@link RuleServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class RuleServiceImpl implements RuleService {

    private @Autowired MongoTemplate mongoTemplate;

    @Override
    public PageHolder<Rule> query(RuleQuery model) {
        final Query query = new Query(
                andCriteria(baseCriteria(model), isIdCriteria(model.getRuleId()), isCriteria("scenesId", model.getScenesId())))
                        .with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        List<Rule> rules = mongoTemplate.find(query, Rule.class, MongoCollectionDefinition.T_RULES.getName());
        // Collections.sort(rules, (o1, o2) -> (o2.getUpdateDate().getTime()
        // - o1.getUpdateDate().getTime()) > 0 ? 1 : -1);

        return new PageHolder<Rule>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.T_RULES.getName()))
                .withRecords(rules);
    }

    @Override
    public RuleSaveResult save(RuleSave model) {
        Rule rule = model;
        // @formatter:off
        //Rule rule = Rule.builder()
        //        .id(model.getId())
        //        .name(model.getName())
        //        .orgCode(model.getOrgCode())
        //        .labels(model.getLabels())
        //        .enable(model.getEnable())
        //        .remark(model.getRemark())
        //        .build();
        // @formatter:off
        notNullOf(rule, "rule");

        if (isNull(rule.getId())) {
            rule.setId(IdGenUtils.nextLong());
            rule.preInsert();
        } else {
            rule.preUpdate();
        }

        Rule saved = mongoTemplate.save(rule, MongoCollectionDefinition.T_RULES.getName());
        return RuleSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public RuleDeleteResult delete(RuleDelete model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.T_RULES.getName());
        return RuleDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
