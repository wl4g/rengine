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
package com.wl4g.rengine.executor.execution.sdk.datasource;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
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
import com.wl4g.infra.common.serialize.BsonUtils2;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.executor.execution.sdk.datasource.JDBCSourceFacade.JDBCSourceFacadeBuilder;
import com.wl4g.rengine.executor.execution.sdk.datasource.KafkaSourceFacade.KafkaSourceFacadeBuilder;
import com.wl4g.rengine.executor.execution.sdk.datasource.MongoSourceFacade.MongoSourceFacadeBuilder;
import com.wl4g.rengine.executor.execution.sdk.datasource.RedisSourceFacade.RedisSourceFacadeBuilder;
import com.wl4g.rengine.executor.util.TestDefaultBaseSetup;

/**
 * {@link GlobalMessageNotifierManagerTests}
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
        //    synchronized (GlobalMessageNotifierManagerTests.class) {
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
                    globalDataSourceManager.config = TestDefaultBaseSetup.createExecutionConfig();
                    globalDataSourceManager.mongoRepository = TestDefaultBaseSetup.createMongoRepository();
                    globalDataSourceManager.builders = asList(new MongoSourceFacadeBuilder(), new JDBCSourceFacadeBuilder(),
                            new RedisSourceFacadeBuilder(), new KafkaSourceFacadeBuilder());
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

    @Test
    @RepeatedTest(10)
    public void testJDBCSourceFacadeFindList() throws Exception {
        setup();

        final JDBCSourceFacade jdbcSourceFacade = globalDataSourceManager.loadDataSource(DataSourceType.JDBC, "default");
        System.out.println("jdbcSourceFacade : " + jdbcSourceFacade);

        final String sql = "select * from user where user='root'";
        final List<Map<String, Object>> result = jdbcSourceFacade.findList(sql);
        System.out.println(result);
    }

    @Test
    @RepeatedTest(10)
    public void testRedisSourceFacadeFindList() throws Exception {
        setup();

        final RedisSourceFacade redisSourceFacade = globalDataSourceManager.loadDataSource(DataSourceType.REDIS, "default");
        System.out.println("redisSourceFacade : " + redisSourceFacade);

        redisSourceFacade.set("foo11", singletonMap("bar11", 123));
        final JsonNode value = redisSourceFacade.get("foo11");
        System.out.println(value);

        assert value.get("bar11").asInt() == 123;
    }

    @Test
    @RepeatedTest(10)
    public void testKafkaSourceFacadeFindList() throws Exception {
        setup();

        final KafkaSourceFacade kafkaSourceFacade = globalDataSourceManager.loadDataSource(DataSourceType.KAFKA, "default");
        System.out.println("kafkaSourceFacade : " + kafkaSourceFacade);

        kafkaSourceFacade.publish("test", singletonMap("foo11", "bar11"));
    }

}
