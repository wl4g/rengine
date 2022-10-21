/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.elasticjob.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.wl4g.rengine.client.collector.job.EventJobExecutor.JobParamBase;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * ElasticJob configuration.
 */
@Getter
@ToString
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobConfiguration {

    private final String jobName;

    private final String cron;

    private final String timeZone;

    private final int shardingTotalCount;

    /**
     * Set mapper of sharding items and sharding parameters.
     *
     * <p>
     * sharding item and sharding parameter split by =, multiple sharding items
     * and sharding parameters split by comma, just like map. Sharding item
     * start from zero, cannot equal to great than sharding total count.
     *
     * For example: 0=a,1=b,2=c
     * </p>
     */
    private final String shardingItemParameters;

    private final String jobParameter;

    /**
     * Set enable or disable monitor execution.
     *
     * <p>
     * For short interval job, it is better to disable monitor execution to
     * improve performance. It can't guarantee repeated data fetch and can't
     * failover if disable monitor execution, please keep idempotence in job.
     *
     * For long interval job, it is better to enable monitor execution to
     * guarantee fetch data exactly once.
     * </p>
     */
    private final boolean monitorExecution;

    private final boolean failover;

    private final boolean misfire;

    /**
     * Set max tolerate time different seconds between job server and registry
     * center.
     *
     * <p>
     * ElasticJob will throw exception if exceed max tolerate time different
     * seconds. -1 means do not check.
     * </p>
     */
    private final int maxTimeDiffSeconds;

    /**
     * Set reconcile interval minutes for job sharding status.
     *
     * <p>
     * Monitor the status of the job server at regular intervals, and resharding
     * if incorrect.
     * </p>
     */
    private final int reconcileIntervalMinutes;

    /**
     * Set job sharding strategy type.
     *
     * <p>
     * Default for {@code AverageAllocationJobShardingStrategy}.
     * </p>
     */
    private final String jobShardingStrategyType;

    private final String jobExecutorServiceHandlerType;

    private final String jobErrorHandlerType;

    private final Collection<String> jobListenerTypes;

    private final Collection<JobExtraConfiguration> extraConfigurations;

    private final String description;

    private final Properties props;

    private final boolean disabled;

    /**
     * Set whether overwrite local configuration to registry center when job
     * startup.
     * 
     * <p>
     * If overwrite enabled, every startup will use local configuration.
     * </p>
     */
    private final boolean overwrite;

    private final String label;

    private final boolean staticSharding;

    //
    // [Begin] ADD FEATURES.
    //
    private @Default List<JobParamBase> staticParams = new ArrayList<>();
    //
    // [End] ADD FEATURES.
    //

}
