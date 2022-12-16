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
package com.wl4g.rengine.executor.execution.sdk;

import javax.validation.constraints.NotBlank;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.executor.execution.datasource.DataSourceFacade;
import com.wl4g.rengine.executor.execution.datasource.GlobalDataSourceManager;

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

    final ScriptHttpClient defaultHttpClient;
    final ScriptSSHClient defaultSSHClient;
    final ScriptTCPClient defaultTCPClient;
    final GlobalDataSourceManager globalDataSourceManager;

    public @HostAccess.Export ScriptHttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }

    public @HostAccess.Export ScriptSSHClient getDefaultSSHClient() {
        return defaultSSHClient;
    }

    public @HostAccess.Export ScriptTCPClient getDefaultTCPClient() {
        return defaultTCPClient;
    }

    public @HostAccess.Export DataSourceFacade getMongoService(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.loadDataSource(DataSourceType.MONGO, dataSourceName);
    }

    public @HostAccess.Export DataSourceFacade getJDBCService(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.loadDataSource(DataSourceType.JDBC, dataSourceName);
    }

    public @HostAccess.Export DataSourceFacade getRedisClusterService(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.loadDataSource(DataSourceType.REDIS_CLUSTER, dataSourceName);
    }

    // public @HostAccess.Export DataSourceFacade phoenixService(final @Nullable
    // String dataSourceName) {
    // return mongoAggregatedService;
    // }

    // public @HostAccess.Export DataSourceFacade openTSDBService(final
    // @Nullable String dataSourceName) {
    // return opentsdbAggregatedService;
    // }

}
