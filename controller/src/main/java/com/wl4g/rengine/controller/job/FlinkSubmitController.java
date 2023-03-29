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
package com.wl4g.rengine.controller.job;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.task.GenericTaskRunner.newDefaultScheduledExecutor;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.nextbreakpoint.flinkclient.api.ApiClient;
import com.nextbreakpoint.flinkclient.api.FlinkApi;
import com.nextbreakpoint.flinkclient.model.JarFileInfo;
import com.nextbreakpoint.flinkclient.model.JarListInfo;
import com.nextbreakpoint.flinkclient.model.JarRunResponseBody;
import com.nextbreakpoint.flinkclient.model.JarUploadResponseBody;
import com.nextbreakpoint.flinkclient.model.JobDetailsInfo;
import com.nextbreakpoint.flinkclient.model.JobIdsWithStatusOverview;
import com.nextbreakpoint.flinkclient.model.JobDetailsInfo.StateEnum;
import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.infra.common.task.SafeScheduledTaskPoolExecutor;
import com.wl4g.rengine.common.entity.ControllerLog;
import com.wl4g.rengine.common.entity.Controller;
import com.wl4g.rengine.common.entity.Controller.FlinkSubmitExecutionConfig;
import com.wl4g.rengine.common.entity.Controller.RunState;
import com.wl4g.rengine.controller.config.RengineControllerProperties.EngineFlinkSchedulerProperties;
import com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.JobParameter;

import lombok.CustomLog;
import lombok.Getter;

/**
 * Notice: The flink cep job can be automatically scheduled, but currently it is
 * recommended to use a professional scheduling platform such as Aws EMR or
 * dolphinscheduler.
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 * @see https://nightlies.apache.org/flink/flink-docs-release-1.17/docs/ops/rest_api/
 * @see https://github1s.com/apache/flink/blob/release-1.14/flink-runtime/src/main/java/org/apache/flink/runtime/rest/RestServerEndpoint.java
 * @see https://github1s.com/wl4g-collect/flink-client/blob/master/src/test/java/com/nextbreakpoint/FlinkClientIT.java#L88-L89
 * @see https://mvnrepository.com/artifact/com.nextbreakpoint/com.nextbreakpoint.flinkclient
 */
@Getter
@CustomLog
public class FlinkSubmitController extends AbstractJobExecutor {

    private FlinkApi flinkApi;
    private SafeScheduledTaskPoolExecutor executor;

    @Override
    public String getType() {
        return ControllerJobType.FLINK_SUBMITTER.name();
    }

    protected FlinkApi getFlinkApi() {
        if (isNull(flinkApi)) {
            synchronized (this) {
                if (isNull(flinkApi)) {
                    final EngineFlinkSchedulerProperties flinkConfig = getConfig().getFlink();
                    this.flinkApi = new FlinkApi(new ApiClient());
                    log.info("Initialized flinkApi of : {}", flinkConfig);
                }
            }
        }
        return flinkApi;
    }

    protected SafeScheduledTaskPoolExecutor getExecutor() {
        if (isNull(executor)) {
            synchronized (this) {
                if (isNull(executor)) {
                    final EngineFlinkSchedulerProperties fr = getConfig().getFlink();
                    this.executor = newDefaultScheduledExecutor(FlinkSubmitController.class.getSimpleName(),
                            fr.getConcurrency(), fr.getAcceptQueue());
                    log.info("Initialized schedule executor of concurrency: {}, acceptQueue: {}", fr.getConcurrency(),
                            fr.getAcceptQueue());
                }
            }
        }
        return executor;
    }

