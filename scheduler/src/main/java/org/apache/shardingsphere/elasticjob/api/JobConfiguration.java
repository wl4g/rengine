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
 * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.elasticjob.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import com.wl4g.rengine.scheduler.job.AbstractJobExecutor.ExecutorJobType;

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

    private String jobName;

    private String cron;

    private String timeZone;

    //
    // [Begin] ADD FEATURES.
    //
    ExecutorJobType jobType;

    // When setup true, the shardingTotalCount will be ignored, and the will
    // be automatically allocated according to the number of cluster nodes
    // priority.
    private boolean autoShardingTotalCount;

    //
    // [End] ADD FEATURES.
    //

    private int shardingTotalCount;

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
    private String shardingItemParameters;

    private String jobParameter;

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
    private boolean monitorExecution;

    private boolean failover;

    private boolean misfire;

    /**
     * Set max tolerate time different seconds between job server and registry
     * center.
     *
     * <p>
     * ElasticJob will throw exception if exceed max tolerate time different
     * seconds. -1 means do not check.
     * </p>
     */
    private int maxTimeDiffSeconds;

    /**
     * Set reconcile interval minutes for job sharding status.
     *
     * <p>
     * Monitor the status of the job server at regular intervals, and resharding
     * if incorrect.
     * </p>
     */
    private int reconcileIntervalMinutes;

    /**
     * Set job sharding strategy type.
     *
     * <p>
     * Default for {@code AverageAllocationJobShardingStrategy}.
     * </p>
     */
    private String jobShardingStrategyType;

    private String jobExecutorServiceHandlerType;

    private String jobErrorHandlerType;

    //
    // [Begin] MODIFIY FEATURES.
    //
    // private Collection<String> jobListenerTypes ;
    private @Default Collection<String> jobListenerTypes = new ArrayList<>();
    //
    // [End] MODIFIY FEATURES.
    //

    //
    // [Begin] MODIFIY FEATURES.
    //
    // private Collection<JobExtraConfiguration> extraConfigurations;
    private @Default Collection<JobExtraConfiguration> extraConfigurations = new ArrayList<>();
    //
    // [End] MODIFIY FEATURES.
    //

    private String description;

    //
    // [Begin] MODIFIY FEATURES.
    //
    // private Properties props;
    private @Default Properties props = new Properties();
    //
    // [End] MODIFIY FEATURES.
    //

    private boolean disabled;

    /**
     * Set whether overwrite local configuration to registry center when job
     * startup.
     * 
     * <p>
     * If overwrite enabled, every startup will use local configuration.
     * </p>
     */
    private boolean overwrite;

    private String label;

    private boolean staticSharding;
}
