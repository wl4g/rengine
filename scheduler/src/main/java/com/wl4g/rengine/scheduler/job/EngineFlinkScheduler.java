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

import static com.wl4g.infra.common.task.GenericTaskRunner.newDefaultScheduledExecutor;
import static java.util.Objects.isNull;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.scheduler.config.RengineSchedulerProperties.EngineFlinkSchedulerProperties;

import lombok.CustomLog;
import lombok.Getter;

/**
 * {@link EngineFlinkScheduler}
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 */
@Getter
@CustomLog
public class EngineFlinkScheduler extends AbstractJobExecutor {

    private SafeScheduledTaskPoolExecutor executor;

    protected SafeScheduledTaskPoolExecutor getExecutor() {
        if (isNull(executor)) {
            synchronized (this) {
                if (isNull(executor)) {
                    final EngineFlinkSchedulerProperties fr = getConfig().getFlink();
                    this.executor = newDefaultScheduledExecutor(EngineFlinkScheduler.class.getSimpleName(), fr.getConcurrency(),
                            fr.getAcceptQueue());
                    log.info("Initialized schedule executor of concurrency: {}, acceptQueue: {}", fr.getConcurrency(),
                            fr.getAcceptQueue());
                }
            }
        }
        return executor;
    }

    @Override
    public String getType() {
        return ExecutorJobType.FLINK_SCHEDULER.name();
    }

    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {
        // TODO
    }

}
