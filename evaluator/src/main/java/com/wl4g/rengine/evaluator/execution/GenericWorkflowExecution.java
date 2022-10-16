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
package com.wl4g.rengine.evaluator.execution;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationEngine;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.common.model.EvaluationResult.GenericEvaluationResult;
import com.wl4g.rengine.evaluator.execution.engine.IEngine;
import com.wl4g.rengine.evaluator.execution.sdk.ScriptResult;

/**
 * {@link GenericWorkflowExecution}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 */
@Named(GenericWorkflowExecution.BEAN_NAME)
@ApplicationScoped
public class GenericWorkflowExecution extends BaseWorkflowExecution {

    public static final String BEAN_NAME = "genericWorkflowExecution";

    @Override
    public EvaluationResult apply(Evaluation model) {
        // TODO parse rules execution graph tree from workflow
        IEngine engine = getEngine(EvaluationEngine.valueOf(model.getEngine()));

        // TODO re-definition result bean?
        ScriptResult result = engine.execute(model);

        return GenericEvaluationResult.builder().result(result).build();
    }

}
