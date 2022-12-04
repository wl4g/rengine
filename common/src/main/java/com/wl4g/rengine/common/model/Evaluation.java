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
 * See the License for the specific engine governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.common.model;

import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link Evaluation}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 */
@Getter
@Setter
@SuperBuilder
@ToString
@NoArgsConstructor
public class Evaluation extends EvaluationBase {

    @NotBlank
    String clientId;

    @NotBlank
    String clientSecret;

    @NotBlank
    String scenesCode;

    /**
     * That is, the maximum execution time, and the user determines the
     * acceptable maximum execution time according to actual needs. Returns
     * immediately if evaluation/feature acquisition and computation times-out.
     */
    @NotNull
    @Min(1)
    @Default
    Long timeout = DEFAULT_TIMEOUT;

    /**
     * This attribute is used to control the behavior when the calculation
     * fails. If it is false, if there is an error in the feature acquisition,
     * such as the database is not found, the calculation times out, etc., an
     * error message is returned. If it is true, when an error occurs, it will
     * still return the same response content as when it is correct, but the
     * value of the evaluation result/feature will be given a default value, and
     * the corresponding error code will be set. </br>
     * refer to: https://mp.weixin.qq.com/s/UG4VJ3HuzcBhjLcmtVpLFw
     */
    @NotNull
    @Default
    Boolean bestEffort = DEFAULT_BESTEFFORT;

    /**
     * The arguments required for evaluating models/features, such as
     * page_view_count_userid, which requires key arguments such as
     * BuyerId+UserId to execute it.
     */
    @Nullable
    Map<String, Object> args;

    public static final long DEFAULT_TIMEOUT = 3_000L;
    public static final boolean DEFAULT_BESTEFFORT = false;
}
