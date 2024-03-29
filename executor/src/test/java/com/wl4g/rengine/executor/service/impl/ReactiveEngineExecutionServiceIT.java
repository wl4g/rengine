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
package com.wl4g.rengine.executor.service.impl;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

import java.time.Duration;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;

import com.wl4g.rengine.common.entity.RuleScript.RuleScriptWrapper;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;
import com.wl4g.rengine.executor.meter.TestDefaultMeterSetup;
import com.wl4g.rengine.executor.util.TestDefaultBaseSetup;
import com.wl4g.rengine.executor.util.TestDefaultRedisSetup;

import io.smallrye.mutiny.Uni;

/**
 * {@link ReactiveEngineExecutionServiceIT}
 * 
 * @author James Wong
 * @date 2022-09-27
 * @since v1.0.0
 */
// @QuarkusTest
// @ExtendWith(MockitoExtension.class)
// @QuarkusTestResource(value = MongoTestResource.class, initArgs =
// @ResourceArg(name = MongoTestResource.PORT, value = "27017"))
public class ReactiveEngineExecutionServiceIT {

    // @Mock
    // @org.mockito.Mock
    // @InjectMock(convertScopes = true)
    // ReactiveEngineExecutionService engineExecutionService;

    static ReactiveEngineExecutionServiceImpl engineExecutionService;

    @Before
    public void setup() {
        // MockitoAnnotations.openMocks(this);
        // DictService mock = Mockito.mock(DictService.class);
        // QuarkusMock.installMockForType(mock, DictService.class);
        if (isNull(engineExecutionService)) {
            synchronized (ReactiveEngineExecutionServiceIT.class) {
                if (isNull(engineExecutionService)) {
                    // Manual setup/inject depends.
                    engineExecutionService = new ReactiveEngineExecutionServiceImpl();
                    engineExecutionService.engineConfig = TestDefaultBaseSetup.createEngineConfig();
                    engineExecutionService.meterService = TestDefaultMeterSetup.setup();
                    // engineExecutionService.lifecycleExecutionService = new
                    // LifecycleExecutionService(); // TODO
                    engineExecutionService.mongoRepository = TestDefaultBaseSetup.createMongoRepository();
                    engineExecutionService.reactiveRedisDS = TestDefaultRedisSetup.buildRedisDataSourceDefault().getReactive();
                    System.out.println("Init...");
                    engineExecutionService.init();
                }
            }
        }
    }

    @Test
    public void testFindRuleScripts() {
        setup();
        try {
            Uni<List<RuleScriptWrapper>> ruleScriptsUni = engineExecutionService
                    .findRuleScripts(singletonList(6150869239922100L));
            System.out.println("Await for " + ruleScriptsUni + " ...");
            System.out.println("----------------");

            final List<RuleScriptWrapper> ruleScripts = ruleScriptsUni.await().atMost(Duration.ofSeconds(60));
            System.out.println(toJSONString(ruleScripts, true));
            assert !ruleScripts.isEmpty();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @RepeatedTest(5)
    public void testFindScenesWorkflowGraphRules() {
        setup();
        try {
            Uni<List<ScenesWrapper>> scenesesUni = engineExecutionService
                    .findScenesWorkflowGraphRules(singletonList("ecommerce_trade_gift"), 1);

            System.out.println("Await for " + scenesesUni + " ...");
            System.out.println("----------------");

            final var sceneses = scenesesUni.await().atMost(Duration.ofSeconds(60));
            System.out.println(toJSONString(sceneses, true));
            assert !sceneses.isEmpty();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @RepeatedTest(5)
    public void testFindScenesWorkflowGraphRulesWithCached() {
        setup();

        try {
            Uni<List<ScenesWrapper>> scenesesUni = engineExecutionService
                    .findScenesWorkflowGraphRulesWithCached(WorkflowExecuteRequest.builder()
                            .scenesCodes(singletonList("ecommerce_trade_gift"))
                            .bestEffort(true)
                            .timeout(60_000L)
                            .build(), 1);

            System.out.println("Await for " + scenesesUni + " ...");
            System.out.println("----------------");

            final var sceneses = scenesesUni.await().atMost(Duration.ofSeconds(60));
            System.out.println(toJSONString(sceneses, true));
            assert !sceneses.isEmpty();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
