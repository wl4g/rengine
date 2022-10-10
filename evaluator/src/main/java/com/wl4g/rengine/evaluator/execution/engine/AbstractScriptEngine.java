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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.evaluator.execution.engine;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.graalvm.polyglot.proxy.ProxyObject;

import com.google.common.collect.Lists;
import com.wl4g.rengine.common.entity.Scenes;
import com.wl4g.rengine.common.entity.UploadObject.ExtensionType;
import com.wl4g.rengine.common.entity.UploadObject.UploadType;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptEventLocation;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptEventSource;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext.ScriptRengineEvent;
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
 * @since v3.0.0
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

    protected @NotNull List<ObjectResource> loadScriptResources(
            @NotNull UploadType type,
            @NotNull Evaluation model,
            boolean useCache) {
        notNullOf(type, "uploadType");
        notNullOf(model, "evaluation");
        log.debug("Loading script by {}, {} ...", type, model);

        List<ObjectResource> scripts = Lists.newArrayList();

        // Gets scenes/workflow/rules/uploads information.
        final Scenes scenes = jobService.loadScenesFull(model.getScenesCode());
        notNull(scenes, "Unable to find scenes '%s'", model.getScenesCode());

        // Add upload object script dependencies all by scenes.workflow.rules
        safeList(scenes.getWorkflow().getRules()).forEach(rule -> {
            safeList(rule.getUploads()).forEach(upload -> {
                try {
                    scripts.add(minioManager.loadObject(UploadType.of(upload.getUploadType()), upload.getObjectPrefix(),
                            model.getScenesCode(), ExtensionType.of(upload.getExtension()).isBinary(), useCache));
                } catch (Exception e) {
                    log.error(format("Unable to load dependency script from MinIO: %s", upload.getObjectPrefix()), e);
                    throw new IllegalStateException(e); // fast-fail:Stay-Strongly-Consistent
                }
            });
        });

        return scripts;
    }

    protected @NotNull ScriptContext newScriptContext(Evaluation model) {
        // TODO dynamic setup more parameters

        ScriptRengineEvent event = new ScriptRengineEvent("generic_device_temp_warning",
                ScriptEventSource.builder()
                        .time(currentTimeMillis())
                        .principals(singletonList("admin"))
                        .location(ScriptEventLocation.builder().zipcode("20500").build())
                        .build(),
                "A serious alarm occurs when the device temperature is greater than 52℃", singletonMap("objId", "1010012"));

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("objId", "1010012");
        attributes.put("remark", "The test js call to java ...");

        return ScriptContext.builder()
                .id("100101")
                .type("iot_warning")
                .args(model.getScripting().getArgs())
                .event(event)
                .attributes(ProxyObject.fromMap(attributes))
                .minioManager(minioManager)
                // .logger(new ScriptLogger(minioManager))
                .dataService(new ScriptDataService(aggregationService))
                .defaultHttpClient(new ScriptHttpClient())
                .build();
    }

}
