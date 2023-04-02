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
import static com.wl4g.infra.common.lang.StringUtils2.eqIgnCase;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.rengine.common.constants.RengineConstants.DEFAULT_CONTROLLER_JAR_TMP_DIR;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWith;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.nextbreakpoint.flinkclient1_15.api.ApiClient;
import com.nextbreakpoint.flinkclient1_15.api.ApiException;
import com.nextbreakpoint.flinkclient1_15.api.DefaultApi;
import com.nextbreakpoint.flinkclient1_15.model.JarFileInfo;
import com.nextbreakpoint.flinkclient1_15.model.JarListInfo;
import com.nextbreakpoint.flinkclient1_15.model.JarRunResponseBody;
import com.nextbreakpoint.flinkclient1_15.model.JarUploadResponseBody;
import com.nextbreakpoint.flinkclient1_15.model.JobDetailsInfo;
import com.nextbreakpoint.flinkclient1_15.model.JobDetailsInfo.StateEnum;
import com.nextbreakpoint.flinkclient1_15.model.JobIdsWithStatusOverview;
import com.squareup.okhttp.OkHttpClient;
import com.wl4g.infra.common.codec.Encodes;
import com.wl4g.infra.common.io.FileIOUtils;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.rengine.common.entity.Controller;
import com.wl4g.rengine.common.entity.Controller.ControllerType;
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

// TODO 当前使用 restapi 提交 job 适用于 session 模式，后续应增加配置，并自动检测环境(rengine-controller)是 k8s 或 VM, 
// 分别是启动 rengine-job pod 还是通过 ssh 远程命令行启动 rengine-job JVM进程 ?
/**
 * Notice: The flink cep job can be automatically scheduled, but currently it is
 * recommended to use a professional scheduling platform such as Aws EMR or
 * dolphinscheduler.
 * 
 * @author James Wong
 * @version 2023-01-11
 * @since v1.0.0
 * @see https://nightlies.apache.org/flink/flink-docs-release-1.15/docs/ops/rest_api/#jobmanager
 * @see https://nightlies.apache.org/flink/flink-docs-release-1.15/generated/rest_v1_dispatcher.yml
 * @see https://github1s.com/apache/flink/blob/release-1.14/flink-runtime/src/main/java/org/apache/flink/runtime/rest/RestServerEndpoint.java
 * @see https://github1s.com/wl4g-collect/flink-client/blob/master/src/test/java/com/nextbreakpoint/FlinkClientIT.java#L88-L89
 * @see https://mvnrepository.com/artifact/com.nextbreakpoint/com.nextbreakpoint.flinkclient
 */
@Getter
@CustomLog
public class FlinkSubmitController extends AbstractJobExecutor {

    private DefaultApi flinkApi;

    @Override
    public String getType() {
        return ControllerJobType.FLINK_SUBMITTER.name();
    }

