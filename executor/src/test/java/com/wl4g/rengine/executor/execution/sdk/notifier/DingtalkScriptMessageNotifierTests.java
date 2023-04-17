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
package com.wl4g.rengine.executor.execution.sdk.notifier;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseMapObject;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.valueOf;

import java.util.Map;

import org.junit.Test;

import com.wl4g.infra.common.notification.dingtalk.internal.DingtalkAPI.CreateSceneGroupV2;
import com.wl4g.infra.common.serialize.JacksonUtils;

/**
 * {@link DingtalkScriptMessageNotifierTests}
 * 
 * @author James Wong
 * @date 2023-01-11
 * @since v1.0.0
 */
public class DingtalkScriptMessageNotifierTests {

    @Test
    public void testMapParameterToCreateScenesGroupV2() throws Exception {
        final CreateSceneGroupV2 mockCreateGroup = CreateSceneGroupV2.builder()
                .title("测试群-01")
                .templateId("4ba6847f-b9b0-42ca-96ea-22c4ed8a3fbd")
                .ownerUserId("6165471647114842627")
                .userIds("6165471647114842627")
                .subadminIds("6165471647114842627")
                .build();
        final String mockJson = toJSONString(mockCreateGroup);
        System.out.println("mockJson : " + mockJson);

        final Map<String, Object> mockParameter = parseMapObject(mockJson);
        System.out.println("mockParameter :" + mockParameter);

        final CreateSceneGroupV2 createGroup = JacksonUtils.convertBean(mockParameter, CreateSceneGroupV2.class);
        System.out.println("createGroup :" + createGroup);
        assert valueOf(createGroup.getTemplateId()).equals("4ba6847f-b9b0-42ca-96ea-22c4ed8a3fbd");
        assert valueOf(createGroup.getSubadminIds()).equals("6165471647114842627");

        // no-safety, poor compatibility?
        // final CreateSceneGroupV2 createGroup2 = new CreateSceneGroupV2();
        // BeanUtilsBean.getInstance().copyProperties(createGroup2, parameter1);
        // System.out.println("createGroup2 :" + createGroup2);
    }

}
