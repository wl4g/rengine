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
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.RuleScriptService;
import com.wl4g.rengine.service.model.RuleScriptDelete;
import com.wl4g.rengine.service.model.RuleScriptDeleteResult;
import com.wl4g.rengine.service.model.RuleScriptQuery;
import com.wl4g.rengine.service.model.RuleScriptSave;
import com.wl4g.rengine.service.model.RuleScriptSaveResult;
import com.wl4g.rengine.service.mongo.GlobalMongoSequenceService;

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
    GlobalMongoSequenceService globalMongoSequenceService;

    @Override
    public PageHolder<RuleScript> query(RuleScriptQuery model) {
        final Query query = new Query(andCriteria(baseCriteria(model), isCriteria("_id", model.getScriptId()),
                isCriteria("ruleId", model.getRuleId())));

        query.with(PageRequest.of(model.getPageNum(), model.getPageSize(), Sort.by(Direction.DESC, "updateDate")));

        final List<RuleScript> ruleScripts = mongoTemplate.find(query, RuleScript.class,
                MongoCollectionDefinition.T_RULE_SCRIPTS.getName());
        // Collections.sort(ruleScripts, (o1, o2) ->
        // (o2.getUpdateDate().getTime()
        // - o1.getUpdateDate().getTime()) > 0 ? 1 : -1);

        return new PageHolder<RuleScript>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, MongoCollectionDefinition.T_RULE_SCRIPTS.getName()))
                .withRecords(ruleScripts);
    }

    @Override
    public RuleScriptSaveResult save(RuleScriptSave model) {
        final RuleScript script = model;
        notNullOf(script, "script");
        notNullOf(script.getRuleId(), "ruleId");

        // The rule script only increments the revision and does not allow
        // modification.
        // @formatter:off
        // if (isNull(script.getId())) {
        //     script.setId(IdGenUtils.nextLong());
        //     script.preInsert();
        // } else {
        //     script.preUpdate();
        // }
        // @formatter:on

        script.setId(IdGenUtils.nextLong()); // ignore frontend model 'id'
        script.preInsert();

        // Sets the current revision number, +1 according to the previous max
        // revision.
        // @formatter:off
        //final Query query = new Query(new Criteria().orOperator(Criteria.where("ruleId").is(model.getRuleId()),
        //        Criteria.where("orgCode").is(model.getOrgCode()))).with(Sort.by(Direction.DESC, "revision")).limit(1);
        //final Long maxRevision = safeList(mongoTemplate.find(query, Long.class, MongoCollectionDefinition.T_RULE_SCRIPTS.getName()))
        //        .stream()
        //        .findFirst()
        //        .orElseThrow(() -> new IllegalStateException(
        //                format("Could not get max revision by ruleId: %s, orgCode: %s", model.getRuleId(), model.getOrgCode())));
        //script.setRevision(1 + maxRevision);
        // @formatter:on

        script.setRevision(globalMongoSequenceService.getNextSequence(GlobalMongoSequenceService.SCRIPTS_REVISION_SEQ));

        RuleScript saved = mongoTemplate.save(script, MongoCollectionDefinition.T_RULE_SCRIPTS.getName());
        return RuleScriptSaveResult.builder().id(saved.getId()).build();
    }

    @Override
    public RuleScriptDeleteResult delete(RuleScriptDelete model) {
        // 'id' is a keyword, it will be automatically converted to '_id'
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("_id").is(model.getId())),
                MongoCollectionDefinition.T_RULE_SCRIPTS.getName());
        return RuleScriptDeleteResult.builder().deletedCount(result.getDeletedCount()).build();
    }

}
