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
package com.wl4g.rengine.evaluator.service.impl;

import static com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition.AGGREGATES;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.wl4g.rengine.evaluator.repository.MongoRepository;
import com.wl4g.rengine.evaluator.repository.PhoenixRepository;
import com.wl4g.rengine.evaluator.service.AggregationService;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link AggregationServiceImpl}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@Slf4j
@Singleton
public class AggregationServiceImpl implements AggregationService {

    // The supported MongoDB or HBase(PHOENIX) event storage.
    @Inject
    MongoRepository mongoRepository;

    @Inject
    PhoenixRepository phoenixRepository;

    @Override
    public List<Map<String, Object>> findList(@Nullable Map<String, Object> query) {
        log.info("Query events aggregates by {}", query);
        if (isNull(query)) {
            return emptyList();
        }

        MongoCollection<Document> collection = mongoRepository.getCollection(AGGREGATES);
        Bson filter = Aggregates.match(Filters.eq("eventType", null));
        // TODO Auto-generated method stub
        collection.find(filter, null);

        return null;
    }

}
