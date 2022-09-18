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
package com.wl4g.rengine.common.bean;

import java.util.Date;

import javax.annotation.Nullable;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * {@link BeanBase}
 * 
 * @author James Wong
 * @version 2022-08-29
 * @since v3.0.0
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
public abstract class BeanBase {

    // 当作为输入参数时(即添加操作)隐藏，当作为返回参数时(即查询操作)不隐藏
    @Schema(hidden = false, accessMode = AccessMode.READ_ONLY)
    private Long id;

    private @NotNull @Min(0) @Max(1) Integer enabled;

    private @Nullable String remark;

    @Schema(hidden = false, accessMode = AccessMode.READ_ONLY)
    private String updateBy;

    @Schema(hidden = false, accessMode = AccessMode.READ_ONLY, example = "2022-09-15 10:23:43")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

}
