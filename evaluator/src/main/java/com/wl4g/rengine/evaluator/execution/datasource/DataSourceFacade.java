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
package com.wl4g.rengine.evaluator.execution.datasource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.evaluator.execution.ExecutionConfig;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * {@link DataSourceFacade}
 * 
 * @author James Wong
 * @version 2022-12-14
 * @since v1.0.0
 */
public interface DataSourceFacade {

    public static interface DataSourceFacadeBuilder<C extends BuildConfig> {
        DataSourceFacade newInstnace(
                final @NotNull ExecutionConfig config,
                final @NotBlank String dataSourceName,
                final @NotNull C buildConfig);

        DataSourceType sourceType();
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static enum DataSourceType {
        MONGO("mongo");

        final String alias;
    }

    public static interface BuildConfig {
    }

}
