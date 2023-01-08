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
package com.wl4g.rengine.scheduler.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.executor.item.impl.TypedJobItemExecutor;
import org.apache.shardingsphere.elasticjob.lite.internal.storage.JobNodePath;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;

import com.wl4g.infra.context.utils.SpringContextHolder;
import com.wl4g.rengine.eventbus.RengineEventBusService;
import com.wl4g.rengine.scheduler.config.RengineSchedulerProperties;
import com.wl4g.rengine.scheduler.config.RengineSchedulerProperties.BaseJobProperties;
import com.wl4g.rengine.scheduler.config.RengineSchedulerProperties.EngineSchedulingControllerJobProperties;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The job abstract executor that actively collects time for scraping events.
 * scrape jobs configuration similar to prometheus. </br>
 * </br>
 * Distributed automatic fragmentation scraping task based on zookeeper, more
 * smart than prometheus.
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 */
@Getter
@CustomLog
public abstract class AbstractJobExecutor implements TypedJobItemExecutor {

    protected final RengineSchedulerProperties config;
    protected final CoordinatorRegistryCenter regCenter;
    @SuppressWarnings("rawtypes")
    protected final Collection<RengineEventBusService> eventbusServices;
    // protected final SpelVariables spelVariables;

    public AbstractJobExecutor() {
        this.config = SpringContextHolder.getBean(RengineSchedulerProperties.class);
        this.regCenter = SpringContextHolder.getBean(CoordinatorRegistryCenter.class);
        this.eventbusServices = safeMap(SpringContextHolder.getBeans(RengineEventBusService.class)).values();
        // this.spelVariables = new
        // SpelVariables().from(config.getGlobalScrapeJobConfig().getJobVariables());
    }

    @Override
    public void process(ElasticJob elasticJob, JobConfiguration jobConfig, JobFacade jobFacade, ShardingContext context) {
        log.info("ShardingContext: {}", toJSONString(context));
        try {
            final int currentShardingTotalCount = determineShardingTotalCount(elasticJob, jobConfig, jobFacade, context);
            execute(currentShardingTotalCount, jobConfig, jobFacade, context);
        } catch (Exception e) {
            log.error(format("Failed to execute of jobConfig: %s, context: %s", jobConfig, context), e);
        }
    }

    protected abstract void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception;

    protected int determineShardingTotalCount(
            ElasticJob elasticJob,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) {

        // When setup true, the shardingTotalCount will be ignored, and the will
        // be automatically allocated according to the number of cluster nodes
        // priority.
        if (nonNull(jobConfig.isAutoShardingTotalCount()) && !jobConfig.isAutoShardingTotalCount()) {
            return jobConfig.getShardingTotalCount();
        }

        // It is dynamically calculated according to the number of cluster
        // nodes.
        /*
         * Only the instance path indicates the current number of online nodes
         * (temporary), and the service path indicates the cumulative number of
         * online + offline nodes (permanent). for example:
         * /rengine/node-exporter-job/instances => [10.0.0.114@-@195117]
         */
        List<String> serverNames = regCenter.getChildrenKeys(new JobNodePath(jobConfig.getJobName()).getInstancesNodePath());
        int shardingTotalCount = (nonNull(serverNames) && serverNames.size() > 0) ? serverNames.size()
                : jobConfig.getShardingTotalCount();

        log.debug("Assigned the shards dynamic accroding to cluster nodes: {}", shardingTotalCount);
        return shardingTotalCount;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static enum SchedulerJobType {
        ENGINE_EXECUTION_SCHEDULER_CONTROLLER(EngineSchedulingControllerJobProperties.class);

        private final Class<? extends BaseJobProperties<? extends JobParamBase>> jobConfigClass;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public abstract static class JobParamBase {
        /**
         * The collect parameter for name.
         */
        private @NotBlank String name;
    }

}
