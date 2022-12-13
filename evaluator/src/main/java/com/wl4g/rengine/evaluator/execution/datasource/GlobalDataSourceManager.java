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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.wl4g.rengine.evaluator.execution.ExecutionConfig;
import com.wl4g.rengine.evaluator.execution.datasource.DataSourceFacade.BuildConfig;
import com.wl4g.rengine.evaluator.execution.datasource.DataSourceFacade.DataSourceFacadeBuilder;
import com.wl4g.rengine.evaluator.execution.datasource.DataSourceFacade.DataSourceType;
import com.wl4g.rengine.evaluator.repository.MongoRepository;

import io.quarkus.arc.All;
import lombok.CustomLog;

/**
 * {@link GlobalDataSourceManager}
 * 
 * @author James Wong
 * @version 2022-12-14
 * @since v1.0.0
 */
@CustomLog
@Singleton
public final class GlobalDataSourceManager {

    @NotNull
    @Inject
    ExecutionConfig config;

    @NotNull
    @Inject
    MongoRepository mongoRepository;

    @NotNull
    @All
    @Inject
    List<DataSourceFacadeBuilder<? extends BuildConfig>> builders;

    Map<DataSourceType, DataSourceFacadeBuilder<? extends BuildConfig>> builderMap = emptyMap();

    Map<DataSourceType, Map<String, DataSourceFacade>> sourceCaching = new ConcurrentHashMap<>(4);

    @PostConstruct
    public void init() {
        notEmptyOf(builders, "builders");
        this.builderMap = safeList(builders).stream().collect(toMap(b -> b.sourceType(), b -> b));
        log.debug("Registered to builders : {}", builderMap);
    }

    public <T extends DataSourceFacade> T loadDataSource(
            final @NotNull DataSourceType dataSourceType,
            final @NotBlank String dataSourceName) {
        notNullOf(dataSourceType, "dataSourceType");
        hasTextOf(dataSourceName, "dataSourceName");

        DataSourceFacade sourceFacade = null;
        synchronized (dataSourceType) {
            Map<String, DataSourceFacade> sources = sourceCaching.get(dataSourceType);
            if (isNull(sources)) {
                sources = new ConcurrentHashMap<>(4);
            }
            if (isNull(sourceFacade = sources.get(dataSourceName))) {
                final DataSourceFacadeBuilder<? extends BuildConfig> builder = notNull(builderMap.get(dataSourceType),
                        "Unsupported dataSource facade type for : %s/%s", dataSourceType, dataSourceName);
                // New init data source facade.
                sources.put(dataSourceName, sourceFacade = builder.newInstnace(config, dataSourceName,
                        findBuildConfig(dataSourceType, dataSourceName)));
            }
        }

        log.debug("Determined source facade : {} of : {}", sourceFacade, dataSourceName);
        return null;
    }

    <C extends BuildConfig> C findBuildConfig(
            final @NotNull DataSourceType dataSourceType,
            final @NotBlank String dataSourceName) {
        // TODO
        return null;
    }

}
