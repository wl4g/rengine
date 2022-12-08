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
package com.wl4g.rengine.evaluator.service.impl;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseToNode;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.IteratorUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.evaluator.execution.ExecutionConfig;
import com.wl4g.rengine.evaluator.repository.MongoRepository;
import com.wl4g.rengine.evaluator.service.MongoAggregatedService;
import com.wl4g.rengine.evaluator.util.BsonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link MongoAggregatedServiceImpl}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@Slf4j
@Singleton
public class MongoAggregatedServiceImpl implements MongoAggregatedService {

    @Inject
    ExecutionConfig config;

    @Inject
    MongoRepository mongoRepository;

    @SuppressWarnings("unchecked")
    @Override
    public List<JsonNode> findList(@NotBlank String tableName, @NotNull List<Map<String, Object>> bsonQueryParams) {
        hasTextOf(tableName, "tableName");
        notNullOf(bsonQueryParams, "bsonQueryParams");
        log.debug("Bson query params : {}", bsonQueryParams);

        final MongoCollection<Document> collection = mongoRepository.getCollection(MongoCollectionDefinition.of(tableName));

        final List<Bson> aggregateQuery = safeList(bsonQueryParams).stream()
                .flatMap(p -> safeMap(p).entrySet()
                        .stream()
                        .map(e -> new BsonDocument(e.getKey(), BsonDocument.parse(BsonUtils.toJson(e.getValue())))))
                .collect(toList());

        final MongoCursor<JsonNode> cursor = collection.aggregate(aggregateQuery)
                .batchSize(config.maxQueryBatch())
                .map(doc -> parseToNode(doc.toJson(), null))
                .iterator();
        try {
            return IteratorUtils.toList(cursor);
        } finally {
            cursor.close();
        }
    }

}
