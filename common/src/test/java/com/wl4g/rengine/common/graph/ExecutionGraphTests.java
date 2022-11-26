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
import static java.util.Collections.emptyMap;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.entity.WorkflowGraph.BaseNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.BootNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.DebugNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.ExecutionNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeConnection;
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
    public void testFromWorkflow() {
        // @formatter:off
//        List<BaseNode<?>> nodes = new LinkedList<>();
//        nodes.add(new BootNode().withId("0").withName("The Start"));
//        nodes.add(new DebugNode().withId("10").withParentId("0").withName("调试1"));
//        nodes.add(new LogicalNode().withId("20").withParentId("10").withName("条件1").withRelation(LogicalType.AND));
//        nodes.add(new ExecutionNode().withId("30").withParentId("20").withName("条件2"));
//        nodes.add(new LogicalNode().withId("40").withParentId("30").withName("条件3").withRelation(LogicalType.OR));
//        nodes.add(new EndNode().withId("100").withParentId("40").withName("The End"));
//
//        ExecutionGraph<?> graph = ExecutionGraph.from(nodes);
//        String json = toJSONString(graph);
//        System.out.println("=> " + json);
        // @formatter:on

        // ExecutionGraph<?> graph2 = parseJSON(json, ExecutionGraph.class);
        // System.out.println("=> " + graph2);
    }

    // TODO
    @Test
    public void testECommerceTradeWorkflow() {
        List<BaseNode<?>> nodes = new LinkedList<>();
        nodes.add(new BootNode().withId("0").withName("The Boot"));
        nodes.add(new DebugNode().withId("11").withName("模拟当前时间"));
        nodes.add(new LogicalNode().withId("21").withName("ALL_AND逻辑运算(10.1~10.8)").withRelation(LogicalType.ALL_AND));
        nodes.add(new LogicalNode().withId("31").withName("AND逻辑运算").withRelation(LogicalType.AND));
        nodes.add(new ExecutionNode().withId("41").withName("充值是否>=120元"));
        nodes.add(new LogicalNode().withId("42").withName("AND逻辑运算").withRelation(LogicalType.AND));
        nodes.add(new ExecutionNode().withId("51").withName("赠送库存是否<=100"));
        nodes.add(new ExecutionNode().withId("52").withName("赠送10元余额"));
        nodes.add(new LogicalNode().withId("32").withName("ALL_AND逻辑运算(10.5~10.8)").withRelation(LogicalType.AND));
        nodes.add(new ExecutionNode().withId("43").withName("充值是否>=50元"));
        nodes.add(new ExecutionNode().withId("44").withName("赠送20积分"));

        List<NodeConnection> collections = new LinkedList<>();
        collections.add(new NodeConnection("11", "0"));
        collections.add(new NodeConnection("21", "11"));
        collections.add(new NodeConnection("31", "21"));
        collections.add(new NodeConnection("32", "21"));
        collections.add(new NodeConnection("41", "31"));
        collections.add(new NodeConnection("42", "31"));
        collections.add(new NodeConnection("51", "42"));
        collections.add(new NodeConnection("52", "42"));
        collections.add(new NodeConnection("32", "21"));
        collections.add(new NodeConnection("43", "32"));
        collections.add(new NodeConnection("44", "32"));

        WorkflowGraph workflow = new WorkflowGraph(nodes, collections);

        ExecutionGraphParameter parameter = ExecutionGraphParameter.builder()
                .requestTime(currentTimeMillis())
                .traceId(UUID.randomUUID().toString())
                .debug(true)
                .workflowId("wf100101001")
                .parameter(emptyMap()) // TODO
                .build();
        ExecutionGraphContext context = new ExecutionGraphContext(parameter);
        ExecutionGraph<?> graph = ExecutionGraph.from(workflow, ctx -> ReturnState.TRUE);
        ExecutionGraphResult result = graph.apply(context);
        System.out.println("Executed trace text : " + context.getTraceText());
        System.out.println("    Executed result : " + toJSONString(result));
    }

}
