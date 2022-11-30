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
package com.wl4g.rengine.common.graph;

import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonMap;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.entity.WorkflowGraph.BaseNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.BootNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.FailbackNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeConnection;
import com.wl4g.rengine.common.entity.WorkflowGraph.ProcessNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.RunNode;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;

/**
 * {@link ExecutionGraphTests}
 * 
 * @author James Wong
 * @version 2022-11-03
 * @since v3.0.0
 */
public class ExecutionGraphTests {

    @Test
    public void testECommerceTradeWorkflow() {
        List<BaseNode<?>> nodes = new LinkedList<>();
        nodes.add(new BootNode().withId("0").withName("The Boot"));
        nodes.add(new ProcessNode().withId("11").withName("预处理(如篡改当前时间以用于测试目的)").withRuleId("r100100"));
        nodes.add(new ProcessNode().withId("21").withName("当前时间是否满足(10.1~10.8)").withRuleId("r100222"));
        nodes.add(new LogicalNode().withId("31").withName("ALL_AND逻辑运算").withLogical(LogicalType.ALL_AND));
        nodes.add(new LogicalNode().withId("41").withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new LogicalNode().withId("42").withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new ProcessNode().withId("51").withName("充值是否>=120元").withRuleId("r100101"));
        nodes.add(new LogicalNode().withId("52").withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new ProcessNode().withId("53").withName("当前时间是否满足(10.5~10.8)").withRuleId("r100223"));
        nodes.add(new ProcessNode().withId("54").withName("充值是否>=50元").withRuleId("r100104"));
        nodes.add(new ProcessNode().withId("61").withName("赠送库存是否<=100").withRuleId("r100102"));
        nodes.add(new FailbackNode().withId("62").withName("如果赠送余额失败则执行回退规则").withRuleId("r111111"));
        nodes.add(new RunNode().withId("63").withName("赠送20积分").withRuleId("r100105"));
        nodes.add(new RunNode().withId("71").withName("赠送10元余额").withRuleId("r100103"));

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

        WorkflowGraph workflow = new WorkflowGraph(nodes, collections);
        System.out.println("Workflow Nodes Json : " + toJSONString(workflow));

        ExecutionGraphParameter parameter = ExecutionGraphParameter.builder()
                .requestTime(currentTimeMillis())
                .traceId(UUID.randomUUID().toString())
                .debug(true)
                .workflowId("wf1234567890")
                .args(singletonMap("deviceId", "12345678"))
                .build();
        ExecutionGraphContext context = new ExecutionGraphContext(parameter, ctx -> ReturnState.TRUE);
        ExecutionGraph<?> graph = ExecutionGraph.from(workflow);
        ExecutionGraphResult result = graph.apply(context);
        System.out.println("    Executed Result : " + toJSONString(result));
        System.out.println("Executed Trace Text : \n" + context.asTraceText(true));
    }

}
