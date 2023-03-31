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

import static com.wl4g.infra.common.collection.CollectionUtils2.ensureMap;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.proxy.ProxyObject;

import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.entity.sys.UploadObject.ExtensionType;
import com.wl4g.rengine.common.entity.sys.UploadObject.UploadType;
import com.wl4g.rengine.common.graph.ExecutionGraph.BaseOperator;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.common.graph.ExecutionGraphResult.ReturnState;
import com.wl4g.rengine.common.util.ScriptEngineUtil;
import com.wl4g.rengine.executor.execution.EngineConfig;
import com.wl4g.rengine.executor.execution.engine.internal.GlobalSdkExecutorManager;
import com.wl4g.rengine.executor.execution.sdk.ScriptContext;
import com.wl4g.rengine.executor.execution.sdk.ScriptContext.ScriptParameter;
import com.wl4g.rengine.executor.execution.sdk.ScriptDataService;
import com.wl4g.rengine.executor.execution.sdk.ScriptExecutor;
import com.wl4g.rengine.executor.execution.sdk.ScriptHttpClient;
//import com.wl4g.rengine.executor.execution.sdk.ScriptLogger;
import com.wl4g.rengine.executor.execution.sdk.ScriptProcessClient;
import com.wl4g.rengine.executor.execution.sdk.ScriptRedisLockClient;
import com.wl4g.rengine.executor.execution.sdk.ScriptResult;
import com.wl4g.rengine.executor.execution.sdk.ScriptSSHClient;
import com.wl4g.rengine.executor.execution.sdk.ScriptTCPClient;
import com.wl4g.rengine.executor.execution.sdk.datasource.GlobalDataSourceManager;
import com.wl4g.rengine.executor.execution.sdk.notifier.GlobalMessageNotifierManager;
import com.wl4g.rengine.executor.execution.sdk.tools.AES;
import com.wl4g.rengine.executor.execution.sdk.tools.Assert;
import com.wl4g.rengine.executor.execution.sdk.tools.Coding;
import com.wl4g.rengine.executor.execution.sdk.tools.DateHolder;
import com.wl4g.rengine.executor.execution.sdk.tools.Files;
import com.wl4g.rengine.executor.execution.sdk.tools.Hashing;
import com.wl4g.rengine.executor.execution.sdk.tools.JSON;
import com.wl4g.rengine.executor.execution.sdk.tools.PrometheusParser;
import com.wl4g.rengine.executor.execution.sdk.tools.RSA;
import com.wl4g.rengine.executor.execution.sdk.tools.RandomHolder;
//import com.wl4g.rengine.executor.execution.sdk.tools.RandomHolder;
import com.wl4g.rengine.executor.execution.sdk.tools.RengineEvent;
import com.wl4g.rengine.executor.execution.sdk.tools.UUID;
//import com.wl4g.rengine.executor.execution.sdk.tools.UUID;
import com.wl4g.rengine.executor.meter.RengineExecutorMeterService;
import com.wl4g.rengine.executor.minio.MinioConfig;
import com.wl4g.rengine.executor.minio.MinioManager;
import com.wl4g.rengine.executor.minio.MinioManager.ObjectResource;

import io.quarkus.redis.datasource.RedisDataSource;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link AbstractScriptEngine}
 * 
 * @author James Wong
 * @version 2022-09-22
 * @since v1.0.0
 */
@Slf4j
public abstract class AbstractScriptEngine implements IScriptEngine {

    @Inject
    EngineConfig engineConfig;

    @Inject
    MinioConfig minioConfig;

    @Inject
    RengineExecutorMeterService meterService;

    @Inject
    RedisDataSource redisDS;

    @Inject
    MinioManager minioManager;

    @Inject
    GlobalDataSourceManager globalDataSourceManager;

    @Inject
    GlobalMessageNotifierManager globalMessageNotifierManager;

    @Inject
    GlobalSdkExecutorManager globalSdkExecutorManager;

    ScriptHttpClient defaultHttpClient;
    ScriptSSHClient defaultSSHClient;
    ScriptTCPClient defaultTCPClient;
    ScriptProcessClient defaultProcessClient;
    ScriptRedisLockClient defaultRedisLockClient;

    protected void init() {
        this.defaultHttpClient = new ScriptHttpClient();
        this.defaultSSHClient = new ScriptSSHClient();
        this.defaultTCPClient = new ScriptTCPClient();
        this.defaultProcessClient = new ScriptProcessClient();
        this.defaultRedisLockClient = new ScriptRedisLockClient(redisDS);
    }

