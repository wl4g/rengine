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
import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.wl4g.rengine.common.entity.SchedulingJob;
import com.wl4g.rengine.common.entity.SchedulingJob.RunState;
import com.wl4g.rengine.common.entity.SchedulingTrigger.CronTriggerConfig;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link EngineExecutionScheduler}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
@Getter
@CustomLog
public class EngineExecutionScheduler extends AbstractJobExecutor {

    public EngineExecutionScheduler() {
    }

    @Override
    public String getType() {
        return ExecutorJobType.ENGINE_EXECUTION_SCHEDULER.name();
    }

    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        CronTriggerConfig ctc = null;
        Long jobId = null;
        assert nonNull(ctc);
        assert nonNull(jobId);

        try {
            final ExecuteResult result = rengineClient.execute(ExecuteRequest.builder()
                    // .clientId("") // TODO
                    // .clientSecret("")
                    .scenesCodes(ctc.getScenesCodes())
                    .bestEffort(ctc.getBestEffort())
                    .timeout(ctc.getTimeout())
                    .args(safeMap(ctc.getArgs()))
                    .build(), ex -> null);

            // Update execution success info to DB.
            final SchedulingJob jobExecutionSuccess = schedulingJobService.get(jobId);
            jobExecutionSuccess.setRunState(RunState.SUCCESS);
            jobExecutionSuccess.setFinishedTime(new Date());
            jobExecutionSuccess.setRequestId(result.getRequestId());
            jobExecutionSuccess.setResults(result.getResults());

            log.debug("Updating to job for : {}", jobExecutionSuccess);
            final var resultSuccess = schedulingJobService.save(jobExecutionSuccess);
            log.debug("Updated to job for : {} => {}", jobExecutionSuccess, resultSuccess);

        } catch (Exception e) {
            log.error(format("Failed to execute engine execution for : contenxt: %s", context), e);

            // Update execution failed info to DB.
            SchedulingJob jobExecutionFailed = null;
            try {
                jobExecutionFailed = schedulingJobService.get(jobId);
                jobExecutionFailed.setRunState(RunState.FAILED);
                jobExecutionFailed.setFinishedTime(new Date());

                log.debug("Updating to job for : {}", jobExecutionFailed);
                final var resultFailed = schedulingJobService.save(jobExecutionFailed);
                log.debug("Updated to job for : {} => {}", jobExecutionFailed, resultFailed);
            } catch (Exception e2) {
                log.error(format("Failed to update failed execution job to DB. job: %s", jobExecutionFailed), e2);
            }
        }

    }

}
