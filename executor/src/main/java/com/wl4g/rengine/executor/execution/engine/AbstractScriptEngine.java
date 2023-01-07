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
import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.common.entity.Rule.RuleWrapper;
import com.wl4g.rengine.common.entity.UploadObject.ExtensionType;
import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.common.graph.ExecutionGraphParameter;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
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
import com.wl4g.rengine.executor.execution.sdk.tools.Coding;
import com.wl4g.rengine.executor.execution.sdk.tools.DateHolder;
import com.wl4g.rengine.executor.execution.sdk.tools.Hashing;
import com.wl4g.rengine.executor.execution.sdk.tools.JSON;
import com.wl4g.rengine.executor.execution.sdk.tools.PrometheusParser;
import com.wl4g.rengine.executor.execution.sdk.tools.RSA;
import com.wl4g.rengine.executor.execution.sdk.tools.RengineEvent;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService;
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
public abstract class AbstractScriptEngine implements IEngine {

    @Inject
    RedisDataSource redisDS;

    @Inject
    ExecutionConfig config;

    @Inject
    ExecutorMeterService meterService;

    @Inject
    MinioManager minioManager;

    @Inject
    GlobalDataSourceManager globalDataSourceManager;

    @Inject
    GlobalMessageNotifierManager globalMessageNotifierManager;

    @Inject
    GlobalExecutorManager globalExecutorManager;

    ScriptHttpClient defaultHttpClient;
    ScriptSSHClient defaultSSHClient;
    ScriptTCPClient defaultTCPClient;
    ScriptProcessClient defaultProcessClient;
    ScriptRedisLockClient defaultRedisLockClient;

    @PostConstruct
    void init() {
        this.defaultHttpClient = new ScriptHttpClient();
        this.defaultSSHClient = new ScriptSSHClient();
        this.defaultTCPClient = new ScriptTCPClient();
        this.defaultProcessClient = new ScriptProcessClient();
        this.defaultRedisLockClient = new ScriptRedisLockClient(redisDS);
    }

    @NotNull
    protected List<ObjectResource> loadScriptResources(@NotBlank String scenesCode, @NotNull RuleWrapper rule, boolean useCache) {
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

    @NotNull
    protected abstract ScriptExecutor createScriptExecutor(
            final @NotNull ExecutionGraphParameter parameter,
            final @NotNull SafeScheduledTaskPoolExecutor executor);

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
        bindingMembers.put(DateHolder.class.getSimpleName(), DateHolder.getInstance());
        bindingMembers.put(Coding.class.getSimpleName(), Coding.getInstance());
        bindingMembers.put(Hashing.class.getSimpleName(), Hashing.getInstance());
        bindingMembers.put(AES.class.getSimpleName(), AES.getInstance());
        bindingMembers.put(RSA.class.getSimpleName(), RSA.getInstance());
        bindingMembers.put(JSON.class.getSimpleName(), JSON.getInstance());
        bindingMembers.put(PrometheusParser.class.getSimpleName(), PrometheusParser.getInstance());
        REGISTER_MEMBERS = unmodifiableMap(bindingMembers);
    }

}