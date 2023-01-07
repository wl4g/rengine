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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.IteratorUtils;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.wl4g.rengine.common.constants.RengineConstants.MongoCollectionDefinition;
import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourcePropertiesBase;
import com.wl4g.rengine.common.entity.DataSourceProperties.DataSourceType;
import com.wl4g.rengine.common.exception.ConfigRengineException;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.execution.sdk.datasource.DataSourceFacade.DataSourceFacadeBuilder;
import com.wl4g.rengine.executor.metrics.ExecutorMeterService;
import com.wl4g.rengine.executor.repository.MongoRepository;

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
    ExecutorMeterService meterService;

    @NotNull
    @Inject
    MongoRepository mongoRepository;

    @NotNull
    @All
    @Inject
    List<DataSourceFacadeBuilder> builders;

    Map<DataSourceType, DataSourceFacadeBuilder> builderMap = emptyMap();

    Map<DataSourceType, Map<String, DataSourceFacade>> dataSourceCaches = new ConcurrentHashMap<>(4);

    @PostConstruct
    public void init() {
        notEmptyOf(builders, "builders");
        this.builderMap = safeList(builders).stream().collect(toMap(b -> b.type(), b -> b));
        log.debug("Registered to builders : {}", builderMap);
    }

    void destroy(@Observes @BeforeDestroyed(ApplicationScoped.class) ServletContext init) {
        safeMap(dataSourceCaches).values().stream().flatMap(e -> e.values().stream()).forEach(ds -> {
            try {
                ds.close();
            } catch (IOException e) {
                log.error(format("Unable to closing data source of %s", ds.getDataSourceName()), e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public <T extends DataSourceFacade> T loadDataSource(
            final @NotNull DataSourceType dataSourceType,
            final @NotBlank String dataSourceName) {
        notNullOf(dataSourceType, "dataSourceType");
        hasTextOf(dataSourceName, "dataSourceName");

        Map<String, DataSourceFacade> dataSourceFacades = dataSourceCaches.get(dataSourceType);
        if (isNull(dataSourceFacades)) {
            synchronized (dataSourceType) {
                dataSourceFacades = dataSourceCaches.get(dataSourceType);
                if (isNull(dataSourceFacades)) {
                    dataSourceCaches.put(dataSourceType, dataSourceFacades = new ConcurrentHashMap<>(4));
                }
            }
        }

        DataSourceFacade dataSourceFacade = dataSourceFacades.get(dataSourceName);
        if (isNull(dataSourceFacade)) {
            synchronized (dataSourceName) {
                dataSourceFacade = dataSourceFacades.get(dataSourceName);
                if (isNull(dataSourceFacade)) {
                    final DataSourceFacadeBuilder builder = notNull(builderMap.get(dataSourceType),
                            "Unsupported to data source facade handler type of : %s/%s", dataSourceType, dataSourceName);
                    // New init data source facade.
                    dataSourceFacades.put(dataSourceName, dataSourceFacade = builder.newInstnace(config, dataSourceName,
                            findDataSourceProperties(dataSourceType, dataSourceName)));
                }
            }
        }

        log.debug("Determined source facade : {} of : {}", dataSourceFacade, dataSourceName);
        return (T) dataSourceFacade;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    DataSourcePropertiesBase findDataSourceProperties(
            final @NotNull DataSourceType dataSourceType,
            final @NotBlank String dataSourceName) {
        notNullOf(dataSourceType, "dataSourceType");
        hasTextOf(dataSourceName, "dataSourceName");

        final MongoCollection<Document> collection = mongoRepository.getCollection(MongoCollectionDefinition.DATASOURCES);

        try (final MongoCursor<DataSourceProperties> cursor = collection
                .find(Filters.and(Filters.eq("properties.type", dataSourceType), Filters.eq("name", dataSourceName)))
                .batchSize(2)
                .limit(2)
                .map(doc -> BsonEntitySerializers.fromDocument(doc, DataSourceProperties.class))
                .iterator();) {

            // Check should have only one.
            final List<DataSourceProperties> dss = safeList(IteratorUtils.toList(cursor));
            if (dss.isEmpty()) {
                throw new ConfigRengineException(
                        format("Unable found data source identifier of '%s (%s)'", dataSourceName, dataSourceType));
            } else if (dss.size() > 1) {
                throw new ConfigRengineException(
                        format("The multiple data sources of the same type and name were found of %s, %s", dataSourceType,
                                dataSourceName));
            }
            final DataSourcePropertiesBase properties = dss.get(0).getProperties();
            if (isNull(properties)) {
                throw new ConfigRengineException(
                        format("The data source configuration properties is missing. %s, %s", dataSourceType, dataSourceName));
            }

            return properties.validate();
        } catch (Throwable e) {
            throw new RengineException(e);
        }
    }

}
