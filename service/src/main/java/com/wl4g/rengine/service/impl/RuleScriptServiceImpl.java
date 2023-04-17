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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.resource.ResourceUtils2.getResourceString;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_BASE;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_INTERNAL_RULESCRIPT;
import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.RE_RULE_SCRIPTS;
import static com.wl4g.rengine.common.util.BsonAggregateFilters.RULE_SCRIPT_UPLOAD_LOOKUP_FILTERS;
import static com.wl4g.rengine.service.mongo.QueryHolder.DEFAULT_FIELD_REVISION;
import static com.wl4g.rengine.service.mongo.QueryHolder.DEFAULT_FIELD_UPDATE_DATE;
import static com.wl4g.rengine.service.mongo.QueryHolder.andCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.baseCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.descSort;
import static com.wl4g.rengine.service.mongo.QueryHolder.isCriteria;
import static com.wl4g.rengine.service.mongo.QueryHolder.isIdCriteria;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.bson.conversions.Bson;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
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
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.RuleScript;
import com.wl4g.rengine.common.entity.RuleScript.RuleScriptWrapper;
import com.wl4g.rengine.common.model.RuleScriptExecuteRequest;
import com.wl4g.rengine.common.model.RuleScriptExecuteResult;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.service.RuleScriptService;
import com.wl4g.rengine.service.model.RuleScriptDelete;
import com.wl4g.rengine.service.model.RuleScriptDeleteResult;
import com.wl4g.rengine.service.model.RuleScriptQuery;
import com.wl4g.rengine.service.model.RuleScriptSave;
import com.wl4g.rengine.service.model.RuleScriptSaveResult;
import com.wl4g.rengine.service.util.RuleScriptParser;
import com.wl4g.rengine.service.util.RuleScriptParser.ScriptASTInfo;
import com.wl4g.rengine.service.util.RuleScriptParser.ScriptInfo;
import com.wl4g.rengine.service.util.RuleScriptParser.TypeInfo;

import io.netty.handler.codec.http.HttpMethod;
import lombok.CustomLog;

/**
 * {@link RuleScriptScriptServiceImpl}
 * 
 * @author James Wong
 * @date 2022-08-29
 * @since v1.0.0
 */
@Service
@CustomLog
public class RuleScriptServiceImpl extends BasicServiceImpl implements RuleScriptService {

    @Override
    public PageHolder<RuleScript> query(RuleScriptQuery model) {
        final Query query = new Query(
                andCriteria(baseCriteria(model), isIdCriteria(model.getScriptId()), isCriteria("ruleId", model.getRuleId())));

        query.with(PageRequest.of(model.getPageNum(), model.getPageSize(),
                descSort(DEFAULT_FIELD_REVISION, DEFAULT_FIELD_UPDATE_DATE)));

        final List<RuleScript> ruleScripts = mongoTemplate.find(query, RuleScript.class, RE_RULE_SCRIPTS.getName());

        return new PageHolder<RuleScript>(model.getPageNum(), model.getPageSize())
                .withTotal(mongoTemplate.count(query, RE_RULE_SCRIPTS.getName()))
                .withRecords(ruleScripts);
    }

