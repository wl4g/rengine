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

import static com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsName.evaluation_total;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService;
import com.wl4g.rengine.evaluator.metrics.EvaluatorMeterService.MetricsTag;
import com.wl4g.rengine.evaluator.service.EvaluatorService;

import io.smallrye.mutiny.Uni;

/**
 * {@link EvaluatorServiceImpl}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v3.0.0
 * @see https://quarkus.io/guides/resteasy-reactive#asyncreactive-support
 */
// @ApplicationScoped
@Singleton
public class EvaluatorServiceImpl implements EvaluatorService {

    private @Inject EvaluatorMeterService meterService;

    @Override
    public Uni<RespBase<EvaluationResult>> evaluate(Evaluation model) {
        return Uni.createFrom().item(() -> {
            RespBase<EvaluationResult> resp = RespBase.<EvaluationResult> create();

            // Add total evaluation metrics.
            meterService
                    .counter(evaluation_total.getName(), evaluation_total.getHelp(), MetricsTag.SERVICE, model.getService(),
                            MetricsTag.SCENES, model.getScenes())
                    .increment();

            // TODO Auto-generated method stub
            // ...

            return resp;
        });
    }

}