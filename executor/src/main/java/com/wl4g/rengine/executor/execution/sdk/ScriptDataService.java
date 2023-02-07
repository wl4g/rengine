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

import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import javax.validation.constraints.NotBlank;

import org.graalvm.polyglot.HostAccess;

import com.wl4g.infra.common.notification.MessageNotifier.NotifierKind;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.executor.execution.sdk.datasource.DataSourceFacade;
import com.wl4g.rengine.executor.execution.sdk.datasource.GlobalDataSourceManager;
import com.wl4g.rengine.executor.execution.sdk.notifier.GlobalMessageNotifierManager;
import com.wl4g.rengine.executor.execution.sdk.notifier.ScriptMessageNotifier;

import lombok.ToString;

/**
 * {@link ScriptDataService}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@ToString
public class ScriptDataService {
    final ScriptHttpClient defaultHttpClient;
    final ScriptSSHClient defaultSSHClient;
    final ScriptTCPClient defaultTCPClient;
    final ScriptProcessClient defaultProcessClient;
    final ScriptRedisLockClient defaultRedisLockClient;
    final GlobalDataSourceManager globalDataSourceManager;
    final GlobalMessageNotifierManager globalMessageNotifierManager;

    public ScriptDataService(ScriptHttpClient defaultHttpClient, ScriptSSHClient defaultSSHClient,
            ScriptTCPClient defaultTCPClient, ScriptProcessClient defaultProcessClient,
            ScriptRedisLockClient defaultRedisLockClient, GlobalDataSourceManager globalDataSourceManager,
            GlobalMessageNotifierManager globalMessageNotifierManager) {
        this.defaultHttpClient = notNullOf(defaultHttpClient, "defaultHttpClient");
        this.defaultSSHClient = notNullOf(defaultSSHClient, "defaultSSHClient");
        this.defaultTCPClient = notNullOf(defaultTCPClient, "defaultTCPClient");
        this.defaultProcessClient = notNullOf(defaultProcessClient, "defaultProcessClient");
        this.defaultRedisLockClient = notNullOf(defaultRedisLockClient, "defaultRedisLockClient");
        this.globalDataSourceManager = notNullOf(globalDataSourceManager, "globalDataSourceManager");
        this.globalMessageNotifierManager = notNullOf(globalMessageNotifierManager, "globalMessageNotifierManager");
    }

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

    public @HostAccess.Export DataSourceFacade obtainMongoDSFacade(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.obtain(DataSourceType.MONGO, dataSourceName);
    }

    public @HostAccess.Export DataSourceFacade obtainJdbcDSFacade(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.obtain(DataSourceType.JDBC, dataSourceName);
    }

    public @HostAccess.Export DataSourceFacade obtainRedisDSFacade(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.obtain(DataSourceType.REDIS, dataSourceName);
    }

    public @HostAccess.Export DataSourceFacade obtainKafkaDSFacade(final @NotBlank String dataSourceName) {
        return globalDataSourceManager.obtain(DataSourceType.KAFKA, dataSourceName);
    }

    // public @HostAccess.Export DataSourceFacade obtainPhoenixFacade(final
    // @Nullable String dataSourceName) {
    // return mongoAggregatedService;
    // }

    // public @HostAccess.Export DataSourceFacade obtainOpenTSDBFacade(final
    // @Nullable String dataSourceName) {
    // return opentsdbAggregatedService;
    // }

    // --- SDK Message Notifiers. ----

    public @HostAccess.Export ScriptMessageNotifier obtainNotifier(final @NotBlank String notifierType) {
        return globalMessageNotifierManager.obtain(NotifierKind.valueOf(notifierType));
    }

}
