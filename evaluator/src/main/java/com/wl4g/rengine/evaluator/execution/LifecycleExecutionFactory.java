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

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;

import com.wl4g.rengine.common.model.EvaluationEngine;
import com.wl4g.rengine.common.model.EvaluationKind;

/**
 * {@link LifecycleExecutionFactory}
 * 
 * @author James Wong
 * @version 2022-09-17
 * @since v1.0.0
 * @see https://github.com/google/guice/wiki/Motivation
 */
@Singleton
public class LifecycleExecutionFactory {

    // @Inject
    // BeanManager beanManager;

    public IExecution getExecution(EvaluationKind kind, EvaluationEngine engine) {
        switch (kind) {
        case DEFAULT:
        default:
            return getBean(DefaultWorkflowExecution.class);
        }
    }

    private <T> T getBean(Class<T> subtype, Annotation... qualifiers) {
        // Set<Bean<?>> beans = beanManager.getBeans(beanType, qualifiers);
        // if (!beans.isEmpty()) {
        // return (T) beans.iterator().next();
        // }
        T bean = CDI.current().select(subtype, qualifiers).get();
        if (nonNull(bean)) {
            return bean;
        }
        throw new IllegalStateException(format("Could not obtain bean by '%s, %s'", subtype, qualifiers));
    }

}