    @NotNull
    protected List<ObjectResource> loadScriptResources(
            final @NotNull Long workflowId,
            final @NotNull RuleWrapper rule,
            boolean usingCache) {
        notNullOf(workflowId, "workflowId");
        notNullOf(rule, "rule");
        log.debug("Loading script {} by workflowId: {}, ruleId: {}", workflowId, rule.getId());

        // Add upload object script dependencies all by scenes.workflow.rules
        final var ruleScript = rule.getEffectiveLatestScript();
        final var scriptObjects = safeList(ruleScript.getUploads()).stream().map(upload -> {
            // Notice: There is no need to concat the tenant ID here, because
            // the objectPrefix field saved in the DB is the full path except
            // the bucket.
            // see:com.wl4g.rengine.service.impl.UploadServiceImpl#apply()
            final String objectPrefix = upload.getObjectPrefix();
            try {
                return minioManager.loadObject(upload.getId(), UploadType.of(upload.getUploadType()), objectPrefix, workflowId,
                        ExtensionType.of(upload.getExtension()).isBinary(),
                        usingCache ? engineConfig.executorScriptCachedExpire() : -1);
            } catch (Throwable ex) {
                log.error(format("Unable to load dependency script from MinIO: %s", objectPrefix), ex);
                throw new IllegalStateException(ex); // fast-fail:Stay-Strongly-Consistent
            }
        }).collect(toList());

        // Notice: According to the graal context eval mechanism, if there is a
        // function with the same name, the latter will overwrite the previous
        // eval function, so make sure that the main script is the last one.
        Collections.sort(scriptObjects, (o1, o2) -> o1.getUploadId() == ruleScript.getEntrypointUploadId() ? 1 : -1);

        return scriptObjects;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @NotNull
    protected ScriptContext newScriptContext(final @NotNull ExecutionGraphContext graphContext) {
        notNullOf(graphContext, "graphContext");

        final ScriptDataService dataService = new ScriptDataService(defaultHttpClient, defaultSSHClient, defaultTCPClient,
                defaultProcessClient, defaultRedisLockClient, globalDataSourceManager, globalMessageNotifierManager);

        final ExecutionGraphParameter parameter = graphContext.getParameter();

        final ScriptResult lastResult = isNull(graphContext.getLastResult()) ? null
                : new ScriptResult(ReturnState.isTrue(graphContext.getLastResult().getReturnState()),
                        graphContext.getLastResult().getValueMap());

        final SafeScheduledTaskPoolExecutor executor = globalSdkExecutorManager.getExecutor(parameter.getWorkflowId(),
                engineConfig.executor().perExecutorThreadPools());

        return ScriptContext.builder()
                .id(graphContext.getCurrentNode().getId())
                .type(((BaseOperator<?>) graphContext.getCurrentNode()).getType())
                .parameter(ScriptParameter.builder()
                        .requestTime(parameter.getRequestTime())
                        .clientId(parameter.getClientId())
                        .traceId(parameter.getTraceId())
                        .trace(parameter.isTrace())
                        .workflowId(parameter.getWorkflowId())
                        .args(ProxyObject.fromMap((Map) parameter.getArgs()))
                        .build())
                .lastResult(lastResult)
                .dataService(dataService)
                // @formatter:off
                // .logger(new ScriptLogger(parameter.getScenesCode(),parameter.getWorkflowId(), minioManager))
                // @formatter:on
                .executor(createScriptExecutor(parameter, executor))
                // Add the workflow graph extension attributes.
                .attributes(ProxyObject.fromMap(ensureMap(parameter.getAttributes())))
                .build();
    }

    @NotNull
    protected abstract ScriptExecutor createScriptExecutor(
            final @NotNull ExecutionGraphParameter parameter,
            final @NotNull SafeScheduledTaskPoolExecutor executor);

    public static String buildScriptLogFilePattern(
            final @NotBlank String scriptLogBaseDir,
            final @Nullable Map<String, Object> metadata,
            final boolean isStdErr) {
        final Long workflowId = (Long) safeMap(metadata).get(KEY_WORKFLOW_ID);
        return ScriptEngineUtil.buildScriptLogFilePattern(scriptLogBaseDir, workflowId, isStdErr);
    }

    public static final String KEY_WORKFLOW_ID = AbstractScriptEngine.class.getSimpleName().concat(".WORKFLOW_ID");
    public static final Map<String, Object> REGISTER_MEMBERS;

    static {
        final Map<String, Object> bindingMembers = new HashMap<>();
        bindingMembers.put(ScriptResult.class.getSimpleName(), ScriptResult.class);
        bindingMembers.put(ScriptHttpClient.class.getSimpleName(), ScriptHttpClient.class);
        bindingMembers.put(ScriptSSHClient.class.getSimpleName(), ScriptSSHClient.class);
        bindingMembers.put(ScriptTCPClient.class.getSimpleName(), ScriptTCPClient.class);
        bindingMembers.put(ScriptProcessClient.class.getSimpleName(), ScriptProcessClient.class);
        bindingMembers.put(ScriptRedisLockClient.class.getSimpleName(), ScriptRedisLockClient.class);
        bindingMembers.put(RengineEvent.class.getSimpleName(), RengineEvent.class);
        // Notice: If you want to statically call the method of the java sdk
        // uiltity class in JavaScript, you must register the sdk tool class as
        // an instance member, otherwise you can only use the method of new
        // MyTool().myMethod() to call.
        bindingMembers.put(Assert.class.getSimpleName(), Assert.DEFAULT);
        bindingMembers.put(AES.class.getSimpleName(), AES.DEFAULT);
        bindingMembers.put(RSA.class.getSimpleName(), RSA.DEFAULT);
        bindingMembers.put(Coding.class.getSimpleName(), Coding.DEFAULT);
        bindingMembers.put(Hashing.class.getSimpleName(), Hashing.DEFAULT);
        bindingMembers.put(JSON.class.getSimpleName(), JSON.DEFAULT);
        bindingMembers.put(DateHolder.class.getSimpleName(), DateHolder.DEFAULT);
        bindingMembers.put(PrometheusParser.class.getSimpleName(), PrometheusParser.DEFAULT);
        bindingMembers.put(RandomHolder.class.getSimpleName(), RandomHolder.DEFAULT);
        bindingMembers.put(UUID.class.getSimpleName(), UUID.DEFAULT);
        bindingMembers.put(Files.class.getSimpleName(), Files.DEFAULT);
        REGISTER_MEMBERS = unmodifiableMap(bindingMembers);
    }

}
