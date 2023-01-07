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

import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.executor.execution.sdk.datasource.DataSourceFacade;
import com.wl4g.rengine.executor.execution.sdk.datasource.GlobalDataSourceManager;
import com.wl4g.rengine.executor.execution.sdk.notifier.GlobalMessageNotifierManager;
import com.wl4g.rengine.executor.execution.sdk.notifier.ScriptMessageNotifier;

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
    final ScriptProcessClient defaultProcessClient;
    final ScriptRedisLockClient defaultRedisLockClient;
    final GlobalDataSourceManager globalDataSourceManager;
    final GlobalMessageNotifierManager globalMessageNotifierManager;

    // --- SDK Utility Clients. ----

    public @HostAccess.Export ScriptHttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }

    public @HostAccess.Export ScriptSSHClient getDefaultSSHClient() {
        return defaultSSHClient;
    }

    public @HostAccess.Export ScriptTCPClient getDefaultTCPClient() {
        return defaultTCPClient;
    }

    public @HostAccess.Export ScriptProcessClient getDefaultProcessClient() {
        return defaultProcessClient;
    }

    public @HostAccess.Export ScriptRedisLockClient getDefaultRedisLockClient() {
        return defaultRedisLockClient;
    }

    // --- SDK DataSource Services. ----

    public @HostAccess.Export DataSourceFacade getMongoService(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.loadDataSource(DataSourceType.MONGO, dataSourceName);
    }

    public @HostAccess.Export DataSourceFacade getJDBCService(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.loadDataSource(DataSourceType.JDBC, dataSourceName);
    }

    public @HostAccess.Export DataSourceFacade getRedisService(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.loadDataSource(DataSourceType.REDIS, dataSourceName);
    }

    public @HostAccess.Export DataSourceFacade getKafkaService(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.loadDataSource(DataSourceType.KAFKA, dataSourceName);
    }

    // public @HostAccess.Export DataSourceFacade phoenixService(final @Nullable
    // String dataSourceName) {
    // return mongoAggregatedService;
    // }

    // public @HostAccess.Export DataSourceFacade openTSDBService(final
    // @Nullable String dataSourceName) {
    // return opentsdbAggregatedService;
    // }

    // --- SDK Message Notifiers. ----

    public @HostAccess.Export ScriptMessageNotifier getMessageNotifier(final @NotBlank String notifierType) {
        return globalMessageNotifierManager.getMessageNotifier(NotifierKind.valueOf(notifierType));
    }

}