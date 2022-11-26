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
package com.wl4g.rengine.common.entity;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.wl4g.rengine.common.entity.WorkflowGraph.BaseNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeType;

/**
 * {@link WorkflowGraphTests}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v3.0.0
 */
public class WorkflowGraphTests {

    @Test
    public void testWorkflowGraphSerialze() {
        LogicalNode node = new LogicalNode().withId("100010").withName("同时满足").withRelation(LogicalType.AND);
        node.getAttributes().put("color", "white");
        node.getAttributes().put("left", "10px");
        node.getAttributes().put("top", "10px");
        System.out.println(toJSONString(node, false));
        Assertions.assertEquals(node.getType(), NodeType.LOGICAL.name());
    }

    @Test
    public void testWorkflowGraphDeserialze() {
        String json = "{\"@type\":\"LOGICAL\",\"id\":\"100010\",\"name\":\"同时满足\",\"attributes\":{\"top\":\"10px\",\"color\":\"white\",\"left\":\"10px\"},\"logical\":\"AND\"}";
        BaseNode<?> node = parseJSON(json, BaseNode.class);
        System.out.println("      node.getClass(): " + node.getClass());
        System.out.println("         node.getId(): " + node.getId());
        System.out.println("       node.getName(): " + node.getName());
        System.out.println("       node.getType(): " + node.getType());
        System.out.println(" node.getAttributes(): " + node.getAttributes());
    }

}
