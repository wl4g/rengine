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

import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.entity.WorkflowGraph.BaseNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.BootNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.FailbackNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.LogicalType;
import com.wl4g.rengine.common.entity.WorkflowGraph.NodeConnection;
import com.wl4g.rengine.common.entity.WorkflowGraph.ProcessNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.RelationNode;
import com.wl4g.rengine.common.entity.WorkflowGraph.RunNode;
import com.wl4g.rengine.common.graph.ExecutionGraph.BaseOperator;
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
        nodes.add(new ProcessNode().withId("11").withName("预处理(如篡改当前时间以用于测试目的)").withRuleId(10100101L));
        nodes.add(new RelationNode().withId("21").withName("当前时间是否满足(10.1~10.8)").withRuleId(10100102L));
        nodes.add(new LogicalNode().withId("31").withName("ALL_AND逻辑运算").withLogical(LogicalType.ALL_AND));
        nodes.add(new LogicalNode().withId("41").withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new LogicalNode().withId("42").withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new RelationNode().withId("51").withName("充值是否>=120元").withRuleId(10100103L));
        nodes.add(new LogicalNode().withId("52").withName("AND逻辑运算").withLogical(LogicalType.AND));
        nodes.add(new RelationNode().withId("53").withName("当前时间是否满足(10.5~10.8)").withRuleId(10100104L));
        nodes.add(new RelationNode().withId("54").withName("充值是否>=50元").withRuleId(10100105L));
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

        WorkflowGraph workflow = new WorkflowGraph(nodes, collections);
        out.println("Workflow Nodes Json : " + toJSONString(workflow));

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

            out.println(format("current nodeId: %s@%s, lastResult : %s", nodeId, nodeType, toJSONString(ctx.getLastResult())));

            // 1. 在之后支持执行script的节点的规则代码中, 可使用 ctx.getLastResult() 获取前一个节点的返回值.
            // 2. 当前节点返回值会覆盖上一个节点的返回值.
            return new ExecutionGraphResult(ReturnState.TRUE, singletonMap("foo" + nodeId, "bar" + nodeId));
        });

        ExecutionGraph<?> graph = ExecutionGraph.from(workflow);
        ExecutionGraphResult result = graph.apply(context);

        out.println("-------------------------------------------------------");
        out.println("                           Final result : " + toJSONString(result));
        out.println("-------------------------------------------------------");
        out.println("Executed tracing info : \n" + context.asTraceText(true));

        assert nonNull(result);
        assert valueOf(result.getValueMap().get("foo63")).equals("bar63");
    }

}
