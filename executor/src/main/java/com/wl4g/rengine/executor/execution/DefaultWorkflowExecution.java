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
package com.wl4g.rengine.executor.execution;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.isInstanceOf;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.entity.Workflow.WorkflowWrapper;
import com.wl4g.rengine.common.entity.graph.FlinkCepGraph;
import com.wl4g.rengine.common.entity.graph.StandardGraph;
import com.wl4g.rengine.common.entity.graph.WorkflowGraph.WorkflowGraphWrapper;
import com.wl4g.rengine.common.graph.ExecutionGraph;
import com.wl4g.rengine.common.graph.ExecutionGraph.IRunOperator;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.common.graph.ExecutionGraphResult;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteResult.ResultDescription;
import com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine;
import com.wl4g.rengine.executor.execution.engine.GraalPythonScriptEngine;
import com.wl4g.rengine.executor.execution.engine.GraalRScriptEngine;
import com.wl4g.rengine.executor.execution.engine.GraalRubyScriptEngine;
import com.wl4g.rengine.executor.execution.engine.IScriptEngine;
import com.wl4g.rengine.executor.execution.sdk.ScriptResult;

import lombok.CustomLog;

/**
 * {@link DefaultWorkflowExecution}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 */
@CustomLog
@Singleton
public class DefaultWorkflowExecution implements WorkflowExecution {

    // @Inject
    // GroovyScriptEngine groovyScriptEngine;

    @Inject
    GraalJSScriptEngine graalJSScriptEngine;

    @Inject
    GraalPythonScriptEngine graalPythonScriptEngine;

    @Inject
    GraalRScriptEngine graalRScriptEngine;

    @Inject
    GraalRubyScriptEngine graalRubyScriptEngine;

    @Override
    public ResultDescription execute(
            final @NotNull ExecuteRequest executeRequest,
            final @NotNull WorkflowWrapper workflow,
            final boolean usingCache) {
        workflow.validate();
        final WorkflowGraphWrapper workflowGraph = workflow.getEffectiveLatestGraph();
        isInstanceOf(StandardGraph.class, workflowGraph.getDetails(),
                "Unsupported workflow graph, this executor currently only supported for execution of '%s'. e.g '%s' should to submit execution by flink job.",
                StandardGraph.class.getSimpleName(), FlinkCepGraph.class.getSimpleName());

        try {
            final ExecutionGraphParameter parameter = ExecutionGraphParameter.builder()
                    .requestTime(currentTimeMillis())
                    .traceId(executeRequest.getRequestId())
                    .trace(executeRequest.isTrace())
                    .clientId(executeRequest.getClientId())
                    .workflowId(workflowGraph.getWorkflowId())
                    .attributes(workflowGraph.getAttributes())
                    .args(executeRequest.getArgs())
                    .build();

            final Map<Long, RuleWrapper> ruleMap = safeList(workflowGraph.getRules()).stream()
                    .collect(toMap(r -> r.getId(), r -> r));

            final ExecutionGraphContext graphContext = new ExecutionGraphContext(parameter, ctx -> {
                final Long ruleId = ((IRunOperator) ctx.getCurrentNode()).getRuleId();

                final RuleWrapper rule = Assert2.notNull(ruleMap.get(ruleId),
                        "Rule '%s' is missing. please check workflow graph rules configuration.", ruleId);

                final IScriptEngine scriptEngine = getScriptEngine(rule.getEngine());

                final ScriptResult result = scriptEngine.execute(ctx, rule, usingCache);
                if (nonNull(result)) {
                    return new ExecutionGraphResult(ReturnState.of(result.getState()), result.getValueMap());
                }

                return new ExecutionGraphResult(ReturnState.FALSE);
            });

            final ExecutionGraph<?> graph = ExecutionGraph.from((StandardGraph) workflowGraph.getDetails());
            final ExecutionGraphResult result = graph.apply(graphContext);

            return ResultDescription.builder().success(true).valueMap(result.getValueMap()).build();
        } catch (Throwable ex) {
            log.warn(format("Failed to execution workflow graph for workflowId: %s", workflowGraph.getWorkflowId()), ex);

            return ResultDescription.builder()
                    .success(false)
                    // Only the outermost reason is returned.
                    .reason(format("Failed to execution workflow graph of '%s', reason: %s", workflowGraph.getId(),
                            ex.getMessage()))
                    .build();
        }
    }

    protected IScriptEngine getScriptEngine(RuleEngine engine) {
        switch (engine) {
        // case GROOVY:
        // return groovyScriptEngine;
        case JS:
            return graalJSScriptEngine;
        case PYTHON:
            return graalPythonScriptEngine;
        case R:
            return graalRScriptEngine;
        case RUBY:
            return graalRubyScriptEngine;
        default:
            throw new Error(format("No support script engine '%s'", engine));
        }
    }

}
