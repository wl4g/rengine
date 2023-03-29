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
import static com.wl4g.infra.common.lang.StringUtils2.getFilenameExtension;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_CONTROLLER_JAR_TMP_DIR;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedHashMap;
import java.util.Map;

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
import com.nextbreakpoint.flinkclient.model.JobDetailsInfo.StateEnum;
import com.nextbreakpoint.flinkclient.model.JobIdsWithStatusOverview;
import com.squareup.okhttp.OkHttpClient;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.rengine.common.entity.Controller;
import com.wl4g.rengine.common.entity.Controller.FlinkSubmitExecutionConfig;
import com.wl4g.rengine.common.entity.Controller.FlinkSubmitExecutionConfig.FlinkJobArgs;
import com.wl4g.rengine.common.entity.Controller.RunState;
import com.wl4g.rengine.common.entity.ControllerLog;
import com.wl4g.rengine.common.entity.ControllerLog.FlinkSubmitControllerLog;
import com.wl4g.rengine.common.entity.ControllerLog.FlinkSubmitControllerLog.FlinkJobState;
import com.wl4g.rengine.controller.config.RengineControllerProperties.EngineFlinkSchedulerProperties;
import com.wl4g.rengine.controller.lifecycle.ElasticJobBootstrapBuilder.JobParameter;
import com.wl4g.rengine.service.minio.MinioClientProperties;

