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
package com.wl4g.rengine.common.graph;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.wl4g.rengine.common.entity.OperatorNode;
import com.wl4g.rengine.common.entity.OperatorNode.EndNode;
import com.wl4g.rengine.common.entity.OperatorNode.FlowNodeType;
import com.wl4g.rengine.common.entity.OperatorNode.RelationNode;
import com.wl4g.rengine.common.entity.OperatorNode.RelationType;
import com.wl4g.rengine.common.entity.OperatorNode.StartNode;

/**
 * {@link WorkflowGraphTests}
 * 
 * @author James Wong
 * @version 2022-11-03
 * @since v3.0.0
 */
public class WorkflowGraphTests {

    @Test
    public void testFromFlowNodes() {
        List<OperatorNode> nodes = new LinkedList<>();
        nodes.add(StartNode.builder().type(FlowNodeType.START.name()).id("0").name("The Start").build());

        OperatorNode node1 = RelationNode.builder()
                .type(FlowNodeType.RELATION.name())
                .id("1")
                .parentId("0")
                .name("同时满足")
                .relation(RelationType.AND)
                .build();
        nodes.add(node1);

        OperatorNode node2 = RelationNode.builder()
                .type(FlowNodeType.RELATION.name())
                .id("2")
                .parentId("1")
                .name("同时满足")
                .relation(RelationType.AND)
                .build();
        nodes.add(node2);

        OperatorNode node3 = RelationNode.builder()
                .type(FlowNodeType.RELATION.name())
                .id("3")
                .parentId("2")
                .name("同时满足")
                .relation(RelationType.AND)
                .build();
        nodes.add(node3);

        OperatorNode node4 = RelationNode.builder()
                .type(FlowNodeType.RELATION.name())
                .id("4")
                .parentId("1")
                .name("同时满足")
                .relation(RelationType.AND)
                .build();
        nodes.add(node4);

        OperatorNode node5 = RelationNode.builder()
                .type(FlowNodeType.RELATION.name())
                .id("5")
                .parentId("4")
                .name("同时满足")
                .relation(RelationType.AND)
                .build();
        nodes.add(node5);

        nodes.add(EndNode.builder().type(FlowNodeType.END.name()).id("100").parentId("5").name("The End").build());

        WorkflowGraph graph = WorkflowGraph.from(nodes);
        System.out.println(graph);
        System.out.println(toJSONString(graph));
        Assertions.assertEquals(graph.getChildrens().size(), 1);
    }

    @Test
    public void testFromJson() {
        String json = "{\"@type\":\"WorkflowGraph\",\"id\":\"0\",\"parentId\":null,\"name\":\"The Start\",\"top\":\"0px\",\"left\":\"1px\",\"color\":\"blue\",\"attributes\":{},\"childrens\":[{\"@type\":\"WorkflowGraph\",\"id\":\"1\",\"parentId\":\"0\",\"name\":\"同时满足\",\"top\":\"0px\",\"left\":\"1px\",\"color\":\"blue\",\"attributes\":{},\"childrens\":[{\"@type\":\"WorkflowGraph\",\"id\":\"2\",\"parentId\":\"1\",\"name\":\"同时满足\",\"top\":\"0px\",\"left\":\"1px\",\"color\":\"blue\",\"attributes\":{},\"childrens\":[{\"@type\":\"WorkflowGraph\",\"id\":\"3\",\"parentId\":\"2\",\"name\":\"同时满足\",\"top\":\"0px\",\"left\":\"1px\",\"color\":\"blue\",\"attributes\":{},\"childrens\":[]}]},{\"@type\":\"WorkflowGraph\",\"id\":\"4\",\"parentId\":\"1\",\"name\":\"同时满足\",\"top\":\"0px\",\"left\":\"1px\",\"color\":\"blue\",\"attributes\":{},\"childrens\":[{\"@type\":\"WorkflowGraph\",\"id\":\"5\",\"parentId\":\"4\",\"name\":\"同时满足\",\"top\":\"0px\",\"left\":\"1px\",\"color\":\"blue\",\"attributes\":{},\"childrens\":[{\"@type\":\"WorkflowGraph\",\"id\":\"100\",\"parentId\":\"5\",\"name\":\"The End\",\"top\":\"0px\",\"left\":\"1px\",\"color\":\"blue\",\"attributes\":{},\"childrens\":[]}]}]}]}]}";
        WorkflowGraph graph = parseJSON(json, WorkflowGraph.class);
        System.out.println(graph);
    }

}
