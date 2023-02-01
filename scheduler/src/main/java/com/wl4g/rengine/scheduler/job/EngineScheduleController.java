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
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.rengine.scheduler.job.AbstractJobExecutor.ExecutorJobType.ENGINE_EXECUTION_SCHEDULER;
import static com.wl4g.rengine.scheduler.job.AbstractJobExecutor.ExecutorJobType.ENGINE_SCHEDULE_CONTROLLER;
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;

import com.wl4g.rengine.common.entity.ScheduleJob.RunState;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.ScheduleTrigger.TriggerType;
import com.wl4g.rengine.scheduler.lifecycle.ElasticJobBootstrapBuilder.JobParameter;
import com.wl4g.rengine.service.model.QueryScheduleTrigger;
import com.wl4g.rengine.service.model.SaveScheduleJob;
import com.wl4g.rengine.service.model.SaveScheduleJobResult;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link EngineScheduleController}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.HttpJobExecutor}
 */
@Getter
@CustomLog
public class EngineScheduleController extends AbstractJobExecutor {

    @Override
    public String getType() {
        return ENGINE_SCHEDULE_CONTROLLER.name();
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

        final List<ScheduleTrigger> shardingTriggers = getScheduleTriggerService().findWithSharding(
                QueryScheduleTrigger.builder().type(TriggerType.CRON.name()).enable(true).build(), currentShardingTotalCount,
                context.getShardingItem());
        log.info("Loaded the sharding triggers : {}", shardingTriggers);

        // Actual scheduling to engine execution job.
        safeList(shardingTriggers).stream().forEach(trigger -> {
            final String jobName = buildJobName(trigger);
            SaveScheduleJob jobPrepared = null;
            try {
                final var lock = getGlobalScheduleJobManager().getMutexLock(trigger.getId());
                // Normally never expires unless the current node jvm exits.
                if (lock.acquire(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                    jobPrepared = preparedSchedulingJob(trigger.getId(), jobName);
                    log.info("Schedule scheduling job for {} : {}", jobName, trigger);

                    final ScheduleJobBootstrap bootstrap = getGlobalScheduleJobManager().add(lock, ENGINE_EXECUTION_SCHEDULER,
                            jobName, trigger, new JobParameter(trigger.getId(), jobPrepared.getId()));

                    bootstrap.schedule();
                    updateSchedulingJobInfo(jobPrepared.getId(), RunState.SCHED, true, false, null);
                } else {
                    log.debug("Trigger {} are already bound on other nodes.", trigger.getId());
                }
            } catch (Exception e) {
                log.error(format("Failed to scheduling for currentShardingTotalCount: %s, context: %s, jobName: %s",
                        currentShardingTotalCount, context, jobName), e);
                if (nonNull(jobPrepared)) {
                    updateSchedulingJobInfo(jobPrepared.getId(), RunState.FAILED_SCHED, false, true, null);
                }
            }
        });
    }

    private String buildJobName(final ScheduleTrigger trigger) {
        return EngineScheduleExecutor.class.getSimpleName() + "-" + trigger.getId();
    }

    private SaveScheduleJob preparedSchedulingJob(@NotNull final Long triggerId, @NotBlank final String jobName) {
        // Update the scheduling preparation info to the DB to ensure
        // that the execution job id is generated.
        final SaveScheduleJob jobPrepared = SaveScheduleJob.builder()
                .triggerId(notNullOf(triggerId, "triggerId"))
                .jobName(hasTextOf(jobName, "jobName"))
                .runState(RunState.PREPARED)
                .build();

        log.debug("Updating to job for : {}", jobPrepared);
        final SaveScheduleJobResult resultPrepared = getScheduleJobService().save(jobPrepared);
        log.debug("Updated to job for : {} => {}", jobPrepared, resultPrepared);

        return jobPrepared;
    }

}
