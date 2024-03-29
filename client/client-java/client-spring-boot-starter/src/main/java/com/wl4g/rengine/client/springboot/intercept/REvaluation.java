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
import com.wl4g.rengine.client.core.RengineClient.FailbackInfo;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;

/**
 * for example:
 * 
 * <pre>
 * &#64;Service
 * public class MyOrderServiceImpl implements MyOrderService {
 * 
 *     &#64;REvaluation(scenesCode = "${scenes_configs.createOrder}", bestEffort = true, paramsTemplate = "{{userId=#0,goodId=#1}}")
 *     &#64;Override
 *     Map<String, String> create(String userId, String goodId, String address, Integer count) {
 *         // Some logical ...
 *     }
 * }
 * </pre>
 * 
 * @author James Wong
 * @date 2022-11-02
 * @since v1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Documented
public @interface REvaluation {

    /**
     * WorkflowExecuteRequest scenes code.
     * 
     * @return
     */
    String scenesCode();

    /**
     * That is, the maximum execution time, and the user determines the
     * acceptable maximum execution time according to actual needs. Returns
     * immediately if evaluation/feature acquisition and computation times-out.
     * 
     * @see {@link com.wl4g.rengine.common.model.WorkflowExecuteRequest#getTimeout()}
     */
    long timeout() default WorkflowExecuteRequest.DEFAULT_TIMEOUT;

    /**
     * This attribute is used to control the behavior when the calculation
     * fails. If it is false, if there is an error in the feature acquisition,
     * such as the database is not found, the calculation times out, etc., an
     * error message is returned. If it is true, when an error occurs, it will
     * still return the same response content as when it is correct, but the
     * value of the evaluation result/feature will be given a default value, and
     * the corresponding error code will be set. </br>
     * 
     * @see {@link com.wl4g.rengine.common.model.WorkflowExecuteRequest#getBestEffort()}
     */
    boolean bestEffort() default WorkflowExecuteRequest.DEFAULT_BESTEFFORT;

    /**
     * WorkflowExecuteRequest to parameters template.
     * 
     * <p>
     * for example: {{ userId=#0.userId, goodId=#0.goodId, count=#1 }}
     * </p>
     * 
     * @see {@link DefaultREvaluationHandler#parseParamsTemplate(java.util.List, String)}
     * @return
     */
    String paramsTemplate();

    /**
     * The assertion expression of the execute result value map.
     * 
     * <p>
     * for example: #{riskScore > 50}
     * </p>
     * 
     * @return
     */
    String assertSpel();

    /**
     * When the result of the assertion fails, an error message is thrown.
     * 
     * @return
     */
    String assertErrmsg() default "The operation denied by the security manager";

    /**
     * When evaluation failure to callback handler.
     * 
     * @return
     */
    Class<? extends Function<FailbackInfo, WorkflowExecuteResult>> failback() default DefaultFailback.class;

}