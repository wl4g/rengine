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
import static java.util.Objects.isNull;
//import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.testcontainers.containers.MongoDBContainer;
//import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.JsonNode;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.common.util.BsonUtils2;
import com.wl4g.rengine.evaluator.util.TestSetupDefaults;

/**
 * {@link GlobalDataSourceManagerTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v1.0.0
 */
public class GlobalDataSourceManagerTests {

    // see:https://www.testcontainers.org/modules/databases/mongodb/#usage-example
    static MongoDBContainer mongoDBContainer;
    static GlobalDataSourceManager globalDataSourceManager;

    // @BeforeClass // invalid?
    public static void setup() throws Exception {
        // @formatter:off
        //if (!isBlank(System.getenv("TEST_USE_DEFAULT_MONGODB")) && isNull(mongoDBContainer)) {
        //    synchronized (GlobalDataSourceManagerTests.class) {
        //        if (isNull(mongoDBContainer)) {
        //            mongoDBContainer = new MongoDBContainer(
        //                    DockerImageName.parse("bitnami/mongodb:4.4.6").asCompatibleSubstituteFor("mongo:4.4.6"));
        //            new Thread(() -> mongoDBContainer.start()).start();
        //            Thread.sleep(100000l);
        //        }
        //    }
        //}
        // @formatter:on

        // Mock manual inject depends.
        if (isNull(globalDataSourceManager)) {
            synchronized (GlobalDataSourceManagerTests.class) {
                if (isNull(globalDataSourceManager)) {
                    globalDataSourceManager = new GlobalDataSourceManager();
                    globalDataSourceManager.config = TestSetupDefaults.createExecutionConfig();
                    globalDataSourceManager.mongoRepository = TestSetupDefaults.createMongoRepository();
                    globalDataSourceManager.builders = singletonList(new MongoSourceFacade.MongoSourceFacadeBuilder());
                    globalDataSourceManager.init();
                }
            }
        }
    }

    @Test
    @RepeatedTest(10)
    public void testMongoSourceFacadeFindList() throws Exception {
        setup();

        final MongoSourceFacade mongoSourceFacade = globalDataSourceManager.loadDataSource(DataSourceType.MONGO, "default");
        System.out.println("mongoSourceFacade : " + mongoSourceFacade);

        // @formatter:off
        final String queryBson = ""
                + "{ $match: { \"eventType\": \"ecommerce_trade_gift\" } },"
                + "{ $project: { \"delFlag\": 0 } }";
        // @formatter:on
        final List<Map<String, Object>> bsonFilters = new ArrayList<>();
        bsonFilters.add(BsonUtils2.asMap(BsonDocument.parse(queryBson)));

        List<JsonNode> result = mongoSourceFacade.findList(MongoCollectionDefinition.AGGREGATES.getName(), bsonFilters);
        System.out.println(toJSONString(result));
    }

}
