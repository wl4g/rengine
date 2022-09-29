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
import com.wl4g.rengine.common.model.EvaluationResult;

/**
 * {@link ScoreWorkflowExecution}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v3.0.0
 */
@Named(ScoreWorkflowExecution.BEAN_NAME)
@ApplicationScoped
public class ScoreWorkflowExecution extends BaseWorkflowExecution {

    public static final String BEAN_NAME = "scoreWorkflowExecution";

    @Override
    public EvaluationResult apply(Evaluation model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

}