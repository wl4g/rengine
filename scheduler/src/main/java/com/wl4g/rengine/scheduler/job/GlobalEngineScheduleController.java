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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.rengine.scheduler.job.AbstractJobExecutor.ExecutorJobType.CLIENT_SCHEDULER;
import static com.wl4g.rengine.scheduler.job.AbstractJobExecutor.ExecutorJobType.GLOBAL_CONTROLLER;
import static java.lang.String.format;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;

import com.wl4g.infra.common.bean.BaseBean;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger.ScheduleType;
import com.wl4g.rengine.scheduler.lifecycle.ElasticJobBootstrapBuilder.JobParameter;
import com.wl4g.rengine.service.model.QueryScheduleTrigger;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link GlobalEngineScheduleController}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.HttpJobExecutor}
 */
@Getter
@CustomLog
public class GlobalEngineScheduleController extends AbstractJobExecutor {

    @Override
    public String getType() {
        return GLOBAL_CONTROLLER.name();
    }

    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        // Scanning for current sharding scheduling triggers.
        log.info("Loading the sharding triggers for currentShardingTotalCount: {}, jobConfig: {}, jobFacade: {}, context: {}",
                currentShardingTotalCount, jobConfig, jobFacade, context);

        final List<ScheduleTrigger> shardingTriggers = getScheduleTriggerService().findWithSharding(QueryScheduleTrigger.builder()
                .type(ScheduleType.CLIENT_SCHEDULER.name())
                ./* enable(true). */build(), currentShardingTotalCount, context.getShardingItem());
        log.info("Loaded the sharding triggers : {}", shardingTriggers);

        // Actual scheduling to engine execution job.
        safeList(shardingTriggers).stream().forEach(trigger -> {
            final String jobName = buildJobName(trigger);
            try {
                // The status of the trigger is disabled, you need to shtudown
                // the scheduling job.
                if (trigger.getEnable() == BaseBean.DISABLED) {
                    log.info("Disabling scheduling job for : {}", trigger.getId());
                    getGlobalScheduleJobManager().shutdown(trigger.getId());
                }

                // Any a trigger can only be scheduled by one cluster node at
                // the same time to prevent repeated scheduling of multiple
                // nodes. Of course, if the current node is down, it will be
                // transferred to other nodes to continue to be scheduled
                // according to the elastic-job mechanism.
                final var schedulingMutexLock = getGlobalScheduleJobManager().getMutexLock(trigger.getId());

                // Infinite timeout unless the current JVM exits.
                if (schedulingMutexLock.acquire(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                    updateTriggerRunState(trigger.getId(), RunState.PREPARED);

                    log.info("Scheduling trigger for {} : {}", jobName, trigger);
                    final ScheduleJobBootstrap bootstrap = getGlobalScheduleJobManager().add(schedulingMutexLock,
                            CLIENT_SCHEDULER, jobName, trigger, new JobParameter(trigger.getId()));
                    bootstrap.schedule();

                    updateTriggerRunState(trigger.getId(), RunState.SCHED);
                } else {
                    log.debug("Trigger {} are already bound on other nodes.", trigger.getId());
                }

            } catch (Exception e) {
                log.error(format("Failed to scheduling for currentShardingTotalCount: %s, context: %s, jobName: %s",
                        currentShardingTotalCount, context, jobName), e);
                updateTriggerRunState(trigger.getId(), RunState.FAILED_SCHED);
            }
        });
    }

    private String buildJobName(final ScheduleTrigger trigger) {
        return EngineClientScheduler.class.getSimpleName() + "-" + trigger.getId();
    }

}
