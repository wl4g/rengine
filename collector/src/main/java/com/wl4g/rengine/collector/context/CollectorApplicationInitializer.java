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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.JobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.OneOffJobBootstrap;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.collector.config.CollectorProperties;
import com.wl4g.rengine.collector.config.ElasticJobBootstrapBuilder;

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

    final CollectorProperties config;
    final ExecutorService executor;

    public CollectorApplicationInitializer() {
        // Load and parse collector job configuration.
        this.config = new CollectorProperties();

        // Initialze executor.
        String prefix = getClass().getSimpleName();
        final AtomicInteger counter = new AtomicInteger(0);
        this.executor = Executors.newFixedThreadPool(
                (config.getGlobalScrapeJobConfig().getOneOffJobThreadPools() <= 1) ? 1
                        : config.getGlobalScrapeJobConfig().getOneOffJobThreadPools(),
                r -> new Thread(r, prefix.concat("-" + counter.incrementAndGet())));
    }

    void onStart(@Observes StartupEvent event, @ConfigProperty(name = "quarkus.application.name") String appName) {
        // Setup API response prompt.
        RespBase.ErrorPromptMessageBuilder.setPrompt(appName.substring(Math.max(appName.lastIndexOf("-") + 1, 0)));

        // @formatter:off
        // log.info("Initializing tracing configuration ...");
        // List<TracingConfiguration<?>> tracingConfiguration = singletonList(new TracingConfiguration<>("RDB", null));
        // @formatter:on

        log.info("Initializing coordinator registry center ...");
        CoordinatorRegistryCenter registryCenter = new ZookeeperRegistryCenter(config.getZookeeper().toZookeeperConfiguration());

        log.info("Startup to all job scheduler ...");
        Map<String, JobBootstrap> bootstraps = new ElasticJobBootstrapBuilder(config, registryCenter, null).build();
        safeMap(bootstraps).forEach((beanName, bootstrap) -> {
            if (bootstrap instanceof ScheduleJobBootstrap) {
                log.info("Startup schedule job for {} -> {}", beanName, bootstrap);
                ((ScheduleJobBootstrap) bootstrap).schedule();
            } else if (bootstrap instanceof OneOffJobBootstrap) {
                executor.execute(() -> ((OneOffJobBootstrap) bootstrap).execute());
            }
        });

        // Reject receiving new tasks and continue executing old tasks.
        executor.shutdown();
    }

    void onStop(@Observes ShutdownEvent event) {
        log.info("The application is stopping...");
    }

}