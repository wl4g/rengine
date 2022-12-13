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
package com.wl4g.rengine.evaluator.execution;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.entity.Scenes.ScenesWrapper;
import com.wl4g.rengine.common.entity.Workflow.WorkflowGraphWrapper;
import com.wl4g.rengine.common.entity.Workflow.WorkflowWrapper;
import com.wl4g.rengine.common.graph.ExecutionGraph;
import com.wl4g.rengine.common.graph.ExecutionGraph.IRunOperator;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.common.graph.ExecutionGraphResult;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult.ResultDescription;
import com.wl4g.rengine.evaluator.execution.engine.GraalJSScriptEngine;
import com.wl4g.rengine.evaluator.execution.engine.IEngine;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptResult;

/**
 * {@link DefaultWorkflowExecution}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 */
@Singleton
public class DefaultWorkflowExecution implements WorkflowExecution {

    // @Inject
    // GroovyScriptEngine groovyScriptEngine;

    @Inject
    GraalJSScriptEngine graalJSScriptEngine;

    @Override
    public ResultDescription execute(final Evaluation evaluation, final ScenesWrapper scenes) {
        final WorkflowWrapper workflow = scenes.getEffectivePriorityWorkflow();
        final WorkflowGraphWrapper workflowGraph = workflow.getEffectiveLatestGraph();
        final IEngine engine = getEngine(workflow.getEngine());

        final ExecutionGraphParameter parameter = ExecutionGraphParameter.builder()
                .requestTime(currentTimeMillis())
                .traceId(evaluation.getRequestId())
                .trace(true)
                .clientId(evaluation.getClientId())
                .scenesCode(valueOf(scenes.getScenesCode()))
                .workflowId(valueOf(workflow.getId()))
                .args(evaluation.getArgs())
                .build();

        final Map<String, RuleWrapper> ruleMap = safeList(workflowGraph.getRules()).stream()
                .collect(toMap(r -> valueOf(r.getId()), r -> r));
        final ExecutionGraphContext context = new ExecutionGraphContext(parameter, ctx -> {
            final String ruleId = ((IRunOperator) ctx.getCurrentNode()).getRuleId();

            final RuleWrapper rule = Assert2.notNull(ruleMap.get(ruleId),
                    "The rule '%s' is missing. please check workflow graph rules configuration.", ruleId);

            final ScriptResult result = engine.execute(ctx, rule);
            if (nonNull(result)) {
                return new ExecutionGraphResult(ReturnState.of(result.getState()), result.getValueMap());
            }

            return new ExecutionGraphResult(ReturnState.FALSE);
        });

        final ExecutionGraph<?> graph = ExecutionGraph.from(workflowGraph);
        final ExecutionGraphResult result = graph.apply(context);
        return ResultDescription.builder()
                .scenesCode(scenes.getScenesCode())
                .success(true)
                .valueMap(result.getValueMap())
                .build();
    }

    protected IEngine getEngine(RuleEngine kind) {
        switch (kind) {
        // case GROOVY:
        // return groovyScriptEngine;
        case JS:
        default:
            return graalJSScriptEngine;
        }
    }

}
