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
package com.wl4g.rengine.evaluator.execution;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.wl4g.rengine.common.entity.Rule.RuleEngine;

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

    @Inject
    DefaultWorkflowExecution workflowExecution;

    public WorkflowExecution getExecution(RuleEngine engine) {
        switch (engine) {
        default:
            // return getBean(DefaultWorkflowExecution.class);
            return workflowExecution;
        }
    }

// @formatter:off
//    private <T> T getBean(Class<T> subtype, Annotation... qualifiers) {
//        // Set<Bean<?>> beans = beanManager.getBeans(beanType, qualifiers);
//        // if (!beans.isEmpty()) {
//        // return (T) beans.iterator().next();
//        // }
//        // 必须有其他地方注入引用，这里才能获取，否则报错 UnsatisfiedResolution
//        T bean = CDI.current().select(subtype).get();
//        if (nonNull(bean)) {
//            return bean;
//        }
//        throw new IllegalStateException(format("Could not obtain bean by '%s, %s'", subtype, qualifiers));
//    }
// @formatter:on

}
