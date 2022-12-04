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
package com.wl4g.rengine.evaluator.execution.engine;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.proxy.ProxyObject;

import com.wl4g.rengine.common.entity.Rule;
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.common.entity.UploadObject.ExtensionType;
import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.common.graph.ExecutionGraphContext;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptDataService;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptHttpClient;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService;
import com.wl4g.rengine.evaluator.minio.MinioManager;
import com.wl4g.rengine.evaluator.minio.MinioManager.ObjectResource;
import com.wl4g.rengine.evaluator.service.AggregationService;
import com.wl4g.rengine.evaluator.service.JobService;

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
    EvaluatorMeterService meterService;

    @Inject
    MinioManager minioManager;

    @Inject
    JobService jobService;

    @Inject
    AggregationService aggregationService;

    protected @NotNull List<ObjectResource> loadScriptResources(@NotNull Scenes scenes, @NotNull Rule rule, boolean useCache) {
        notNullOf(rule, "rule");
        log.debug("Loading script {} by scenesCode: {}, ruleId: {}", scenes.getScenesCode(), rule.getId());

        // Add upload object script dependencies all by scenes.workflow.rules
        return safeList(rule.getUploads()).stream().map(upload -> {
            try {
                return minioManager.loadObject(UploadType.of(upload.getUploadType()), upload.getObjectPrefix(),
                        scenes.getScenesCode(), ExtensionType.of(upload.getExtension()).isBinary(), useCache);
            } catch (Exception e) {
                log.error(format("Unable to load dependency script from MinIO: %s", upload.getObjectPrefix()), e);
                throw new IllegalStateException(e); // fast-fail:Stay-Strongly-Consistent
            }
        }).collect(toList());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected @NotNull ScriptContext newScriptContext(
            @NotNull final ExecutionGraphContext graphContext,
            @NotNull final Evaluation evaluation) {
        notNullOf(graphContext, "graphContext");
        notNullOf(evaluation, "evaluation");

        return ScriptContext.builder()
                .id(graphContext.getCurrentNode().getId())
                .type("iot_warning")
                .args(ProxyObject.fromMap((Map) evaluation.getArgs()))
                // .attributes(ProxyObject.fromMap(attributes))
                .minioManager(minioManager)
                .dataService(new ScriptDataService(aggregationService))
                .defaultHttpClient(new ScriptHttpClient())
                .build();
    }

    // TODO handcode for 'process'
    public static final String DEFAULT_MAIN_FUNCTION = "process";

}
