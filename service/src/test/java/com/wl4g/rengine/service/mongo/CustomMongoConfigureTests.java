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
package com.wl4g.rengine.service.mongo;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonList;

import java.util.Date;

import org.junit.Test;

import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.MongoDataSourceProperties;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.service.model.DataSourceSave;

/**
 * {@link CustomMongoConfigureTests}
 * 
 * @author James Wong
 * @date 2022-12-28
 * @since v1.0.0
 */
public class CustomMongoConfigureTests {

    @Test
    public void testSerializeIgnoreAndTransformProperties() {
        final DataSourceProperties source = DataSourceSave.builder()
                .id(111101001L)
                .enable(1)
                .labels(singletonList("foo"))
                .details(MongoDataSourceProperties.builder().connectionString("mongodb://localhost:27010").build())
                .createBy(11L)
                .createDate(new Date())
                .updateBy(11L)
                .updateDate(new Date())
                .delFlag(0)
                .build();
        final String json = toJSONString(BsonEntitySerializers.DEFAULT_MODIFIER_MAPPER, source,
                BsonEntitySerializers.ID_SERIALIZE_TRANSFORMER, BsonEntitySerializers.BASE_EXCLUDER);
        System.out.println(json);
        assert json.contains("{\"_id\":111101001");
    }

    @Test
    public void testDeSerializeIgnoreAndTransformProperties() {
        final String json = "{\"_id\":111101001,\"orgCode\":null,\"enable\":1,\"labels\":[\"foo\"],\"remark\":null,\"createBy\":11,\"createDate\":\"2022-12-28 23:27:19\",\"updateBy\":11,\"updateDate\":\"2022-12-28 23:27:19\",\"delFlag\":0,\"name\":null,\"details\":{\"type\":\"MONGO\",\"connectionString\":\"mongodb://localhost:27010\"}}";
        final DataSourceProperties source = parseJSON(BsonEntitySerializers.DEFAULT_MODIFIER_MAPPER, json,
                DataSourceProperties.class, BsonEntitySerializers.ID_DESERIALIZE_TRANSFORMER,
                BsonEntitySerializers.BASE_EXCLUDER);
        System.out.println(source);
        assert source.getId() == 111101001L;
    }

}
