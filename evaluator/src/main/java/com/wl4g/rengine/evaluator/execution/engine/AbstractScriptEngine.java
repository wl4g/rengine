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

import java.util.List;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.wl4g.rengine.common.bean.UploadObject.UploadType;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptContext;
import com.wl4g.rengine.evaluator.minio.MinioManager;

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
    MinioManager minioManager;

    protected List<String> loadScripts(@NotNull UploadType type, Evaluation model) {
        log.debug("Loading script by {}, {} ...", type, model);

        List<String> scripts = Lists.newArrayList();

        // TODO each: find rules object dependencies by scenes
        try {
            scripts.add(minioManager.getObjectAsText(type, "test.js"));
        } catch (Exception e) {
            log.error("Failed to load dependency scripting from MinIO.", e);
        }

        return scripts;
    }

    protected ScriptContext newRengineContext(Evaluation model) {
        ScriptContext context = ScriptContext.builder().build();
        // TODO add more context parameters
        context.getArgs().addAll(model.getScripting().getArgs());
        return context;
    }

    // protected EvaluationResult doEvaluation(Evaluation model) {
    // switch (EvaluationKind.valueOf(model.getKind())) {
    // case GENERIC:
    // return doEvaluationWithGeneric(model);
    // case SCORE:
    // return doEvaluationWithScore(model);
    // default:
    // throw new Error("No should be here");
    // }
    // }
    //
    // protected EvaluationResult doEvaluationWithGeneric(Evaluation model) {
    // // TODO Auto-generated method stub
    // // ...
    //
    // return GenericEvaluationResult.builder().result(null).build();
    // }
    //
    // protected EvaluationResult doEvaluationWithScore(Evaluation model) {
    // // TODO Auto-generated method stub
    // // ...
    //
    // return ScoreEvaluationResult.builder().score(89f).build();
    // }

}
