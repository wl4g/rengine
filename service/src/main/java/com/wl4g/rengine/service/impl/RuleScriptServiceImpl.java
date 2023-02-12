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
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_BASE;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_INTERNAL_RULESCRIPT;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;

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
import com.wl4g.infra.common.collection.multimap.LinkedMultiValueMap;
import com.wl4g.infra.common.collection.multimap.MultiValueMap;
import com.wl4g.infra.common.reflect.ParameterizedTypeReference;
import com.wl4g.infra.common.remoting.HttpEntity;
import com.wl4g.infra.common.remoting.HttpResponseEntity;
import com.wl4g.infra.common.remoting.RestClient;
import com.wl4g.infra.common.remoting.uri.UriComponentsBuilder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.RuleScript;
import com.wl4g.rengine.common.model.RuleScriptExecuteRequest;
import com.wl4g.rengine.common.model.RuleScriptExecuteResult;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.service.RuleScriptService;
import com.wl4g.rengine.service.config.RengineServiceProperties;
import com.wl4g.rengine.service.model.RuleScriptDelete;
import com.wl4g.rengine.service.model.RuleScriptDeleteResult;
import com.wl4g.rengine.service.model.RuleScriptQuery;
import com.wl4g.rengine.service.model.RuleScriptSave;
import com.wl4g.rengine.service.model.RuleScriptSaveResult;
import com.wl4g.rengine.service.mongo.GlobalMongoSequenceService;

import io.netty.handler.codec.http.HttpMethod;
import lombok.CustomLog;

/**
 * {@link RuleScriptScriptServiceImpl}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v1.0.0
 */
@Service
@CustomLog
public class RuleScriptServiceImpl implements RuleScriptService {

    @Autowired
    RengineServiceProperties config;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    GlobalMongoSequenceService mongoSequenceService;

    @Override
    public PageHolder<RuleScript> query(RuleScriptQuery model) {
        final Query query = new Query(
                andCriteria(baseCriteria(model), isIdCriteria(model.getScriptId()), isCriteria("ruleId", model.getRuleId())));

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

        script.setRevision(mongoSequenceService.getNextSequence(GlobalMongoSequenceService.SCRIPTS_REVISION_SEQ));

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

    @Override
    public RespBase<RuleScriptExecuteResult> execute(RuleScriptExecuteRequest model) {
        notNullOf(model, "executeRequest");
        model.validate();

        log.info("Executing for {}, {}", config.getExecutorEndpoint(), model);
        final RestClient restClient = new RestClient(false, 6_000, model.getTimeout().intValue(), 65535);

        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(2);
        headers.add("Content-Type", "application/json");

        RespBase<RuleScriptExecuteResult> resp = RespBase.create();
        try {
            final HttpResponseEntity<RespBase<RuleScriptExecuteResult>> result = restClient
                    .exchange(UriComponentsBuilder.fromUri(config.getExecutorEndpoint())
                            .path(API_EXECUTOR_EXECUTE_BASE)
                            .path(API_EXECUTOR_EXECUTE_INTERNAL_RULESCRIPT)
                            .build()
                            .toUri(), HttpMethod.POST, new HttpEntity<>(model, headers), RULE_EXECUTE_RESULT_TYPE);

            log.info("Executed the result : {}", resp);
            resp.withCode(RetCode.OK).withData(result.getBody().getData());

        } catch (Throwable ex) {
            log.error("Failed to execute rule script.", ex);
            resp.withCode(RetCode.SYS_ERR).withMessage(ex.getMessage());
        }

        return resp;
    }

    static final ParameterizedTypeReference<RespBase<RuleScriptExecuteResult>> RULE_EXECUTE_RESULT_TYPE = new ParameterizedTypeReference<>() {
    };

}