    protected DefaultApi getDefaultApi() {
        if (isNull(flinkApi)) {
            synchronized (this) {
                if (isNull(flinkApi)) {
                    final EngineFlinkSchedulerProperties flinkConfig = getConfig().getFlink().validate();
                    final ApiClient apiClient = new ApiClient();
                    if (nonNull(flinkConfig.getDebugging())) {
                        apiClient.setDebugging(flinkConfig.getDebugging());
                    }
                    apiClient.setBasePath(flinkConfig.getEndpoint());
                    apiClient.setHttpClient(new OkHttpClient());
                    apiClient.setUserAgent(FlinkSubmitController.class.getSimpleName()
                            .concat(format("/%s/java", FlinkSubmitController.class.getPackage().getImplementationVersion())));
                    if (nonNull(flinkConfig.getConnTimeout())) {
                        apiClient.setConnectTimeout(flinkConfig.getConnTimeout());
                    }
                    if (nonNull(flinkConfig.getReadTimeout())) {
                        apiClient.setReadTimeout(flinkConfig.getReadTimeout());
                    }
                    // for upload jars
                    if (nonNull(flinkConfig.getWriteTimeout())) {
                        apiClient.setWriteTimeout(flinkConfig.getWriteTimeout());
                    }
                    if (nonNull(flinkConfig.getVerifyingSsl())) {
                        apiClient.setVerifyingSsl(flinkConfig.getVerifyingSsl());
                    }
                    if (nonNull(flinkConfig.getSslCaCert())) {
                        apiClient.setSslCaCert(flinkConfig.getSslCaCert());
                    }
                    // for basic auth
                    if (!isBlank(flinkConfig.getUsername()) && !isBlank(flinkConfig.getPassword())) {
                        apiClient.setUsername(flinkConfig.getUsername());
                        apiClient.setPassword(flinkConfig.getPassword());
                    }
                    // for api auth
                    if (!isBlank(flinkConfig.getApiKey())) {
                        apiClient.setApiKey(flinkConfig.getApiKey());
                    }
                    if (!isBlank(flinkConfig.getApiKeyPrefix())) {
                        apiClient.setApiKeyPrefix(flinkConfig.getApiKeyPrefix());
                    }
                    // for oauth auth
                    if (!isBlank(flinkConfig.getAccessToken())) {
                        apiClient.setAccessToken(flinkConfig.getAccessToken());
                    }
                    this.flinkApi = new DefaultApi(apiClient);
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
            if (!checkJobAlready(fssc)) {
                return;
            }
            final var jars = uploadJars(fssc);
            final var jobArgs = buildFlinkJobArgs(fssc);
            final String jobArgsLine = jobArgs.entrySet()
                    .stream()
                    .map(e -> format("--%s %s", e.getKey(), e.getValue()))
                    .collect(joining(","));
            log.info("Run flink job args line : {}", jobArgsLine);

            // TODO multi jars dependencies, The current flink rest run api does
            // not support multiple jars ???
            final JarRunResponseBody response = getDefaultApi().jarsJaridRunPost(jars.get(0).getId(), null, true, null, null,
                    jobArgsLine, jobArgsConfig.getEntryClass(), jobArgsConfig.getParallelis());

            final JobDetailsInfo jobDetails = getDefaultApi().jobsJobidGet(response.getJobid());
            if (jobDetails.getState() == StateEnum.CREATED || jobDetails.getState() == StateEnum.INITIALIZING
                    || jobDetails.getState() == StateEnum.RESTARTING || jobDetails.getState() == StateEnum.RUNNING) {
                updateControllerRunState(controllerId, RunState.SUCCESS);
            } else {
                updateControllerRunState(controllerId, RunState.FAILED);
            }

            upsertControllerLog(controllerId, controllerLog.getId(), false, true, true, _jobLog -> {
                _jobLog.setDetails(FlinkSubmitControllerLog.builder()
                        .jarId(jars.get(0).getId())
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

    protected boolean checkJobAlready(FlinkSubmitExecutionConfig fssc) {
        try {
            JobIdsWithStatusOverview allJobInfo = getDefaultApi().jobsGet();
            log.info("Found all flink job info : {}", allJobInfo);

            // Check for already job?
            if (nonNull(allJobInfo)) {
                final List<JobDetailsInfo> existingJobDetailsList = safeList(allJobInfo.getJobs()).parallelStream().map(job -> {
                    try {
                        final JobDetailsInfo jobDetails = getDefaultApi().jobsJobidGet(job.getId());
                        if (nonNull(jobDetails) && eqIgnCase(jobDetails.getName(), fssc.getJobArgs().getJobName())
                                && isExistingJob(jobDetails)) {
                            return jobDetails;
                        }
                        return null;
                    } catch (Throwable ex) {
                        throw new IllegalStateException(ex);
                    }
                }).filter(jd -> nonNull(jd)).collect(toList());

                if (!existingJobDetailsList.isEmpty()) {
                    log.info("The flink jobs is already running. - {}", existingJobDetailsList);
                    return false;
                }
            }
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }

        return true;
    }

    protected List<JarFileInfo> uploadJars(FlinkSubmitExecutionConfig fssc) {
        try {
            // e.g: jobinfra/rengine-job-base-1.0.0.jar
            // e.g: jobinfra/rengine-job-base-1.0.0-jar-with-dependencies.jar
            final var uploads = safeList(fssc.getJobJarUrls()).parallelStream().map(jarUrl -> {
                final File jarFile = obtainFlinkJobJarFile(jarUrl);
                try {
                    // see:com.nextbreakpoint.flinkclient1_15.api.ApiClient#serialize()
                    JarUploadResponseBody upload = getDefaultApi().jarsUploadPost(jarFile);
                    if (isNull(upload)) {
                        throw new IllegalStateException(format("Failed to upload jars for : %s", jarFile));
                    }
                    return upload;
                } catch (ApiException ex) {
                    throw new IllegalArgumentException(ex);
                }
            }).collect(toList());

            final JarListInfo allJars = getDefaultApi().jarsGet();
            if (isNull(allJars)) {
                throw new IllegalStateException(format("Unable to get flink upload jars."));
            }
            final List<JarFileInfo> jars = safeList(allJars.getFiles()).stream()
                    .filter(j -> matchJars(uploads, j))
                    .collect(toList());

            if (jars.isEmpty()) {
                throw new IllegalStateException(format("Could't find jar of flink uploads : {}", uploads));
            }
            return jars;
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected File obtainFlinkJobJarFile(@NotBlank String jarUrl) {
        final URI jarUri = URI.create(jarUrl);
        final String scheme = isBlank(jarUri.getScheme()) ? "file" : jarUri.getScheme();
        final String resourcePath = jarUri.getRawPath();
        Assert2.isTrue(resourcePath.contains("/"), "invalid resourcePath %s, missing is '/'", resourcePath);
        final File localTmpFile = new File(
                DEFAULT_CONTROLLER_JAR_TMP_DIR.concat("/").concat(resourcePath.substring(resourcePath.lastIndexOf("/"))))
        // .concat("-")
        // .concat(valueOf(currentTimeMillis()))
        // .concat(".")
        // .concat(getFilenameExtension(resourcePath))
        ;
        if (localTmpFile.exists()) {
            return localTmpFile;
        }

        try {
            FileIOUtils.forceMkdirParent(localTmpFile);

            if (equalsAnyIgnoreCase(scheme, "file")) {
                return new File(jarUrl);
            } else if (equalsAnyIgnoreCase(scheme, "http", "https")) {
                log.info("Downloading flink job jar file to '{}' from remote : '%s'", localTmpFile, resourcePath);

                try (ReadableByteChannel channel = Channels.newChannel(jarUri.toURL().openStream());
                        FileOutputStream fos = new FileOutputStream(localTmpFile, false);) {
                    fos.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                }
            }
            // Similar to the ossref protocol of Alibaba EMR.
            else if (equalsAnyIgnoreCase(scheme, "minio", "s3ref")) {
                final MinioClientProperties config = getMinioManager().getConfig();

                // The S3 specification must not prefix is '/'.
                final String objectPrefix = startsWith(resourcePath, "/") ? resourcePath.substring(1) : resourcePath;
                log.info("Downloading flink job jar file to '{}' from s3ref : '%s'", localTmpFile, objectPrefix);

                getMinioManager().getMinioClient()
                        .downloadObject(DownloadObjectArgs.builder()
                                .bucket(config.getBucket())
                                .region(config.getRegion())
                                .object(objectPrefix)
                                .filename(localTmpFile.getAbsolutePath())
                                .build());
            }
            log.info("Downloaded flink job jar file '{}', length: {}", localTmpFile, localTmpFile.length());

            return localTmpFile;
        } catch (Throwable ex) {
            log.error(format(
                    "Failed to obtain flink job jar file : '%s', The currently only supported protocols are: minio://, s3ref://, http://, https://, file://",
                    jarUrl), ex);
        }

        throw new UnsupportedOperationException(format("No supported job jar URIs : '%s'. ", jarUrl));
    }

    protected boolean isExistingJob(JobDetailsInfo jobDetails) {
        return jobDetails.getState() == StateEnum.CREATED || jobDetails.getState() == StateEnum.INITIALIZING
                || jobDetails.getState() == StateEnum.RECONCILING || jobDetails.getState() == StateEnum.RESTARTING
                || jobDetails.getState() == StateEnum.CANCELLING || jobDetails.getState() == StateEnum.FAILING
                || jobDetails.getState() == StateEnum.RUNNING;
    }

    protected boolean matchJars(List<JarUploadResponseBody> uploads, JarFileInfo jar) {
        // for example:
        // upload.filename=/tmp/flink-web-04861467-bc42-4f0e-aa0b-7530040ae21d/flink-web-upload/41bf260e-9132-4f97-b9ec-1998f4ebc6d5_rengine-job-base-1.0.0.jar
        // jar.id=41bf260e-9132-4f97-b9ec-1998f4ebc6d5_rengine-job-base-1.0.0.jar
        // jar.name=rengine-job-base-1.0.0.jar
        return safeList(uploads).stream().anyMatch(upload -> {
            final String uploadName = upload.getFilename().substring(upload.getFilename().lastIndexOf("/"));
            return StringUtils2.equals(uploadName, jar.getId().concat("_").concat(jar.getName()));
        });
    }

    protected Map<String, Object> buildFlinkJobArgs(FlinkSubmitExecutionConfig fssc) {
        final FlinkJobArgs jobArgsConfig = fssc.getJobArgs();

        final Map<String, Object> jobArgs = new LinkedHashMap<>(36);
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
        jobArgs.put("cepPatterns", Encodes.encodeBase64(jobArgsConfig.getCepPatterns()));
        jobArgs.put("inProcessingTime", jobArgsConfig.getInProcessingTime());
        jobArgs.put("alertTopic", jobArgsConfig.getAlertTopic());
        // FLINK CEP job with kafka options.
        jobArgs.put("offsetResetStrategy", jobArgsConfig.getOffsetResetStrategy());
        jobArgs.put("partitionDiscoveryIntervalMs", jobArgsConfig.getPartitionDiscoveryIntervalMs());

        return jobArgs;
    }

    @Override
    protected ControllerLog newDefaultControllerLog(Long controllerId) {
        return ControllerLog.builder()
                .controllerId(controllerId)
                .details(FlinkSubmitControllerLog.builder().type(ControllerType.FLINK_SUBMITTER.name()).build())
                .build();
    }

}