    /**
     * {@link com.wl4g.rengine.job.cep.RengineKafkaFlinkCepStreamingIT}
     */
    @Override
    protected void execute(
            int currentShardingTotalCount,
            @NotNull JobConfiguration jobConfig,
            @NotNull JobFacade jobFacade,
            @NotNull ShardingContext context) throws Exception {

        final JobParameter jobParameter = notNullOf(parseJSON(jobConfig.getJobParameter(), JobParameter.class), "jobParameter");
        final Long controllerId = notNullOf(jobParameter.getScheduleId(), "controllerId");

        updateControllerRunState(controllerId, RunState.RUNNING);
        final ControllerLog controllerLog = upsertControllerLog(controllerId, null, true, false, null, null);

        final Controller controller = notNullOf(getControllerScheduleService().get(controllerId), "controller");
        final FlinkSubmitExecutionConfig fssc = ((FlinkSubmitExecutionConfig) notNullOf(controller.getDetails(),
                "flinkSubmitScheduleConfig")).validate();

        try {
            log.info("Execution scheduling for : {}", controller);

            final JobIdsWithStatusOverview allJobInfo = getFlinkApi().getJobs();
            log.info("Found all flink job info : {}", allJobInfo);

            if (nonNull(allJobInfo)) {
                safeList(allJobInfo.getJobs());
            }

            // rengine-job-base-1.0.0-jar-with-dependencies.jar
            final JarUploadResponseBody upload = getFlinkApi().uploadJar(new File("xx/rengine-job-base-1.0.0.jar")); // TODO

            final JarListInfo jarsInfo = getFlinkApi().listJars();
            final JarFileInfo jar = safeList(jarsInfo.getFiles()).stream()
                    .filter(j -> StringUtils2.equals(j.getName(), upload.getFilename()))
                    .findFirst()
                    .orElseGet(null);

            // Make flink job arguments.
            final List<String> jobArgs = new ArrayList<>(8);
            // Flink source MQ (kafka/pulsar/rabbitmq/...) options.
            jobArgs.add(format("--brokers %s", fssc.getBrokers()));
            jobArgs.add(format("--eventTopicPattern %s", fssc.getEventTopicPattern()));
            jobArgs.add(format("--groupId %s", fssc.getGroupId()));
            jobArgs.add(format("--fromOffsetTime %s", fssc.getFromOffsetTime()));
            jobArgs.add(format("--deserializerClass %s", fssc.getDeserializerClass()));
            jobArgs.add(format("--keyByExprPath %s", fssc.getKeyByExprPath()));
            // FLINK basic options.
            jobArgs.add(format("--runtimeMode %s", fssc.getRuntimeMode()));
            jobArgs.add(format("--restartAttempts %s", fssc.getRestartAttempts()));
            jobArgs.add(format("--restartDelaySeconds %s", fssc.getRestartDelaySeconds()));
            // FLINK Checkpoint options.
            jobArgs.add(format("--checkpointDir %s", fssc.getCheckpointDir()));
            jobArgs.add(format("--checkpointMode %s", fssc.getCheckpointMode()));
            jobArgs.add(format("--checkpointIntervalMs %s", fssc.getCheckpointIntervalMs()));
            jobArgs.add(format("--checkpointMinPauseBetween %s", fssc.getCheckpointMinPauseBetween()));
            jobArgs.add(format("--checkpointMaxConcurrent %s", fssc.getCheckpointMaxConcurrent()));
            jobArgs.add(format("--externalizedCheckpointCleanup %s", fssc.getExternalizedCheckpointCleanup()));
            // FLINK Performance options.
            jobArgs.add(format("--parallelis %s", fssc.getParallelis()));
            jobArgs.add(format("--maxParallelism %s", fssc.getMaxParallelism()));
            jobArgs.add(format("--bufferTimeoutMillis %s", fssc.getBufferTimeoutMillis()));
            jobArgs.add(format("--outOfOrdernessMillis %s", fssc.getOutOfOrdernessMillis()));
            jobArgs.add(format("--idleTimeoutMillis %s", fssc.getIdleTimeoutMillis()));
            // FLINK Sink options.
            jobArgs.add(format("--forceUsePrintSink %s", fssc.getForceUsePrintSink()));
            // FLINK ControllerLog options.
            jobArgs.add(format("--jobName %s", fssc.getJobName()));
            // FLINK CEP job options.
            jobArgs.add(format("--cepPatterns %s", "TODO")); // TODO
            jobArgs.add(format("--inProcessingTime %s", fssc.getInProcessingTime()));
            jobArgs.add(format("--alertTopic %s", fssc.getAlertTopic()));
            // FLINK CEP job with kafka options.
            jobArgs.add(format("--offsetResetStrategy %s", fssc.getOffsetResetStrategy()));
            jobArgs.add(format("--partitionDiscoveryIntervalMs %s", fssc.getPartitionDiscoveryIntervalMs()));

            log.info("Run flink job args : {}", jobArgs);

            final JarRunResponseBody response = getFlinkApi().runJar(jar.getId(), true, null, null, join(jobArgs, ","),
                    fssc.getEntryClass(), fssc.getParallelis());

            final JobDetailsInfo jobDetails = getFlinkApi().getJobDetails(response.getJobid());
            // TODO
            if (jobDetails.getState() == StateEnum.RUNNING) {
            }

            updateControllerRunState(controllerId, RunState.SUCCESS);

        } catch (Throwable ex) {
            final String errmsg = format(
                    "Failed to executing requests job of currentShardingTotalCount: %s, context: %s, controllerId: %s, jobLogId: %s",
                    currentShardingTotalCount, context, controller.getId(), controllerLog.getId());
            if (log.isDebugEnabled()) {
                log.error(errmsg, ex);
            } else {
                log.error(format("%s. - reason: %s", errmsg, ex.getMessage()));
            }

            updateControllerRunState(controllerId, RunState.FAILED);
            upsertControllerLog(controllerId, controllerLog.getId(), false, true, false, null);
        }
    }

}