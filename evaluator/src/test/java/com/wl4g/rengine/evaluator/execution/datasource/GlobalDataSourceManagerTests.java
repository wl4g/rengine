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

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.evaluator.execution.ExecutionConfig;
import com.wl4g.rengine.evaluator.execution.datasource.DataSourceFacade.DataSourceType;
import com.wl4g.rengine.evaluator.util.BsonUtils;
import com.wl4g.rengine.evaluator.util.TestSetupDefaults;

/**
 * {@link GlobalDataSourceManagerTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v1.0.0
 */
public class GlobalDataSourceManagerTests {

    GlobalDataSourceManager globalDataSourceManager;

    @Before
    public void setup() {
        // Manual setup/inject depends.
        ExecutionConfig config = TestSetupDefaults.createExecutionConfig();
        this.globalDataSourceManager = new GlobalDataSourceManager();
        this.globalDataSourceManager.config = config;
        this.globalDataSourceManager.builders = singletonList(new MongoSourceFacade.MongoSourceFacadeBuilder());
        this.globalDataSourceManager.init();
    }

    @Test
    public void testMongoSourceFacadeFindList() {
        final MongoSourceFacade mongoSourceFacade = globalDataSourceManager.loadDataSource(DataSourceType.MONGO, "default");

        // @formatter:off
        final String queryBson = ""
                + "{ $match: { \"eventType\": \"ecommerce_trade_gift\" } },"
                + "{ $project: { \"delFlag\": 0 } }";
        // @formatter:on
        final List<Map<String, Object>> bsonFilters = new ArrayList<>();
        bsonFilters.add(BsonUtils.asMap(BsonDocument.parse(queryBson)));

        List<JsonNode> result = mongoSourceFacade.findList(MongoCollectionDefinition.AGGREGATES.getName(), bsonFilters);
        System.out.println(toJSONString(result));
    }

}
