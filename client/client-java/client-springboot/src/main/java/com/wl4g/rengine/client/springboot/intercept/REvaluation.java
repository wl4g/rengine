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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.client.springboot.intercept;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

import com.wl4g.rengine.client.core.RengineClient.DefaultFailback;
import com.wl4g.rengine.common.model.Evaluation;
import com.wl4g.rengine.common.model.EvaluationResult;

/**
 * {@link REvaluation}
 * 
 * @author James Wong
 * @version 2022-11-02
 * @since v1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface REvaluation {

    /**
     * Evaluation scenes code.
     * 
     * @return
     */
    String scenesCode();

    /**
     * That is, the maximum execution time, and the user determines the
     * acceptable maximum execution time according to actual needs. Returns
     * immediately if evaluation/feature acquisition and computation times-out.
     * 
     * @see {@link com.wl4g.rengine.common.model.Evaluation#getTimeout()}
     */
    long timeout() default Evaluation.DEFAULT_TIMEOUT;

    /**
     * This attribute is used to control the behavior when the calculation
     * fails. If it is false, if there is an error in the feature acquisition,
     * such as the database is not found, the calculation times out, etc., an
     * error message is returned. If it is true, when an error occurs, it will
     * still return the same response content as when it is correct, but the
     * value of the evaluation result/feature will be given a default value, and
     * the corresponding error code will be set. </br>
     * 
     * @see {@link com.wl4g.rengine.common.model.Evaluation#getBestEffort()}
     */
    boolean bestEffort() default Evaluation.DEFAULT_BESTEFFORT;

    /**
     * Evaluation input parameters template.
     * 
     * @return
     */
    String paramsTemplate() default "";

    /**
     * When evaluation failure to callback handler.
     * 
     * @return
     */
    Class<? extends Function<Throwable, EvaluationResult>> failback() default DefaultFailback.class;

}