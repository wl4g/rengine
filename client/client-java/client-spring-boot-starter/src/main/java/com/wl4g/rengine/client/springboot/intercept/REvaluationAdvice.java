/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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
package com.wl4g.rengine.client.springboot.intercept;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * {@link REvaluationAdvice}
 * 
 * @author James Wong
 * @date 2022-11-02
 * @since v1.0.0
 */
@Aspect
public class REvaluationAdvice {

    private final REvaluationHandler<REvaluation> handler;

    public REvaluationAdvice(REvaluationHandler<REvaluation> handler) {
        this.handler = notNullOf(handler, "handler");
    }

    @Pointcut("@annotation(com.wl4g.rengine.client.springboot.intercept.REvaluation)")
    private void pointcut() {
    }

    /**
     * AOP section intercept, controller interface required safe evaluation.
     * 
     * @param jp
     * @param annotation
     * @return
     * @throws Throwable
     */
    @Around("pointcut() && @annotation(annotation)")
    public Object intercept(ProceedingJoinPoint jp, REvaluation annotation) throws Throwable {
        return handler.doIntercept(jp, annotation);
    }

}