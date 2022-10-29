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
package com.wl4g.rengine.common.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link EvaluationResult}
 * 
 * @author James Wong
 * @version 2022-09-21
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
public class EvaluationResult {

    private @NotNull @Default Integer errorCount = 0;
    private @NotNull @Default List<ResultDescription> results = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    @SuperBuilder
    @NoArgsConstructor
    public static class ResultDescription {
        // TODO 之后演进为通用特征平台后, 此字段表示为: feature
        // 参见: https://mp.weixin.qq.com/s/UG4VJ3HuzcBhjLcmtVpLFw
        private @NotNull String node; // 暂定义为执行失败的 workflow 节点 ID
        private @NotNull Boolean success;
        private @NotBlank String value;
    }

}
