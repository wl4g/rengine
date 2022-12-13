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

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import org.junit.Test;

import com.wl4g.rengine.evaluator.execution.datasource.MongoSourceFacade.MongoSourceBuildConfig;

/**
 * {@link MongoSourceFacadeTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v1.0.0
 */
public class MongoSourceFacadeTests {

    @Test
    public void testSerializeMongoSourceBuildConfig() {
        final MongoSourceBuildConfig config = new MongoSourceBuildConfig("mongodb://localhost:27017/rengine");

        final String json = toJSONString(config);
        System.out.println("    Serialized : " + json);

        final MongoSourceBuildConfig config2 = parseJSON(json, MongoSourceBuildConfig.class);
        System.out.println("  DeSerialized : " + config2);
    }

}
