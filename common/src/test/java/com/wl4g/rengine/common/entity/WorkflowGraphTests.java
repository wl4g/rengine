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
import static java.util.Collections.singletonList;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.wl4g.rengine.common.entity.WorkflowGraph.BaseNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.BootNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.FailbackNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeConnection;
import com.wl4g.rengine.common.entity.WorkflowGraph.ProcessNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.RelationNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.RunNode;

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
        final WorkflowGraph workflowGraph = buildDefaultWorkflowGraph();
        System.out.println("Workflow Nodes Json : " + toJSONString(workflowGraph, false));
    }

    @Test
    public void testWorkflowGraphDeserialze() {
        final WorkflowGraph graph = parseJSON(testWorkflowGraphJson, WorkflowGraph.class);
        for (BaseNode<?> node : graph.getNodes()) {
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

        List<NodeConnection> collections = new LinkedList<>();
        collections.add(new NodeConnection("11", "0"));
        collections.add(new NodeConnection("21", "11"));
        collections.add(new NodeConnection("31", "21"));
        collections.add(new NodeConnection("41", "31"));
        collections.add(new NodeConnection("42", "31"));
        collections.add(new NodeConnection("51", "41"));
        collections.add(new NodeConnection("52", "41"));
        collections.add(new NodeConnection("53", "42"));
        collections.add(new NodeConnection("54", "42"));
        collections.add(new NodeConnection("61", "51"));
        collections.add(new NodeConnection("62", "52"));
        collections.add(new NodeConnection("63", "54"));
        collections.add(new NodeConnection("71", "62"));

        final WorkflowGraph graph = new WorkflowGraph(10100101L, nodes, collections);
        graph.setId(100100101L);
        graph.setEnable(1);
        graph.setLabels(singletonList("foo"));
        graph.setOrgCode("top");
        graph.setCreateDate(new Date());
        graph.setCreateBy(202002111L);
        graph.setUpdateDate(new Date());
        graph.setUpdateBy(202002111L);
        return graph;
    }

    public static String testWorkflowGraphJson = "{\"_id\":100100101,\"orgCode\":\"top\",\"enable\":1,\"labels\":[\"foo\"],\"remark\":null,\"createBy\":202002111,\"createDate\":\"2023-01-16 23:15:13\",\"updateBy\":202002111,\"updateDate\":\"2023-01-16 23:15:13\",\"delFlag\":null,\"revision\":null,\"workflowId\":null,\"nodes\":[{\"@type\":\"BOOT\",\"id\":\"0\",\"name\":\"The Boot\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"}},{\"@type\":\"PROCESS\",\"id\":\"11\",\"name\":\"预处理(如篡改当前时间以用于测试目的)\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r100100\"},{\"@type\":\"RELATION\",\"id\":\"21\",\"name\":\"当前时间是否满足(10.1~10.8)\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r100222\"},{\"@type\":\"LOGICAL\",\"id\":\"31\",\"name\":\"ALL_AND逻辑运算\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"logical\":\"ALL_AND\"},{\"@type\":\"LOGICAL\",\"id\":\"41\",\"name\":\"AND逻辑运算\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"logical\":\"AND\"},{\"@type\":\"LOGICAL\",\"id\":\"42\",\"name\":\"AND逻辑运算\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"logical\":\"AND\"},{\"@type\":\"RELATION\",\"id\":\"51\",\"name\":\"充值是否>=120元\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r100101\"},{\"@type\":\"LOGICAL\",\"id\":\"52\",\"name\":\"AND逻辑运算\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"logical\":\"AND\"},{\"@type\":\"RELATION\",\"id\":\"53\",\"name\":\"当前时间是否满足(10.5~10.8)\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r100223\"},{\"@type\":\"RELATION\",\"id\":\"54\",\"name\":\"充值是否>=50元\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r100104\"},{\"@type\":\"RELATION\",\"id\":\"61\",\"name\":\"赠送库存是否<=100\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r100102\"},{\"@type\":\"FAILBACK\",\"id\":\"62\",\"name\":\"如果赠送余额失败则执行回退规则\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r111111\"},{\"@type\":\"RUN\",\"id\":\"63\",\"name\":\"赠送20积分\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r100105\"},{\"@type\":\"RUN\",\"id\":\"71\",\"name\":\"赠送10元余额\",\"attributes\":{\"top\":\"10px\",\"color\":\"#5f5f5f\",\"left\":\"10px\"},\"ruleId\":\"r100103\"}],\"connections\":[{\"name\":null,\"to\":\"11\",\"from\":\"0\",\"attributes\":null},{\"name\":null,\"to\":\"21\",\"from\":\"11\",\"attributes\":null},{\"name\":null,\"to\":\"31\",\"from\":\"21\",\"attributes\":null},{\"name\":null,\"to\":\"41\",\"from\":\"31\",\"attributes\":null},{\"name\":null,\"to\":\"42\",\"from\":\"31\",\"attributes\":null},{\"name\":null,\"to\":\"51\",\"from\":\"41\",\"attributes\":null},{\"name\":null,\"to\":\"52\",\"from\":\"41\",\"attributes\":null},{\"name\":null,\"to\":\"53\",\"from\":\"42\",\"attributes\":null},{\"name\":null,\"to\":\"54\",\"from\":\"42\",\"attributes\":null},{\"name\":null,\"to\":\"61\",\"from\":\"51\",\"attributes\":null},{\"name\":null,\"to\":\"62\",\"from\":\"52\",\"attributes\":null},{\"name\":null,\"to\":\"63\",\"from\":\"54\",\"attributes\":null},{\"name\":null,\"to\":\"71\",\"from\":\"62\",\"attributes\":null}]}";

}
