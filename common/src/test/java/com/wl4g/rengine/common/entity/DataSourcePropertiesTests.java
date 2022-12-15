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

import org.junit.Test;

import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.common.entity.DataSourceProperties.JDBCDataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.MongoDataSourceProperties;

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
        final MongoDataSourceProperties config = MongoDataSourceProperties.builder()
                .id(10101001L)
                .type(DataSourceType.MONGO)
                .name("default")
                .connectionString("mongodb://localhost:27017")
                .build();

        String jsonString = toJSONString(config, true);
        System.out.println(jsonString);

        DataSourceProperties ds1 = parseJSON(jsonString, DataSourceProperties.class);
        System.out.println(ds1);
    }

    @Test
    public void testJDBCDataSourcePropertiesSerialize() {
        try {
            final JDBCDataSourceProperties config = JDBCDataSourceProperties.builder()
                    .id(10101001L)
                    .type(DataSourceType.JDBC)
                    .name("default")
                    .fetchSize(1024)
                    .jdbcUrl(
                            "jdbc:mysql://localhost:3306/mysql?useUnicode=true&serverTimezone=UTC&characterEncoding=utf-8&useSSL=false")
                    .username("test")
                    .password("123456")
                    .build();
            String jsonString = toJSONString(config, true);
            System.out.println(jsonString);
            DataSourceProperties ds1 = parseJSON(jsonString, DataSourceProperties.class);
            System.out.println(ds1);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
