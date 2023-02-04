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
package com.wl4g.rengine.controller.job;

import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.ElasticJob;
import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.executor.item.impl.TypedJobItemExecutor;
import org.apache.shardingsphere.elasticjob.lite.internal.storage.JobNodePath;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;

import com.wl4g.infra.context.utils.SpringContextHolder;
import com.wl4g.rengine.client.core.RengineClient;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.RunState;
import com.wl4g.rengine.controller.config.RengineControllerProperties;
import com.wl4g.rengine.controller.lifecycle.GlobalControllerJobManager;
import com.wl4g.rengine.service.ScheduleJobLogService;
import com.wl4g.rengine.service.ScheduleTriggerService;
import com.wl4g.rengine.service.meter.RengineMeterService;
import com.wl4g.rengine.service.model.SaveScheduleTriggerResult;

import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.ToString;

/**
 * <ul>
 * <li>Actively execute the abstract job of workflow.</br>
 * </li>
 * <li>For monitoring scenarios, it is similar to Prometheus's active scraping
 * index operation. Distributed automatic fragmentation scraping task based on
 * zookeeper, more smart than prometheus scraper.</br>
 * </li>
 * <li>For offline task scenarios, similar to spark, flink job.</br>
 * </li>
 * <ul>
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 */
@CustomLog
public abstract class AbstractJobExecutor implements TypedJobItemExecutor {
    private RengineControllerProperties config;
    private RengineMeterService meterService;
    private CoordinatorRegistryCenter regCenter;
    private RengineClient rengineClient;
    private GlobalControllerJobManager globalControllerJobManager;
    private ScheduleTriggerService scheduleTriggerService;
    private ScheduleJobLogService scheduleJobLogService;
    // private Collection<RengineEventBusService> eventbusServices;

    protected RengineControllerProperties getConfig() {
        if (isNull(config)) {
            synchronized (this) {
                if (isNull(config)) {
                    this.config = SpringContextHolder.getBean(RengineControllerProperties.class);
                }
            }
        }
        return config;
    }

    protected RengineMeterService getMeterService() {
        if (isNull(meterService)) {
            synchronized (this) {
                if (isNull(meterService)) {
                    this.meterService = SpringContextHolder.getBean(RengineMeterService.class);
                }
            }
        }
        return meterService;
    }

    protected CoordinatorRegistryCenter getRegCenter() {
        if (isNull(regCenter)) {
            synchronized (this) {
                if (isNull(regCenter)) {
                    this.regCenter = SpringContextHolder.getBean(CoordinatorRegistryCenter.class);
                }
            }
        }
        return regCenter;
    }

    protected RengineClient getRengineClient() {
        if (isNull(rengineClient)) {
            synchronized (this) {
                if (isNull(rengineClient)) {
                    this.rengineClient = SpringContextHolder.getBean(RengineClient.class);
                }
            }
        }
        return rengineClient;
    }

    protected GlobalControllerJobManager getGlobalScheduleJobManager() {
        if (isNull(globalControllerJobManager)) {
            synchronized (this) {
                if (isNull(globalControllerJobManager)) {
                    this.globalControllerJobManager = SpringContextHolder.getBean(GlobalControllerJobManager.class);
                }
            }
        }
        return globalControllerJobManager;
    }

    protected ScheduleTriggerService getScheduleTriggerService() {
        if (isNull(scheduleTriggerService)) {
            synchronized (this) {
                if (isNull(scheduleTriggerService)) {
                    this.scheduleTriggerService = SpringContextHolder.getBean(ScheduleTriggerService.class);
                }
            }
        }
        return scheduleTriggerService;
    }

    protected ScheduleJobLogService getScheduleJobLogService() {
        if (isNull(scheduleJobLogService)) {
            synchronized (this) {
                if (isNull(scheduleJobLogService)) {
                    this.scheduleJobLogService = SpringContextHolder.getBean(ScheduleJobLogService.class);
                }
            }
        }
        return scheduleJobLogService;
    }

    // @formatter:off
    //protected List<RengineEventBusService> getEventbusServices() {
    //    if (isNull(eventbusServices)) {
    //        this.eventbusServices = safeMap(SpringContextHolder.getBeans(RengineEventBusService.class)).values();
    //    }
    //    return eventbusServices;
    //}
    // @formatter:on

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
        final List<String> serverNames = getRegCenter()
                .getChildrenKeys(new JobNodePath(jobConfig.getJobName()).getInstancesNodePath());
        final int shardingTotalCount = (nonNull(serverNames) && serverNames.size() > 0) ? serverNames.size()
                : jobConfig.getShardingTotalCount();

        log.debug("Assigned the shards dynamic accroding to cluster nodes: {}", shardingTotalCount);
        return shardingTotalCount;
    }

    protected ScheduleTrigger updateTriggerRunState(final @NotNull Long triggerId, final @NotNull RunState runState) {
        notNullOf(runState, "runState");
        ScheduleTrigger trigger = null;
        SaveScheduleTriggerResult result = null;
        try {
            trigger = getScheduleTriggerService().get(notNullOf(triggerId, "triggerId"));
            trigger.setRunState(runState);
            notNull(trigger, "Not found schedule trigger of triggerId: %s", triggerId);

            log.debug("Updating to scheduling trigger run-state : {}", trigger);
            result = getScheduleTriggerService().save(trigger);
            log.debug("Updated to scheduling trigger run-state : {} => {}", trigger, result);
        } catch (Exception ex) {
            log.error(format("Failed to update scheduling trigger run-state to DB. - %s", trigger), ex);
        }
        return trigger;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static enum SchedulerJobType {
        GLOBAL_CONTROLLER, CLIENT_SCHEDULER, FLINK_SCHEDULER;
    }

}