    @Override
    public ScriptASTInfo parse(final @NotNull RuleEngine engine, final @NotNull Long scriptId) {
        final List<Bson> aggregates = Lists.newArrayList();
        aggregates.add(Aggregates.match(Filters.in("_id", scriptId)));
        RULE_SCRIPT_UPLOAD_LOOKUP_FILTERS.stream().forEach(rs -> aggregates.add(rs.asDocument()));

        try (final var cursor = mongoTemplate.getDb()
                .getCollection(RE_RULE_SCRIPTS.getName())
                .aggregate(aggregates)
                .map(ruleScriptDoc -> RuleScriptWrapper
                        .validate(BsonEntitySerializers.fromDocument(ruleScriptDoc, RuleScriptWrapper.class)))
                .iterator();) {

            if (!cursor.hasNext()) {
                throw new IllegalArgumentException(format("Could't to load rule script for %s", scriptId));
            }

            final RuleScriptWrapper ruleScript = cursor.next();
            // Load all depends content.
            final List<ScriptInfo> depends = safeList(ruleScript.getUploads()).stream().map(upload -> {
                try {
                    final byte[] buf = minioManager.getObjectToByteArray(upload.getObjectPrefix());
                    return new ScriptInfo(upload, buf);
                } catch (Throwable ex) {
                    throw new IllegalStateException(ex);
                }
            }).collect(toList());

            // Dynamic to parse user depends scripts AST.
            final ScriptASTInfo scriptAst = RuleScriptParser.parse(scriptId, ruleScript.getRevision(), engine, depends);

            // Add system built JS SDK scripts AST.
            scriptAst.getTypes().addAll(BUILT_JSSDK_TYPE_LIST);

            return scriptAst;
        }
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

        script.preInsert();

        // Sets the current revision number, +1 according to the previous max
        // revision.
        // @formatter:off
        //final Query query = new Query(new Criteria().orOperator(Criteria.where("ruleId").is(model.getRuleId()),
        //        Criteria.where("orgCode").is(model.getOrgCode()))).with(Sort.by(Direction.DESC, "revision")).limit(1);
        //final Long maxRevision = safeList(mongoTemplate.find(query, Long.class, RE_RULE_SCRIPTS.getName()))
        //        .stream()
        //        .findFirst()
        //        .orElseThrow(() -> new IllegalStateException(
        //                format("Could not get max revision by ruleId: %s, orgCode: %s", model.getRuleId(), model.getOrgCode())));
        //script.setRevision(1 + maxRevision);
        // @formatter:on

        script.setRevision(mongoSequenceService.getNextSequence(RuleScript.class, valueOf(script.getRuleId())));

        RuleScript saved = mongoTemplate.save(script, RE_RULE_SCRIPTS.getName());
        return RuleScriptSaveResult.builder().id(saved.getId()).revision(saved.getRevision()).build();
    }

    @Override
    public RuleScriptDeleteResult delete(RuleScriptDelete model) {
        return RuleScriptDeleteResult.builder().deletedCount(doDeleteGracefully(model, RE_RULE_SCRIPTS)).build();
    }

    @Override
    public RespBase<RuleScriptExecuteResult> execute(RuleScriptExecuteRequest model) {
        notNullOf(model, "executeRequest");
        model.validate();

        log.info("Executing for {}, {}", config.getExecutorEndpoint(), model);
        final RestClient restClient = new RestClient(false, 6_000, model.getTimeout().intValue(), 65535);

        final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>(2);
        headers.add("Content-Type", "application/json");

        final RespBase<RuleScriptExecuteResult> resp = RespBase.create();
        try {
            final HttpResponseEntity<RespBase<RuleScriptExecuteResult>> result = restClient
                    .exchange(UriComponentsBuilder.fromUri(config.getExecutorEndpoint())
                            .path(API_EXECUTOR_EXECUTE_BASE)
                            .path(API_EXECUTOR_EXECUTE_INTERNAL_RULESCRIPT)
                            .build()
                            .toUri(), HttpMethod.POST, new HttpEntity<>(model, headers), RULE_EXECUTE_RESULT_TYPE);

            log.info("Executed the result : {}", resp);
            resp.withCode(result.getBody().getCode()).withData(result.getBody().getData());

        } catch (Throwable ex) {
            log.error("Failed to execute rule script.", ex);
            resp.withCode(RetCode.SYS_ERR).withMessage(ex.getMessage());
        }

        return resp;
    }

    static final ParameterizedTypeReference<RespBase<RuleScriptExecuteResult>> RULE_EXECUTE_RESULT_TYPE = new ParameterizedTypeReference<>() {
    };

    static final List<TypeInfo> BUILT_JSSDK_TYPE_LIST = parseJSON(getResourceString(null, "META-INF/executor-js-sdk-ast.json"),
            new TypeReference<List<TypeInfo>>() {
            });

}
