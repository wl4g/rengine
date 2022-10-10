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

import org.junit.Before;
import org.junit.Test;

import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.evaluator.service.JobService;
import com.wl4g.rengine.evaluator.util.TestMongoUtil;

/**
 * {@link JobServiceTests}
 * 
 * @author James Wong
 * @version 2022-09-27
 * @since v3.0.0
 */
// @QuarkusTest
// @ExtendWith(MockitoExtension.class)
// @QuarkusTestResource(value = MongoTestResource.class, initArgs =
// @ResourceArg(name = MongoTestResource.PORT, value = "27017"))
public class JobServiceTests {

    // @Mock
    // @org.mockito.Mock
    // @InjectMock(convertScopes = true)
    JobService jobService;

    @Before
    public void setup() {
        // MockitoAnnotations.openMocks(this);
        // JobService mock = Mockito.mock(JobService.class);
        // QuarkusMock.installMockForType(mock, JobService.class);

        // Manual setup/inject depends.
        JobServiceImpl jobService = new JobServiceImpl();
        jobService.mongoRepository = TestMongoUtil.createMongoRepository();
        this.jobService = jobService;
    }

    @Test
    public void testLoadScenesFull() {
        Scenes scenes = jobService.loadScenesFull("iot_generic_temp_warning");
        System.out.println(toJSONString(scenes));
    }

}
