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
package com.wl4g.rengine.executor.execution.engine;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.entity.UploadObject.ExtensionType;
import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.common.graph.ExecutionGraph.BaseOperator;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.common.graph.ExecutionGraphResult;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;
import com.wl4g.rengine.executor.execution.datasource.GlobalDataSourceManager;
import com.wl4g.rengine.executor.execution.sdk.ScriptContext;
import com.wl4g.rengine.executor.execution.sdk.ScriptContext.ScriptParameter;
import com.wl4g.rengine.executor.execution.sdk.ScriptDataService;
import com.wl4g.rengine.executor.execution.sdk.ScriptHttpClient;
import com.wl4g.rengine.executor.execution.sdk.ScriptResult;
import com.wl4g.rengine.executor.execution.sdk.ScriptSSHClient;
import com.wl4g.rengine.executor.execution.sdk.ScriptTCPClient;
import com.wl4g.rengine.executor.execution.sdk.extension.JSON;
import com.wl4g.rengine.executor.execution.sdk.extension.PrometheusParser;
import com.wl4g.rengine.executor.execution.sdk.extension.RengineEvent;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService;
import com.wl4g.rengine.executor.minio.MinioManager;
import com.wl4g.rengine.executor.minio.MinioManager.ObjectResource;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link AbstractScriptEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@Slf4j
public abstract class AbstractScriptEngine implements IEngine {

    @Inject
    ExecutorMeterService meterService;

    @Inject
    MinioManager minioManager;

    @Inject
    GlobalDataSourceManager globalDataSourceManager;

    @NotNull
    List<ObjectResource> loadScriptResources(@NotBlank String scenesCode, @NotNull RuleWrapper rule, boolean useCache) {
        notNullOf(rule, "rule");
        log.debug("Loading script {} by scenesCode: {}, ruleId: {}", scenesCode, rule.getId());

        // Add upload object script dependencies all by scenes.workflow.rules
        return safeList(rule.getEffectiveLatestScript().getUploads()).stream().map(upload -> {
            try {
                return minioManager.loadObject(UploadType.of(upload.getUploadType()), upload.getObjectPrefix(), scenesCode,
                        ExtensionType.of(upload.getExtension()).isBinary(), useCache);
            } catch (Exception e) {
                log.error(format("Unable to load dependency script from MinIO: %s", upload.getObjectPrefix()), e);
                throw new IllegalStateException(e); // fast-fail:Stay-Strongly-Consistent
            }
        }).collect(toList());
    }

    void bindingMembers(final @NotNull Value bindings) {
        // Try not to bind implicit objects, let users create new objects by
        // self or get default objects from the graal context.
        REGISTER_MEMBERS.forEach((name, member) -> bindings.putMember(name, member));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @NotNull
    ScriptContext newScriptContext(@NotNull final ExecutionGraphContext graphContext) {
        notNullOf(graphContext, "graphContext");

        final ScriptDataService dataService = new ScriptDataService(defaultHttpClient, defaultSSHClient, defaultTCPClient,
                globalDataSourceManager);
        final ExecutionGraphParameter parameter = graphContext.getParameter();
        final ExecutionGraphResult result = graphContext.getLastResult();

        return ScriptContext.builder()
                .id(graphContext.getCurrentNode().getId())
                .type(((BaseOperator<?>) graphContext.getCurrentNode()).getType())
                .parameter(ScriptParameter.builder()
                        .requestTime(parameter.getRequestTime())
                        .clientId(graphContext.getParameter().getClientId())
                        .traceId(graphContext.getParameter().getTraceId())
                        .trace(graphContext.getParameter().isTrace())
                        .scenesCode(graphContext.getParameter().getScenesCode())
                        .workflowId(graphContext.getParameter().getWorkflowId())
                        .args(ProxyObject.fromMap((Map) graphContext.getParameter().getArgs()))
                        .build())
                .lastResult(isNull(result) ? null
                        : new ScriptResult(ReturnState.isTrue(result.getReturnState()), result.getValueMap()))
                .minioManager(minioManager)
                .dataService(dataService)
                // .attributes(ProxyObject.fromMap(attributes))
                .build();
    }

    // Handcode default entrypoint funciton for 'process'
    public static final String DEFAULT_MAIN_FUNCTION = "process";
    public static final Map<String, Class<?>> REGISTER_MEMBERS;

    final ScriptHttpClient defaultHttpClient = new ScriptHttpClient();
    final ScriptSSHClient defaultSSHClient = new ScriptSSHClient();
    final ScriptTCPClient defaultTCPClient = new ScriptTCPClient();

    static {
        final Map<String, Class<?>> bindingMembers = new HashMap<>();
        bindingMembers.put(ScriptResult.class.getSimpleName(), ScriptResult.class);
        bindingMembers.put(ScriptHttpClient.class.getSimpleName(), ScriptHttpClient.class);
        bindingMembers.put(ScriptSSHClient.class.getSimpleName(), ScriptSSHClient.class);
        bindingMembers.put(ScriptTCPClient.class.getSimpleName(), ScriptTCPClient.class);
        bindingMembers.put(JSON.class.getSimpleName(), JSON.class);
        bindingMembers.put(RengineEvent.class.getSimpleName(), RengineEvent.class);
        bindingMembers.put(PrometheusParser.class.getSimpleName(), PrometheusParser.class);
        REGISTER_MEMBERS = unmodifiableMap(bindingMembers);
    }

}
