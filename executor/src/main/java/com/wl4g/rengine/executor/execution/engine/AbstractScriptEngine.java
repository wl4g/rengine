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

import org.graalvm.polyglot.proxy.ProxyObject;

import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.entity.UploadObject.ExtensionType;
import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.common.graph.ExecutionGraph.BaseOperator;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.execution.datasource.GlobalDataSourceManager;
import com.wl4g.rengine.executor.execution.sdk.ScriptContext;
import com.wl4g.rengine.executor.execution.sdk.ScriptContext.ScriptParameter;
import com.wl4g.rengine.executor.execution.sdk.ScriptDataService;
import com.wl4g.rengine.executor.execution.sdk.ScriptExecutor;
import com.wl4g.rengine.executor.execution.sdk.ScriptHttpClient;
import com.wl4g.rengine.executor.execution.sdk.ScriptLogger;
import com.wl4g.rengine.executor.execution.sdk.ScriptProcessClient;
import com.wl4g.rengine.executor.execution.sdk.ScriptResult;
import com.wl4g.rengine.executor.execution.sdk.ScriptSSHClient;
import com.wl4g.rengine.executor.execution.sdk.ScriptTCPClient;
import com.wl4g.rengine.executor.execution.sdk.extension.AES;
import com.wl4g.rengine.executor.execution.sdk.extension.Coding;
import com.wl4g.rengine.executor.execution.sdk.extension.Hashing;
import com.wl4g.rengine.executor.execution.sdk.extension.JSON;
import com.wl4g.rengine.executor.execution.sdk.extension.PrometheusParser;
import com.wl4g.rengine.executor.execution.sdk.extension.RSA;
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
    ExecutionConfig config;

    @Inject
    ExecutorMeterService meterService;

    @Inject
    MinioManager minioManager;

    @Inject
    GlobalDataSourceManager globalDataSourceManager;

    @Inject
    GlobalExecutorManager globalExecutorManager;

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @NotNull
    ScriptContext newScriptContext(final @NotNull ExecutionGraphContext graphContext) {
        notNullOf(graphContext, "graphContext");

        final ScriptDataService dataService = new ScriptDataService(defaultHttpClient, defaultSSHClient, defaultTCPClient,
                defaultProcessClient, globalDataSourceManager);
        final ExecutionGraphParameter parameter = graphContext.getParameter();
        final ScriptResult lastResult = isNull(graphContext.getLastResult()) ? null
                : new ScriptResult(ReturnState.isTrue(graphContext.getLastResult().getReturnState()),
                        graphContext.getLastResult().getValueMap());

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
                .lastResult(lastResult)
                .dataService(dataService)
                .logger(new ScriptLogger(minioManager))
                .executor(createScriptExecutor(
                        globalExecutorManager.getExecutor(parameter.getScenesCode(), config.perExecutorThreadPools())))
                // .attributes(ProxyObject.fromMap(emptyMap()))
                .build();
    }

    @NotNull
    abstract ScriptExecutor createScriptExecutor(final @NotNull SafeScheduledTaskPoolExecutor executor);

    // Notice: The handcode entrypoint function is 'process'
    public static final String DEFAULT_MAIN_FUNCTION = "process";
    public static final String DEFAULT_TMP_CACHE_ROOT_DIR = "/tmp/__rengine_script_files_caches";

    public static final Map<String, Class<?>> REGISTER_MEMBERS;

    final ScriptHttpClient defaultHttpClient = new ScriptHttpClient();
    final ScriptSSHClient defaultSSHClient = new ScriptSSHClient();
    final ScriptTCPClient defaultTCPClient = new ScriptTCPClient();
    final ScriptProcessClient defaultProcessClient = new ScriptProcessClient();

    static {
        final Map<String, Class<?>> bindingMembers = new HashMap<>();
        bindingMembers.put(ScriptResult.class.getSimpleName(), ScriptResult.class);
        bindingMembers.put(ScriptHttpClient.class.getSimpleName(), ScriptHttpClient.class);
        bindingMembers.put(ScriptSSHClient.class.getSimpleName(), ScriptSSHClient.class);
        bindingMembers.put(ScriptTCPClient.class.getSimpleName(), ScriptTCPClient.class);
        bindingMembers.put(ScriptProcessClient.class.getSimpleName(), ScriptProcessClient.class);
        bindingMembers.put(AES.class.getSimpleName(), AES.class);
        bindingMembers.put(RSA.class.getSimpleName(), RSA.class);
        bindingMembers.put(Coding.class.getSimpleName(), Coding.class);
        bindingMembers.put(Hashing.class.getSimpleName(), Hashing.class);
        bindingMembers.put(JSON.class.getSimpleName(), JSON.class);
        bindingMembers.put(RengineEvent.class.getSimpleName(), RengineEvent.class);
        bindingMembers.put(PrometheusParser.class.getSimpleName(), PrometheusParser.class);
        REGISTER_MEMBERS = unmodifiableMap(bindingMembers);
    }

}
