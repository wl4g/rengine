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

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.wl4g.rengine.evaluator.service.AggregationService;
import com.wl4g.rengine.evaluator.util.TestMongoUtil;

/**
 * {@link AggregationServiceTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v3.0.0
 */
public class AggregationServiceTests {

    AggregationService aggregationService;

    @Before
    public void setup() {
        // Manual setup/inject depends.
        AggregationServiceImpl aggregationService = new AggregationServiceImpl();
        aggregationService.mongoRepository = TestMongoUtil.createMongoRepository();
        this.aggregationService = aggregationService;
    }

    @Test
    public void testFindList1() {
        Map<String, Object> query = new HashMap<>();
        // TODO
        query.put("eventType", "");

        List<Map<String, Object>> result = aggregationService.findList(query);
        System.out.println(toJSONString(result));
    }

}
