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
package com.wl4g.rengine.evaluator.execution.sdk;

import javax.annotation.Nullable;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.rengine.evaluator.execution.datasource.DataSourceFacade;
import com.wl4g.rengine.evaluator.execution.datasource.DataSourceFacade.DataSourceType;
import com.wl4g.rengine.evaluator.execution.datasource.GlobalDataSourceManager;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * {@link ScriptDataService}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@ToString
@AllArgsConstructor
public class ScriptDataService {

    private final GlobalDataSourceManager globalDataSourceManager;

    public @HostAccess.Export DataSourceFacade mongoService(final @Nullable String dataSourceName) {
        return globalDataSourceManager.loadDataSource(DataSourceType.MONGO, dataSourceName);
    }

    // public @HostAccess.Export PhoenixAggregatedService phoenixService() {
    // return mongoAggregatedService;
    // }

    // public @HostAccess.Export OpenTSDBAggregatedService openTSDBService() {
    // return opentsdbAggregatedService;
    // }

}
