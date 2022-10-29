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

import com.wl4g.rengine.common.entity.FlowNode.FlowNodeType;
import com.wl4g.rengine.common.entity.FlowNode.OperationFlowNode;

/**
 * {@link FlowNodeTests}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v3.0.0
 */
public class FlowNodeTests {

    @Test
    public void testOperationFlowNodeSerialze() {
        FlowNode node = OperationFlowNode.builder()
                .id("100010")
                .name("同时满足")
                .type(FlowNodeType.OPERATION.name())
                .color("WHITE")
                .top("80px")
                .left("20px")
                .build();
        System.out.println(toJSONString(node));
    }

    @Test
    public void testOperationFlowNodeDeSerialze() {
        String json = "{\"@type\":\"OPERATION\",\"id\":\"100010\",\"name\":\"同时满足\",\"top\":\"80px\",\"left\":\"20px\",\"color\":\"WHITE\",\"attributes\":{},\"operator\":null,\"@type\":\"OPERATION\"}";
        FlowNode node = parseJSON(json, FlowNode.class);
        System.out.println(" node.getClass(): " + node.getClass());
        System.out.println("    node.getId(): " + node.getId());
        System.out.println("  node.getName(): " + node.getName());
        System.out.println("  node.getType(): " + node.getType());
    }

}
