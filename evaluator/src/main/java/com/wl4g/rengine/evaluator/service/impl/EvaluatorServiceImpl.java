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
package com.wl4g.rengine.evaluator.service.impl;

import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsName.evaluation_failure;
import static com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsName.evaluation_success;
import static com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsName.evaluation_total;
import static java.lang.String.format;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.web.rest.RespBase.RetCode;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationEngine;
import com.wl4g.rengine.common.model.EvaluationKind;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.evaluator.execution.IExecution;
import com.wl4g.rengine.evaluator.execution.LifecycleExecutionFactory;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsTag;
import com.wl4g.rengine.evaluator.service.EvaluatorService;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EvaluatorServiceImpl}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v3.0.0
 * @see https://quarkus.io/guides/resteasy-reactive#asyncreactive-support
 */
@Slf4j
// @ApplicationScoped
@Singleton
public class EvaluatorServiceImpl implements EvaluatorService {

    @Inject
    EvaluatorMeterService meterService;

    @Inject
    LifecycleExecutionFactory lifecycleExecutionFactory;

    @Override
    public Uni<RespBase<EvaluationResult>> evaluate(Evaluation model) {
        return Uni.createFrom().item(() -> {
            RespBase<EvaluationResult> resp = RespBase.<EvaluationResult> create();

            // Buried-point: total evaluation.
            meterService
                    .counter(evaluation_total.getName(), evaluation_total.getHelp(), MetricsTag.KIND, model.getKind(),
                            MetricsTag.ENGINE, model.getEngine(), MetricsTag.SCENESCODE, model.getScenesCode(),
                            MetricsTag.SERVICE, model.getService())
                    .increment();

            try {
                resp.withData(doEvaluation(model));

                // Buried-point: success evaluation.
                meterService
                        .counter(evaluation_success.getName(), evaluation_success.getHelp(), MetricsTag.KIND, model.getKind(),
                                MetricsTag.ENGINE, model.getEngine(), MetricsTag.SCENESCODE, model.getScenesCode(),
                                MetricsTag.SERVICE, model.getService())
                        .increment();
            } catch (Exception e) {
                String errmsg = format("Failed to evaluation with kind(%s), engine(%s). - %s", model.getKind(), model.getEngine(),
                        e.getMessage());
                log.error(errmsg, e);

                // Buried-point: failed evaluation.
                meterService
                        .counter(evaluation_failure.getName(), evaluation_failure.getHelp(), MetricsTag.KIND, model.getKind(),
                                MetricsTag.ENGINE, model.getEngine(), MetricsTag.SCENESCODE, model.getScenesCode(),
                                MetricsTag.SERVICE, model.getService())
                        .increment();
                resp.withCode(RetCode.SYS_ERR).withMessage(errmsg);
            }

            return resp;
        });
    }

    private EvaluationResult doEvaluation(Evaluation model) {
        IExecution execution = lifecycleExecutionFactory.getExecution(EvaluationKind.valueOf(model.getKind()),
                EvaluationEngine.valueOf(model.getEngine()));
        notNull(execution, "Could not load execution engine via %s", model.getEngine());
        return execution.apply(model);
    }

}
