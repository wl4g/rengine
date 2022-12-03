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
import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

import java.util.Map;

import javax.inject.Inject;

import com.wl4g.rengine.common.entity.Rule;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.common.entity.Workflow;
import com.wl4g.rengine.common.graph.ExecutionGraph;
import com.wl4g.rengine.common.graph.ExecutionGraph.IRunOperator;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.common.graph.ExecutionGraphResult;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult;
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
public class DefaultWorkflowExecution implements WorkflowExecution {

    // @Inject
    // GroovyScriptEngine groovyScriptEngine;

    @Inject
    GraalJSScriptEngine graalJSScriptEngine;

    @Override
    public EvaluationResult execute(final Evaluation evaluation, final Scenes scenes) {
        final Workflow workflow = scenes.getWorkflow();
        final IEngine engine = getEngine(workflow.getRuleEngine());

        final ExecutionGraphParameter parameter = ExecutionGraphParameter.builder()
                .requestTime(currentTimeMillis())
                .traceId(evaluation.getRequestId())
                .trace(true)
                .debug(true)
                .workflowId(valueOf(scenes.getWorkflowId()))
                .args(evaluation.getArgs())
                .build();

        final Map<String, Rule> ruleMap = safeList(workflow.getRules()).stream().collect(toMap(r -> valueOf(r.getId()), r -> r));

        final ExecutionGraphContext context = new ExecutionGraphContext(parameter, ctx -> {
            final Rule rule = ruleMap.get(((IRunOperator) ctx.getCurrentNode()).getRuleId());
            final ScriptResult result = engine.execute(ctx, evaluation, scenes, rule);
            if (nonNull(result)) {
                return ReturnState.of(result.getState());
            }
            return ReturnState.FALSE;
        });

        final ExecutionGraph<?> graph = ExecutionGraph.from(workflow.getGraph());
        final ExecutionGraphResult result = graph.apply(context);

        // TODO 支持批量 evaluation 返回
        return EvaluationResult.builder()
                .results(singletonList(ResultDescription.builder()
                        .scenesCode(evaluation.getScenesCode())
                        .valueMap(result.getValueMap())
                        .build()))
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
