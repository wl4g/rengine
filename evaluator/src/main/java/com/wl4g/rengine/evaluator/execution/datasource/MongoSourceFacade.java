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
package com.wl4g.rengine.evaluator.execution.datasource;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseToNode;
import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.IteratorUtils;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.common.entity.DataSourceProperties.MongoDataSourceProperties;
import com.wl4g.rengine.common.exception.ConfigRengineException;
import com.wl4g.rengine.common.util.BsonUtils2;
import com.wl4g.rengine.evaluator.execution.ExecutionConfig;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link MongoSourceFacade}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@Getter
@CustomLog
@AllArgsConstructor
public class MongoSourceFacade implements DataSourceFacade {

    final ExecutionConfig executionConfig;
    final String dataSourceName;
    final MongoClient mongoClient;

    @Override
    public void close() throws IOException {
        if (nonNull(mongoClient)) {
            log.info("Closing to mongo data source for {} ...", dataSourceName);
            mongoClient.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<JsonNode> findList(@NotBlank String tableName, @NotNull List<Map<String, Object>> bsonFilters) {
        hasTextOf(tableName, "tableName");
        notNullOf(bsonFilters, "bsonFilters");
        log.debug("Bson query params : {}", bsonFilters);

        final MongoCollection<Document> collection = getCollection(MongoCollectionDefinition.of(tableName));

        final List<Bson> aggregateQuery = safeList(bsonFilters).stream()
                .flatMap(p -> safeMap(p).entrySet()
                        .stream()
                        .map(e -> new BsonDocument(e.getKey(), BsonDocument.parse(BsonUtils2.toJson(e.getValue())))))
                .collect(toList());

        final MongoCursor<JsonNode> cursor = collection.aggregate(aggregateQuery)
                .batchSize(getExecutionConfig().maxQueryBatch())
                .map(doc -> parseToNode(doc.toJson(), null))
                .iterator();
        try {
            return IteratorUtils.toList(cursor);
        } finally {
            cursor.close();
        }
    }

    public Set<Integer> insertMany(@NotBlank String tableName, @NotNull List<Map<String, Object>> bsonEntitys) {
        hasTextOf(tableName, "tableName");
        notNullOf(bsonEntitys, "bsonEntitys");
        log.debug("Insert bson entitys: {}", bsonEntitys);

        final MongoCollection<Document> collection = getCollection(MongoCollectionDefinition.of(tableName));

        final List<Document> insertDocs = safeList(bsonEntitys).stream().map(b -> new Document(b)).collect(toList());

        // final InsertManyOptions options = new InsertManyOptions();
        final InsertManyResult result = collection.insertMany(insertDocs);

        return result.getInsertedIds().keySet();
    }

    public Long updateMany(
            @NotBlank String tableName,
            @NotNull Map<String, Object> bsonFilter,
            @NotNull List<Map<String, Object>> bsonEntitys) {
        hasTextOf(tableName, "tableName");
        notNullOf(bsonFilter, "bsonFilter");
        notNullOf(bsonEntitys, "bsonEntitys");
        log.debug("Update bson entitys: {} of filter: {}", bsonEntitys, bsonFilter);

        final MongoCollection<Document> collection = getCollection(MongoCollectionDefinition.of(tableName));

        final List<Bson> updateBsons = safeList(bsonEntitys).stream().map(b -> new Document(b)).collect(toList());

        final UpdateOptions options = new UpdateOptions().upsert(true);
        final UpdateResult result = collection.updateMany(BsonUtils2.asBson(bsonFilter), updateBsons, options);

        return result.getModifiedCount();
    }

    public Long deleteMany(@NotBlank String tableName, @NotNull Map<String, Object> bsonFilter) {
        hasTextOf(tableName, "tableName");
        notNullOf(bsonFilter, "bsonFilter");
        log.debug("Delete bson filter: {}", bsonFilter);

        final MongoCollection<Document> collection = getCollection(MongoCollectionDefinition.of(tableName));

        // final DeleteOptions options = new DeleteOptions();
        final DeleteResult result = collection.deleteMany(new Document(bsonFilter));

        return result.getDeletedCount();
    }

    MongoCollection<Document> getCollection(MongoCollectionDefinition collection) {
        return mongoClient.getDatabase(RengineConstants.DEF_MONGODB_DATABASE).getCollection(collection.getName());
    }

    @Singleton
    public static class MongoSourceFacadeBuilder implements DataSourceFacadeBuilder {

        @Override
        public DataSourceFacade newInstnace(
                final @NotNull ExecutionConfig config,
                final @NotBlank String dataSourceName,
                final @NotNull DataSourceProperties dataSourceProperties) {
            notNullOf(config, "properties");
            hasTextOf(dataSourceName, "dataSourceName");

            final String connectionString = ((MongoDataSourceProperties) dataSourceProperties).getConnectionString();
            if (isBlank(connectionString)) {
                throw new ConfigRengineException(format("No found mongo dataSource properties : %s", dataSourceProperties));
            }

            final MongoClient mongoClient = new MongoClientImpl(
                    MongoClientSettings.builder().applyConnectionString(new ConnectionString(connectionString)).build(),
                    MongoDriverInformation.builder().build());

            return new MongoSourceFacade(config, dataSourceName, mongoClient);
        }

        @Override
        public DataSourceType type() {
            return DataSourceType.MONGO;
        }

    }

}
