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
package com.wl4g.rengine.executor.execution.sdk.datasource;

import java.io.Closeable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourcePropertiesBase;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.executor.execution.EngineConfig;
import com.wl4g.rengine.executor.minio.MinioConfig;
import com.wl4g.rengine.executor.service.ServiceConfig;

/**
 * {@link DataSourceFacade}
 * 
 * @author James Wong
 * @date 2022-12-14
 * @since v1.0.0
 */
public interface DataSourceFacade extends Closeable {

    EngineConfig getEngineConfig();

    String getDataSourceName();

    public static interface DataSourceFacadeBuilder {
        DataSourceFacade newInstnace(
                final @NotNull EngineConfig engineConfig,
                final @NotNull ServiceConfig serviceConfig,
                final @NotNull MinioConfig minioConfig,
                final @NotNull GlobalDataSourceManager globalDataSourceManager,
                final @NotBlank String dataSourceName,
                final @NotNull DataSourcePropertiesBase dataSourceProperties);

        DataSourceType type();
    }
}
