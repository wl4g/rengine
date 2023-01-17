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
package com.wl4g.rengine.scheduler.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeToList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;

import lombok.CustomLog;

/**
 * {@link GlobalScheduleJobManager}
 * 
 * @author James Wong
 * @version 2023-01-17
 * @since v1.0.0
 */
@CustomLog
public class GlobalScheduleJobManager {

    private final Map<String, ScheduleJobBootstrap> jobBootstrapRegistry;

    public GlobalScheduleJobManager() {
        this.jobBootstrapRegistry = new ConcurrentHashMap<>(16);
        notNullOf(jobBootstrapRegistry, "jobBootstrapRegistry");
    }

    public ScheduleJobBootstrap get(String jobName) {
        return jobBootstrapRegistry.get(jobName);
    }

    public GlobalScheduleJobManager put(String jobName, ScheduleJobBootstrap bootstrap) {
        jobBootstrapRegistry.put(jobName, bootstrap);
        return this;
    }

    public GlobalScheduleJobManager remove(String jobName) {
        jobBootstrapRegistry.remove(jobName);
        return this;
    }

    public GlobalScheduleJobManager start(String... jobNames) {
        log.info("Schedule job bootstrap starting ...");
        final List<String> _jobNames = safeToList(String.class, jobNames);
        safeMap(jobBootstrapRegistry).entrySet().stream().filter(e -> _jobNames.contains(e.getKey())).forEach(e -> {
            e.getValue().schedule();
            log.info("Scheduled job bootstrap : {}", e.getKey());
        });
        return this;
    }

    public GlobalScheduleJobManager shutdown(String... jobNames) {
        log.info("Schedule job bootstrap shutdown ...");
        final List<String> _jobNames = safeToList(String.class, jobNames);
        safeMap(jobBootstrapRegistry).entrySet().stream().filter(e -> _jobNames.contains(e.getKey())).forEach(e -> {
            e.getValue().shutdown();
            log.info("Shutdown job bootstrap : {}", e.getKey());
        });
        return this;
    }

}
