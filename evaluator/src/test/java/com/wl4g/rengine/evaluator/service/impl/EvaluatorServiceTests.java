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
package com.wl4g.rengine.evaluator.service.impl;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonList;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.evaluator.service.EvaluatorService;
import com.wl4g.rengine.evaluator.util.TestSetupDefaults;

/**
 * {@link EvaluatorServiceTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v1.0.0
 */
// @QuarkusTest
// @ExtendWith(MockitoExtension.class)
// @QuarkusTestResource(value = MongoTestResource.class, initArgs =
// @ResourceArg(name = MongoTestResource.PORT, value = "27017"))
public class EvaluatorServiceTests {

    // @Mock
    // @org.mockito.Mock
    // @InjectMock(convertScopes = true)
    EvaluatorService evaluatorService;

    @Before
    public void setup() {
        // MockitoAnnotations.openMocks(this);
        // JobService mock = Mockito.mock(JobService.class);
        // QuarkusMock.installMockForType(mock, JobService.class);

        // Manual setup/inject depends.
        EvaluatorServiceImpl evaluatorService = new EvaluatorServiceImpl();
        evaluatorService.mongoRepository = TestSetupDefaults.createMongoRepository();
        evaluatorService.config = TestSetupDefaults.createExecutionConfig();
        this.evaluatorService = evaluatorService;
    }

    @Test
    public void testFindScenesWithCascade() {
        try {
            List<Scenes> sceneses = evaluatorService.findScenesWithCascade(singletonList("ecommerce_trade_gift"));
            System.out.println(toJSONString(sceneses, true));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
