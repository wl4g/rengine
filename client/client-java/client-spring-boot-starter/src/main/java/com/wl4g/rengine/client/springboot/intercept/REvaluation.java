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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

import com.wl4g.rengine.client.core.RengineClient.DefaultFailback;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;

/**
 * for example:
 * 
 * <pre>
 * &#64;Service
 * class MyOrderServiceImpl implements MyOrderService {
 * 
 *     &#64;REvaluation(scenesCode = "${scenes_configs.createOrder}", bestEffort = true, paramsTemplate = "{{userId=#0,goodId=#1}}")
 *     &#64;Override
 *     Map<String, String> create2(String userId, String goodId, String address, Integer count) {
 *         // Some logical ...
 *     }
 * }
 * </pre>
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
     * ExecuteRequest scenes code.
     * 
     * @return
     */
    String scenesCode();

    /**
     * That is, the maximum execution time, and the user determines the
     * acceptable maximum execution time according to actual needs. Returns
     * immediately if evaluation/feature acquisition and computation times-out.
     * 
     * @see {@link com.wl4g.rengine.common.model.ExecuteRequest#getTimeout()}
     */
    long timeout() default ExecuteRequest.DEFAULT_TIMEOUT;

    /**
     * This attribute is used to control the behavior when the calculation
     * fails. If it is false, if there is an error in the feature acquisition,
     * such as the database is not found, the calculation times out, etc., an
     * error message is returned. If it is true, when an error occurs, it will
     * still return the same response content as when it is correct, but the
     * value of the evaluation result/feature will be given a default value, and
     * the corresponding error code will be set. </br>
     * 
     * @see {@link com.wl4g.rengine.common.model.ExecuteRequest#getBestEffort()}
     */
    boolean bestEffort() default ExecuteRequest.DEFAULT_BESTEFFORT;

    /**
     * ExecuteRequest to parameters template.
     * 
     * <p>
     * for example: {{ userId=#0.userId, goodId=#0.goodId, count=#1 }}
     * </p>
     * 
     * @see {@link DefaultREvaluationHandler#parseParamsTemplate(java.util.List, String)}
     * @return
     */
    String paramsTemplate() default "";

    /**
     * When evaluation failure to callback handler.
     * 
     * @return
     */
    Class<? extends Function<Throwable, ExecuteResult>> failback() default DefaultFailback.class;

}