import io.minio.DownloadObjectArgs;
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

    @Override
    public String getType() {
        return ControllerJobType.FLINK_SUBMITTER.name();
    }

    protected FlinkApi getFlinkApi() {
        if (isNull(flinkApi)) {
            synchronized (this) {
                if (isNull(flinkApi)) {
                    final EngineFlinkSchedulerProperties flinkConfig = getConfig().getFlink();
                    final ApiClient apiClient = new ApiClient();
                    apiClient.setBasePath(flinkConfig.getEndpoint());
                    apiClient.setHttpClient(new OkHttpClient());
                    apiClient.setUserAgent(FlinkSubmitController.class.getSimpleName().concat("/1.0/java"));
                    apiClient.setConnectTimeout(5);
                    apiClient.setReadTimeout(10);
                    // for upload jars
                    apiClient.setWriteTimeout(300);
                    apiClient.setVerifyingSsl(flinkConfig.getVerifyingSsl());
                    apiClient.setSslCaCert(flinkConfig.getSslCaCert());
                    apiClient.setDebugging(flinkConfig.getDebugging());
                    // for basic auth
                    apiClient.setUsername(flinkConfig.getUsername());
                    apiClient.setPassword(flinkConfig.getPassword());
                    // for api auth
                    apiClient.setApiKey(flinkConfig.getApiKey());
                    apiClient.setApiKeyPrefix(flinkConfig.getApiKeyPrefix());
                    // for oauth auth
                    apiClient.setAccessToken(flinkConfig.getAccessToken());
                    this.flinkApi = new FlinkApi(apiClient);
                    log.info("Initialized flinkApi of : {}", flinkConfig);
                }
            }
        }
        return flinkApi;
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
        final Long controllerId = notNullOf(jobParameter.getControllerId(), "controllerId");

        updateControllerRunState(controllerId, RunState.RUNNING);
        final ControllerLog controllerLog = upsertControllerLog(controllerId, null, true, false, null, null);

        final Controller controller = notNullOf(getControllerScheduleService().get(controllerId), "controller");
        final FlinkSubmitExecutionConfig fssc = ((FlinkSubmitExecutionConfig) notNullOf(controller.getDetails(),
                "flinkSubmitScheduleConfig")).validate();
        final FlinkJobArgs jobArgsConfig = fssc.getJobArgs();

        try {
            log.info("Execution scheduling for : {}", controller);

            final JobIdsWithStatusOverview allJobInfo = getFlinkApi().getJobs();
            log.info("Found all flink job info : {}", allJobInfo);

            if (nonNull(allJobInfo)) {
                safeList(allJobInfo.getJobs());
            }

            // e.g: jobinfra/rengine-job-base-1.0.0.jar
            // e.g: jobinfra/rengine-job-base-1.0.0-jar-with-dependencies.jar
            final JarUploadResponseBody upload = getFlinkApi().uploadJar(obtainFlinkJobJarFile(fssc));
            if (isNull(upload)) {
                throw new IllegalStateException(format("Failed to upload jar for : %s", fssc.getJobFileUrl()));
            }

            final JarListInfo jarsInfo = getFlinkApi().listJars();
            if (isNull(jarsInfo)) {
                throw new IllegalStateException(format("Unable to get flink upload jars."));
            }
            final JarFileInfo jar = safeList(jarsInfo.getFiles()).stream()
                    .filter(j -> StringUtils2.equals(j.getName(), upload.getFilename()))
                    .findFirst()
                    .orElseGet(null);
            if (isNull(jar)) {
                throw new IllegalStateException(format("Could't find jar of flink upload file : {}", upload.getFilename()));
            }

            // Make flink job arguments.
            final Map<String, Object> jobArgs = new LinkedHashMap<>(32);
            // Flink source MQ (kafka/pulsar/rabbitmq/...) options.
            jobArgs.put("brokers", jobArgsConfig.getBrokers());
            jobArgs.put("eventTopicPattern", jobArgsConfig.getEventTopicPattern());
            jobArgs.put("groupId", jobArgsConfig.getGroupId());
            jobArgs.put("fromOffsetTime", jobArgsConfig.getFromOffsetTime());
            jobArgs.put("deserializerClass", jobArgsConfig.getDeserializerClass());
            jobArgs.put("keyByExprPath", jobArgsConfig.getKeyByExprPath());
            // FLINK basic options.
            jobArgs.put("runtimeMode", jobArgsConfig.getRuntimeMode());
            jobArgs.put("restartAttempts", jobArgsConfig.getRestartAttempts());
            jobArgs.put("restartDelaySeconds", jobArgsConfig.getRestartDelaySeconds());
            // FLINK Checkpoint options.
            jobArgs.put("checkpointDir", jobArgsConfig.getCheckpointDir());
            jobArgs.put("checkpointMode", jobArgsConfig.getCheckpointMode());
            jobArgs.put("checkpointIntervalMs", jobArgsConfig.getCheckpointIntervalMs());
            jobArgs.put("checkpointMinPauseBetween", jobArgsConfig.getCheckpointMinPauseBetween());
            jobArgs.put("checkpointMaxConcurrent", jobArgsConfig.getCheckpointMaxConcurrent());
            jobArgs.put("externalizedCheckpointCleanup", jobArgsConfig.getExternalizedCheckpointCleanup());
            // FLINK Performance options.
            jobArgs.put("parallelis", jobArgsConfig.getParallelis());
            jobArgs.put("maxParallelism", jobArgsConfig.getMaxParallelism());
            jobArgs.put("bufferTimeoutMillis", jobArgsConfig.getBufferTimeoutMillis());
            jobArgs.put("outOfOrdernessMillis", jobArgsConfig.getOutOfOrdernessMillis());
            jobArgs.put("idleTimeoutMillis", jobArgsConfig.getIdleTimeoutMillis());
            // FLINK Sink options.
            jobArgs.put("forceUsePrintSink", jobArgsConfig.getForceUsePrintSink());
            // FLINK ControllerLog options.
            jobArgs.put("jobName", jobArgsConfig.getJobName());
            // FLINK CEP job options.
            jobArgs.put("cepPatterns", "TODO"); // TODO
            jobArgs.put("inProcessingTime", jobArgsConfig.getInProcessingTime());
            jobArgs.put("alertTopic", jobArgsConfig.getAlertTopic());
            // FLINK CEP job with kafka options.
            jobArgs.put("offsetResetStrategy", jobArgsConfig.getOffsetResetStrategy());
            jobArgs.put("partitionDiscoveryIntervalMs", jobArgsConfig.getPartitionDiscoveryIntervalMs());

            final String jobArgsLine = jobArgs.entrySet()
                    .stream()
                    .map(e -> format("--%s %s", e.getKey(), e.getValue()))
                    .collect(joining(","));

            log.info("Run flink job args line : {}", jobArgsLine);
            final JarRunResponseBody response = getFlinkApi().runJar(jar.getId(), true, null, null, jobArgsLine,
                    jobArgsConfig.getEntryClass(), jobArgsConfig.getParallelis());

            final JobDetailsInfo jobDetails = getFlinkApi().getJobDetails(response.getJobid());
            // TODO
            if (jobDetails.getState() == StateEnum.RUNNING) {
            }

            updateControllerRunState(controllerId, RunState.SUCCESS);

            upsertControllerLog(controllerId, controllerLog.getId(), false, true, true, _jobLog -> {
                _jobLog.setDetails(FlinkSubmitControllerLog.builder()
                        .jarId(jar.getId())
                        .jobId(response.getJobid())
                        .jobArgs(jobArgs)
                        .name(jobDetails.getName())
                        .isStoppable(jobDetails.isIsStoppable())
                        .state(FlinkJobState.valueOf(jobDetails.getState().name()))
                        .startTime(jobDetails.getStartTime())
                        .endTime(jobDetails.getEndTime())
                        .duration(jobDetails.getDuration())
                        .now(jobDetails.getNow())
                        .timestamps(jobDetails.getTimestamps())
                        .statusCounts(jobDetails.getStatusCounts())
                        .build());
            });

        } catch (Throwable ex) {
            final String errmsg = format(
                    "Failed to executing requests job of currentShardingTotalCount: %s, context: %s, controllerId: %s, controllerLogId: %s",
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

    protected File obtainFlinkJobJarFile(FlinkSubmitExecutionConfig fssc) {
        final URI jarUri = URI.create(fssc.getJobFileUrl());
        final String scheme = isBlank(jarUri.getScheme()) ? "file://" : jarUri.getScheme();
        final String resourcePath = jarUri.getRawPath();
        Assert2.isTrue(resourcePath.contains("/"), "invalid resourcePath %s, missing is '/'", resourcePath);
        final String localTmpPath = DEFAULT_CONTROLLER_JAR_TMP_DIR.concat("/")
                .concat(resourcePath.substring(resourcePath.lastIndexOf("/")))
                .concat("-")
                .concat(valueOf(currentTimeMillis()))
                .concat(getFilenameExtension(resourcePath));

        try {
            if (equalsAnyIgnoreCase(scheme, "file://")) {
                return new File(fssc.getJobFileUrl());
            } else if (equalsAnyIgnoreCase(scheme, "http://", "https://")) {
                log.info("Downloading flink job jar file to '{}' from remote : '%s'", localTmpPath, resourcePath);

                try (ReadableByteChannel channel = Channels.newChannel(jarUri.toURL().openStream());
                        FileOutputStream fos = new FileOutputStream(localTmpPath, false);) {
                    fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                }
            } else if (equalsAnyIgnoreCase(scheme, "minio://", "s3://")) {
                final MinioClientProperties config = getMinioManager().getConfig();

                // The S3 specification must not prefix is '/'.
                final String objectPrefix = startsWith(resourcePath, "/") ? resourcePath.substring(1) : resourcePath;
                log.info("Downloading flink job jar file to '{}' from S3 : '%s'", localTmpPath, objectPrefix);

                getMinioManager().getMinioClient()
                        .downloadObject(DownloadObjectArgs.builder()
                                .bucket(config.getBucket())
                                .region(config.getRegion())
                                .object(objectPrefix)
                                .filename(localTmpPath)
                                .build());
            }
            log.info("Downloaded flink job jar file '{}', length: {}", localTmpPath, new File(localTmpPath).length());

        } catch (Throwable ex) {
            log.error(format(
                    "Failed to obtain flink job jar file : '%s', The currently only supported protocols are: minio://, s3://, http://, https://, file://",
                    fssc.getJobFileUrl()), ex);
        }

        throw new UnsupportedOperationException(format("No supported job jar URI : '%s'. ", fssc.getJobFileUrl()));
    }

}