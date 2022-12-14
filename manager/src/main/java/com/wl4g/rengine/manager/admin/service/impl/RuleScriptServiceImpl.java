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
package com.wl4g.rengine.manager.admin.service.impl;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

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
import com.wl4g.rengine.common.entity.RuleScript;
import com.wl4g.rengine.common.util.IdGenUtil;
import com.wl4g.rengine.manager.admin.model.DeleteRuleScript;
import com.wl4g.rengine.manager.admin.model.DeleteRuleScriptResult;
import com.wl4g.rengine.manager.admin.model.QueryRuleScript;
import com.wl4g.rengine.manager.admin.model.SaveRuleScript;
import com.wl4g.rengine.manager.admin.model.SaveRuleScriptResult;
import com.wl4g.rengine.manager.admin.service.RuleScriptService;
import com.wl4g.rengine.manager.mongo.GlobalMongoSequenceService;

/**
 * {@link RuleScriptScriptServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
public class RuleScriptServiceImpl implements RuleScriptService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    GlobalMongoSequenceService sequenceFacade;

    @Override
    public PageHolder<RuleScript> query(QueryRuleScript model) {
        final Query query = new Query(new Criteria().orOperator(Criteria.where("_id").is(model.getScriptId()),
                Criteria.where("ruleId").is(model.getRuleId()), Criteria.where("enable").is(model.getEnable()),
                Criteria.where("labels").in(model.getLabels()), Criteria.where("orgCode").is(model.getOrgCode())));
        query.with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        final List<RuleScript> rules = mongoTemplate.find(query, RuleScript.class,
                MongoCollectionDefinition.RULE_SCRIPTS.getName());

        return new PageHolder<RuleScript>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.RULE_SCRIPTS.getName()))
                .withRecords(rules);
    }

    @Override
    public SaveRuleScriptResult save(SaveRuleScript model) {
        final RuleScript script = model;
        notNullOf(script, "script");
        notNullOf(script.getRuleId(), "ruleId");

        // The rule script only increments the revision and does not allow
        // modification.
        // @formatter:off
        // if (isNull(script.getId())) {
        //     script.setId(IdGenUtil.nextLong());
        //     script.preInsert();
        // } else {
        //     script.preUpdate();
        // }
        // @formatter:on

        script.setId(IdGenUtil.nextLong()); // ignore frontend model 'id'
        script.preInsert();

        // Sets the current revision number, +1 according to the previous max
        // revision.
        // @formatter:off
        //final Query query = new Query(new Criteria().orOperator(Criteria.where("ruleId").is(model.getRuleId()),
        //        Criteria.where("orgCode").is(model.getOrgCode()))).with(Sort.by(Direction.DESC, "revision")).limit(1);
        //final Long maxRevision = safeList(mongoTemplate.find(query, Long.class, MongoCollectionDefinition.RULE_SCRIPTS.getName()))
        //        .stream()
        //        .findFirst()
        //        .orElseThrow(() -> new IllegalStateException(
        //                format("Could not get max revision by ruleId: %s, orgCode: %s", model.getRuleId(), model.getOrgCode())));
        //script.setRevision(1 + maxRevision);
        // @formatter:on

        script.setRevision(sequenceFacade.getNextSequence(GlobalMongoSequenceService.SCRIPTS_REVISION_SEQ));

        RuleScript saved = mongoTemplate.insert(script, MongoCollectionDefinition.RULE_SCRIPTS.getName());
        return SaveRuleScriptResult.builder().id(saved.getId()).build();
    }

    @Override
    public DeleteRuleScriptResult delete(DeleteRuleScript model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.RULE_SCRIPTS.getName());
        return DeleteRuleScriptResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
