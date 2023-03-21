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
package com.wl4g.rengine.common.entity.graph;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Collections.singletonList;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.wl4g.rengine.common.entity.graph.StandardGraph.BaseNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.BootNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.FailbackNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.LogicalNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.LogicalType;
import com.wl4g.rengine.common.entity.graph.StandardGraph.NodeEdge;
import com.wl4g.rengine.common.entity.graph.StandardGraph.ProcessNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.RelationNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.RunNode;
import com.wl4g.rengine.common.util.BsonEntitySerializers;

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
        final WorkflowGraph graph = buildDefaultWorkflowGraph();
        System.out.println("Workflow Node Json1 : " + toJSONString(graph, false));
        System.out.println("Workflow Node Json2 : " + BsonEntitySerializers.serialize(graph));
    }

    @Test
    public void testWorkflowGraphDeserialze() {
        final WorkflowGraph graph = parseJSON(testWorkflowGraphJson, WorkflowGraph.class);
        for (BaseNode<?> node : ((StandardGraph) graph.getDetails()).getNodes()) {
            System.out.println("      node.getClass(): " + node.getClass());
            System.out.println("         node.getId(): " + node.getId());
            System.out.println("       node.getName(): " + node.getName());
            System.out.println("       node.getType(): " + node.getType());
            System.out.println(" node.getAttributes(): " + node.getAttributes());
        }
    }

    public static WorkflowGraph buildDefaultWorkflowGraph() {
        List<BaseNode<?>> nodes = new LinkedList<>();
        nodes.add(new BootNode().withId("0").withName("The Boot"));
        nodes.add(new ProcessNode().withId("11").withName("预处理(如篡改当前时间以用于测试目的)").withRuleId(10100101L));
        nodes.add(new RelationNode().withId("21").withName("当前时间是否满足(10.1~10.8)").withRuleId(10100102L));
        nodes.add(new LogicalNode().withId("31").withName("ALL_AND逻辑运算").withLogical(LogicalType.ALL_AND));
        nodes.add(new LogicalNode().withId("41").withPriority(1).withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new LogicalNode().withId("42").withPriority(2).withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new RelationNode().withId("51").withPriority(1).withName("充值是否>=120元").withRuleId(10100103L));
        nodes.add(new LogicalNode().withId("52").withPriority(2).withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new RelationNode().withId("53").withPriority(1).withName("当前时间是否满足(10.5~10.8)").withRuleId(10100104L));
        nodes.add(new RelationNode().withId("54").withPriority(2).withName("充值是否>=50元").withRuleId(10100105L));
        nodes.add(new RelationNode().withId("61").withName("赠送库存是否<=100").withRuleId(10100105L));
        nodes.add(new FailbackNode().withId("62").withName("如果赠送余额失败则执行回退规则").withRuleId(10100106L));
        nodes.add(new RunNode().withId("63").withName("赠送20积分").withRuleId(10100108L));
        nodes.add(new RunNode().withId("71").withName("赠送10元余额").withRuleId(10100107L));

        List<NodeEdge> edges = new LinkedList<>();
        edges.add(new NodeEdge("11", "0"));
        edges.add(new NodeEdge("21", "11"));
        edges.add(new NodeEdge("31", "21"));
        edges.add(new NodeEdge("41", "31"));
        edges.add(new NodeEdge("42", "31"));
        edges.add(new NodeEdge("51", "41"));
        edges.add(new NodeEdge("52", "41"));
        edges.add(new NodeEdge("53", "42"));
        edges.add(new NodeEdge("54", "42"));
        edges.add(new NodeEdge("61", "51"));
        edges.add(new NodeEdge("62", "52"));
        edges.add(new NodeEdge("63", "54"));
        edges.add(new NodeEdge("71", "62"));

        final StandardGraph graph = new StandardGraph(nodes, edges);
        final WorkflowGraph workflowGraph = new WorkflowGraph(10100101L);
        workflowGraph.setId(100100101L);
        workflowGraph.setEnable(1);
        workflowGraph.setLabels(singletonList("foo"));
        workflowGraph.setOrgCode("top");
        workflowGraph.setCreateDate(new Date());
        workflowGraph.setCreateBy(202002111L);
        workflowGraph.setUpdateDate(new Date());
        workflowGraph.setUpdateBy(202002111L);
        workflowGraph.setDetails(graph);
        return workflowGraph;
    }

    public static String testWorkflowGraphJson = "{\"_id\":100100101,\"enable\":1,\"labels\":[\"foo\"],\"remark\":null,\"createBy\":202002111,\"createDate\":\"2023-03-21 15:43:25\",\"updateBy\":202002111,\"updateDate\":\"2023-03-21 15:43:25\",\"delFlag\":null,\"nameEn\":null,\"nameZh\":null,\"tenantId\":null,\"revision\":null,\"workflowId\":10100101,\"details\":{\"engine\":\"STANDARD_GRAPH\",\"nodes\":[{\"@type\":\"BOOT\",\"id\":\"0\",\"priority\":null,\"name\":\"The Boot\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"}},{\"@type\":\"PROCESS\",\"id\":\"11\",\"priority\":null,\"name\":\"预处理(如篡改当前时间以用于测试目的)\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100101},{\"@type\":\"RELATION\",\"id\":\"21\",\"priority\":null,\"name\":\"当前时间是否满足(10.1~10.8)\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100102},{\"@type\":\"LOGICAL\",\"id\":\"31\",\"priority\":null,\"name\":\"ALL_AND逻辑运算\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"logical\":\"ALL_AND\"},{\"@type\":\"LOGICAL\",\"id\":\"41\",\"priority\":1,\"name\":\"AND逻辑运算\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"logical\":\"AND\"},{\"@type\":\"LOGICAL\",\"id\":\"42\",\"priority\":2,\"name\":\"AND逻辑运算\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"logical\":\"AND\"},{\"@type\":\"RELATION\",\"id\":\"51\",\"priority\":1,\"name\":\"充值是否>=120元\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100103},{\"@type\":\"LOGICAL\",\"id\":\"52\",\"priority\":2,\"name\":\"AND逻辑运算\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"logical\":\"AND\"},{\"@type\":\"RELATION\",\"id\":\"53\",\"priority\":1,\"name\":\"当前时间是否满足(10.5~10.8)\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100104},{\"@type\":\"RELATION\",\"id\":\"54\",\"priority\":2,\"name\":\"充值是否>=50元\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100105},{\"@type\":\"RELATION\",\"id\":\"61\",\"priority\":null,\"name\":\"赠送库存是否<=100\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100105},{\"@type\":\"FAILBACK\",\"id\":\"62\",\"priority\":null,\"name\":\"如果赠送余额失败则执行回退规则\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100106},{\"@type\":\"RUN\",\"id\":\"63\",\"priority\":null,\"name\":\"赠送20积分\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100108},{\"@type\":\"RUN\",\"id\":\"71\",\"priority\":null,\"name\":\"赠送10元余额\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":10100107}],\"edges\":[{\"name\":null,\"to\":\"11\",\"from\":\"0\",\"attributes\":null},{\"name\":null,\"to\":\"21\",\"from\":\"11\",\"attributes\":null},{\"name\":null,\"to\":\"31\",\"from\":\"21\",\"attributes\":null},{\"name\":null,\"to\":\"41\",\"from\":\"31\",\"attributes\":null},{\"name\":null,\"to\":\"42\",\"from\":\"31\",\"attributes\":null},{\"name\":null,\"to\":\"51\",\"from\":\"41\",\"attributes\":null},{\"name\":null,\"to\":\"52\",\"from\":\"41\",\"attributes\":null},{\"name\":null,\"to\":\"53\",\"from\":\"42\",\"attributes\":null},{\"name\":null,\"to\":\"54\",\"from\":\"42\",\"attributes\":null},{\"name\":null,\"to\":\"61\",\"from\":\"51\",\"attributes\":null},{\"name\":null,\"to\":\"62\",\"from\":\"52\",\"attributes\":null},{\"name\":null,\"to\":\"63\",\"from\":\"54\",\"attributes\":null},{\"name\":null,\"to\":\"71\",\"from\":\"62\",\"attributes\":null}]},\"attributes\":null}";
}
