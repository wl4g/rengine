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
package com.wl4g.rengine.collector.context;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.EnvironmentUtil.getStringProperty;
import static java.lang.String.format;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.SystemUtils;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.JobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.google.common.io.Resources;
import com.wl4g.infra.common.resource.StreamResource;
import com.wl4g.infra.common.resource.resolver.ClassPathResourcePatternResolver;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.infra.common.yaml.YamlUtils;
import com.wl4g.rengine.collector.config.CollectorProperties;
import com.wl4g.rengine.collector.config.CollectorProperties.ScrapeJobProperties;
import com.wl4g.rengine.collector.config.CollectorYamlConstructor;
import com.wl4g.rengine.collector.config.ElasticJobBootstrapBuilder;
import com.wl4g.rengine.collector.job.CollectJobExecutor.JobParamBase;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * {@linkplain @ApplicationScoped} vs {@linkplain @Singleton} see:
 * https://quarkus.io/guides/cdi#applicationscoped-and-singleton-look-very-similar-which-one-should-i-choose-for-my-quarkus-application
 * 
 * {@linkplain @Singleton}: Better performance because there is no client proxy.
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://quarkus.io/guides/lifecycle#listening-for-startup-and-shutdown-events
 */
@Slf4j
@Singleton
public class CollectorApplicationInitializer {

    @Inject
    @NotNull
    Validator validator;

    CollectorProperties collectorConfig;

    ClientEventBusConfig eventBusConfig;

    ExecutorService oneoffExecutor;

    void onStart(@Observes StartupEvent event, @ConfigProperty(name = "quarkus.application.name") String appName) {
        RespBase.ErrorPromptMessageBuilder.setPrompt(appName.substring(Math.max(appName.lastIndexOf("-") + 1, 0)));

        // Initializing collector job
        initJobConfiguration();
        initJobScheduling();

        // Initializing eventBus
        initEventBusConfiguration();
        initEventBus();
    }

    void onStop(@Observes ShutdownEvent event) {
        log.info("The application is stopping...");
    }

    /**
     * Initialzing load collector job collectorConfiguration.
     */
    void initJobConfiguration() {
        try {
            final ClassPathResourcePatternResolver resolver = new ClassPathResourcePatternResolver();
            // Load base collectorConfiguration.
            final StreamResource baseConf = resolver.getResources(CONF_COLLECTOR_LOCATION)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            format("Not found collector configuration for '%s'", CONF_COLLECTOR_LOCATION)));
            this.collectorConfig = YamlUtils.parse(Resources.toString(baseConf.getURL(), UTF_8), new CollectorYamlConstructor(),
                    CONF_COLLECTOR_PREFIX, CollectorProperties.class);

            // Load extension collectorConfiguration.
            final Set<StreamResource> extConfs = resolver.getResources(CONF_COLLECTOR_LOCATION_EXTENSION);
            for (StreamResource r : extConfs) {
                // Merge with extension scrape collectorConfiguration.
                final CollectorProperties extensionConfig = YamlUtils.parse(Resources.toString(r.getURL(), UTF_8),
                        new CollectorYamlConstructor(), CONF_COLLECTOR_PREFIX, CollectorProperties.class);
                for (ScrapeJobProperties<JobParamBase> scrapeConf : extensionConfig.getScrapeJobConfigs()) {
                    if (!collectorConfig.getScrapeJobConfigs().contains(scrapeConf)) {
                        collectorConfig.getScrapeJobConfigs().add(scrapeConf);
                    }
                }
            }

            this.collectorConfig.init(validator);
        } catch (Throwable e) {
            throw new IllegalStateException(
                    format("Failed to load resolve collector configuration. - classpath: %s", SystemUtils.JAVA_CLASS_PATH), e);
        }
    }

    void initJobScheduling() {
        try {
            // @formatter:off
            // log.info("Initializing tracing collectorConfiguration ...");
            // List<TracingConfiguration<?>> tracingConfiguration = singletonList(new TracingConfiguration<>("RDB", null));
            // @formatter:on

            // Initialzing one-off oneoffExecutor
            String prefix = getClass().getSimpleName();
            final AtomicInteger counter = new AtomicInteger(0);
            this.oneoffExecutor = Executors.newFixedThreadPool(
                    (collectorConfig.getGlobalScrapeJobConfig().getOneOffJobThreadPools() <= 1) ? 1
                            : collectorConfig.getGlobalScrapeJobConfig().getOneOffJobThreadPools(),
                    r -> new Thread(r, prefix.concat("-" + counter.incrementAndGet())));

            log.info("Initializing coordinator registry center ...");
            CoordinatorRegistryCenter registryCenter = new ZookeeperRegistryCenter(
                    collectorConfig.getZookeeper().toZookeeperConfiguration());
            registryCenter.init();

            log.info("Startup to all job scheduler ...");
            Map<String, JobBootstrap> bootstraps = new ElasticJobBootstrapBuilder(collectorConfig, registryCenter, null).build();
            safeMap(bootstraps).forEach((beanName, bootstrap) -> {
                if (bootstrap instanceof ScheduleJobBootstrap) {
                    log.info("Scheduling job for {} -> {}", beanName, bootstrap);
                    ((ScheduleJobBootstrap) bootstrap).schedule();
                } else if (bootstrap instanceof OneOffJobBootstrap) {
                    log.info("Execution job for {} -> {}", beanName, bootstrap);
                    oneoffExecutor.execute(() -> ((OneOffJobBootstrap) bootstrap).execute());
                }
            });

            // Reject receiving new tasks and continue executing old tasks.
            oneoffExecutor.shutdown();
        } catch (Throwable e) {
            throw new IllegalStateException(format("Failed to init collector job scheduler."), e);
        }
    }

    void initEventBusConfiguration() {
        try {
            final ClassPathResourcePatternResolver resolver = new ClassPathResourcePatternResolver();
            // Load base collectorConfiguration.
            final StreamResource conf = resolver.getResources(CONF_EVENTBUS_LOCATION)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            format("Not found eventbus configuration for '%s'", CONF_EVENTBUS_LOCATION)));
            this.eventBusConfig = YamlUtils.parse(Resources.toString(conf.getURL(), UTF_8), CONF_EVENTBUS_PREFIX,
                    ClientEventBusConfig.class);
        } catch (Throwable e) {
            throw new IllegalStateException(
                    format("Failed to load resolve eventbus configuration. - classpath: %s", SystemUtils.JAVA_CLASS_PATH), e);
        }
    }

    void initEventBus() {
        // TODO
    }

    public static final String CONF_COLLECTOR_PREFIX = getStringProperty("CONF_COLLECTOR_PREFIX", "/rengine/collector");
    public static final String CONF_COLLECTOR_LOCATION = getStringProperty("CONF_COLLECTOR_LOCATION",
            "classpath*:/collector-job.yaml");
    public static final String CONF_COLLECTOR_LOCATION_EXTENSION = getStringProperty("CONF_COLLECTOR_LOCATION_EXTENSION",
            "classpath*:/collector-job*.yaml");

    public static final String CONF_EVENTBUS_PREFIX = getStringProperty("CONF_COLLECTOR_PREFIX", "/rengine/eventbus");
    public static final String CONF_EVENTBUS_LOCATION = getStringProperty("CONF_EVENTBUS_LOCATION",
            "classpath*:/collector-eventbus.yaml");

}