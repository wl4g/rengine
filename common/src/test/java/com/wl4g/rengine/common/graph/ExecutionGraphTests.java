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
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.util.Collections.singletonMap;
import static java.util.Objects.nonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.wl4g.rengine.common.entity.graph.StandardGraph;
import com.wl4g.rengine.common.entity.graph.StandardGraph.BaseNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.BootNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.LogicalNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.LogicalType;
import com.wl4g.rengine.common.entity.graph.StandardGraph.NodeEdge;
import com.wl4g.rengine.common.entity.graph.StandardGraph.ProcessNode;
import com.wl4g.rengine.common.entity.graph.StandardGraph.RelationNode;
import com.wl4g.rengine.common.entity.graph.WorkflowGraph;
import com.wl4g.rengine.common.entity.graph.WorkflowGraphTests;
import com.wl4g.rengine.common.graph.ExecutionGraph.BaseOperator;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;

/**
 * {@link ExecutionGraphTests}
 * 
 * @author James Wong
 * @date 2022-11-03
 * @since v3.0.0
 */
public class ExecutionGraphTests {

    @Test(expected = Throwable.class)
    public void testInvalidNodeEdge() {
        try {
            List<BaseNode<?>> nodes = new LinkedList<>();
            nodes.add(new BootNode().withId("0").withName("The Boot"));
            nodes.add(new ProcessNode().withId("11").withName("预处理(如篡改当前时间以用于测试目的)").withRuleId(10100101L));
            nodes.add(new RelationNode().withId("21").withName("当前时间是否满足(10.1~10.8)").withRuleId(10100102L));
            nodes.add(new LogicalNode().withId("31").withName("ALL_AND逻辑运算").withLogical(LogicalType.ALL_AND));
            nodes.add(new LogicalNode().withId("41").withPriority(1).withName("AND逻辑运算").withLogical(LogicalType.AND));
            nodes.add(new LogicalNode().withId("42")./* withPriority(2). */withName("AND逻辑运算").withLogical(LogicalType.AND));

            List<NodeEdge> edges = new LinkedList<>();
            edges.add(new NodeEdge("11", "0"));
            edges.add(new NodeEdge("21", "11"));
            edges.add(new NodeEdge("31", "21"));
            edges.add(new NodeEdge("41", "31"));
            // edges.add(new NodeEdge("42", "31"));
            edges.add(new NodeEdge("42", "999"));

            StandardGraph graph = new StandardGraph(nodes, edges);
            out.println("Standard graph json : " + toJSONString(graph));
            ExecutionGraph.from(graph);
        } catch (Throwable ex) {
            // ex.printStackTrace();
            throw ex;
        }
    }

    @Test(expected = Throwable.class)
    public void testInvalidNodeMustPriority() {
        try {
            List<BaseNode<?>> nodes = new LinkedList<>();
            nodes.add(new BootNode().withId("0").withName("The Boot"));
            nodes.add(new ProcessNode().withId("11").withName("预处理(如篡改当前时间以用于测试目的)").withRuleId(10100101L));
            nodes.add(new RelationNode().withId("21").withName("当前时间是否满足(10.1~10.8)").withRuleId(10100102L));
            nodes.add(new LogicalNode().withId("31").withName("ALL_AND逻辑运算").withLogical(LogicalType.ALL_AND));
            nodes.add(new LogicalNode().withId("41").withPriority(1).withName("AND逻辑运算").withLogical(LogicalType.AND));
            nodes.add(new LogicalNode().withId("42")./* withPriority(2). */withName("AND逻辑运算").withLogical(LogicalType.AND));

            List<NodeEdge> edges = new LinkedList<>();
            edges.add(new NodeEdge("11", "0"));
            edges.add(new NodeEdge("21", "11"));
            edges.add(new NodeEdge("31", "21"));
            edges.add(new NodeEdge("41", "31"));
            edges.add(new NodeEdge("42", "31"));

            StandardGraph graph = new StandardGraph(nodes, edges);
            out.println("Standard graph json : " + toJSONString(graph));
            ExecutionGraph.from(graph);
        } catch (Throwable ex) {
            // ex.printStackTrace();
            throw ex;
        }
    }

    @Test
    public void testECommerceTradeWorkflow() {
        try {
            final WorkflowGraph workflowGraph = WorkflowGraphTests.buildDefaultWorkflowGraph();
            out.println("Workflow Node Json : " + toJSONString(workflowGraph));

            ExecutionGraphParameter parameter = ExecutionGraphParameter.builder()
                    .requestTime(currentTimeMillis())
                    .traceId(UUID.randomUUID().toString())
                    .trace(true)
                    .workflowId(100100101L)
                    .args(singletonMap("deviceId", "12345678"))
                    .build();

            ExecutionGraphContext context = new ExecutionGraphContext(parameter, ctx -> { // 模拟(PROCESS/RELATION/RUN)类型的node执行script,也只有这几种类型才需要执行script
                final String nodeId = ctx.getCurrentNode().getId(); // 当前执行script的节点ID
                final String nodeType = ((BaseOperator<?>) ctx.getCurrentNode()).getType(); // 当前执行script的节点Type

                out.println(
                        format("current nodeId: %s@%s, lastResult : %s", nodeId, nodeType, toJSONString(ctx.getLastResult())));

                // 1. 在之后支持执行script的节点的规则代码中, 可使用 ctx.getLastResult()
                // 获取前一个节点的返回值.
                // 2. 当前节点返回值会覆盖上一个节点的返回值.
                return new ExecutionGraphResult(ReturnState.TRUE, singletonMap("foo" + nodeId, "bar" + nodeId));
            });

            ExecutionGraph<?> execution = ExecutionGraph.from((StandardGraph) workflowGraph.getDetails());
            ExecutionGraphResult result = execution.apply(context);

            out.println("-------------------------------------------------------");
            out.println("      Executed result : " + toJSONString(result));
            out.println("-------------------------------------------------------");
            out.println("Executed tracing info : \n" + context.asTraceText(true));

            assert nonNull(result);
            assert valueOf(result.getValueMap().get("foo63")).equals("bar63");

        } catch (Throwable ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

}
