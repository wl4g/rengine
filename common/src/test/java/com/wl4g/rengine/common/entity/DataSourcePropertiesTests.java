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
package com.wl4g.rengine.common.entity;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Arrays.asList;

import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.Test;

import com.wl4g.infra.common.jedis.JedisClientBuilder.JedisConfig;
import com.wl4g.rengine.common.entity.DataSourceProperties.JDBCDataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.KafkaDataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.MongoDataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.RedisDataSourceProperties;

/**
 * {@link DataSourcePropertiesTests}
 * 
 * @author James Wong
 * @version 2022-12-15
 * @since v1.0.0
 */
public class DataSourcePropertiesTests {

    @Test
    public void testMongoDataSourcePropertiesSerialize() {
        final DataSourceProperties datasource = new DataSourceProperties();
        datasource.setId(10101001L);
        datasource.setName("default");
        datasource.setProperties(MongoDataSourceProperties.builder()
                // .type(DataSourceType.MONGO)
                .connectionString("mongodb://localhost:27017")
                .build());

        String json = toJSONString(datasource, true);
        System.out.println(json);

        DataSourceProperties datasource2 = parseJSON(json, DataSourceProperties.class);
        System.out.println(datasource2);

        assert datasource2.getProperties() instanceof MongoDataSourceProperties;
    }

    @Test
    public void testJDBCDataSourcePropertiesSerialize() {
        final DataSourceProperties datasource = new DataSourceProperties();
        datasource.setId(10101001L);
        datasource.setName("default");
        datasource.setProperties(JDBCDataSourceProperties.builder()
                // .type(DataSourceType.JDBC)
                .fetchSize(1024)
                .jdbcUrl(
                        "jdbc:mysql://localhost:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false")
                .username("test")
                .password("123456")
                .build());

        String json = toJSONString(datasource, true);
        System.out.println(json);

        DataSourceProperties datasource2 = parseJSON(json, DataSourceProperties.class);
        System.out.println(datasource2);

        assert datasource2.getProperties() instanceof JDBCDataSourceProperties;
    }

    @Test
    public void testRedisDataSourcePropertiesSerialize() {
        final DataSourceProperties datasource = new DataSourceProperties();
        datasource.setId(10101001L);
        datasource.setName("default");
        datasource.setProperties(RedisDataSourceProperties.builder()
                // .type(DataSourceType.REDIS)
                .jedisConfig(JedisConfig.builder()
                        .nodes(asList(
                                "localhost:6379,localhost:6380,localhost:6381,localhost:7379,localhost:7380,localhost:7381"))
                        .connTimeout(3000)
                        .password("123456")
                        .build())
                .build());

        String json = toJSONString(datasource, true);
        System.out.println(json);

        DataSourceProperties datasource2 = parseJSON(json, DataSourceProperties.class);
        System.out.println(datasource2);

        assert datasource2.getProperties() instanceof RedisDataSourceProperties;
    }

    @Test
    public void testKafkaDataSourcePropertiesSerialize() {
        final DataSourceProperties datasource = new DataSourceProperties();
        datasource.setId(10101001L);
        datasource.setName("default");
        datasource.setProperties(KafkaDataSourceProperties.builder()
                // .type(DataSourceType.KAFKA)
                .bootstrapServers("localhost:9092")
                .acks("all")
                .bufferMemory(1024_000_000L)
                .build());

        String json = toJSONString(datasource, true);
        System.out.println(json);

        DataSourceProperties datasource2 = parseJSON(json, DataSourceProperties.class);
        System.out.println(datasource2);

        assert datasource2.getProperties() instanceof KafkaDataSourceProperties;

        // Assertion for kafka producer configure.
        final Map<String, Object> configMap = ((KafkaDataSourceProperties) datasource2.getProperties()).toConfigMap();
        new ProducerConfig(configMap);
    }

}
