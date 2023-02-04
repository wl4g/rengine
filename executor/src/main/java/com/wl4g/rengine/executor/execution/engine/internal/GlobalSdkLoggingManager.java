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
package com.wl4g.rengine.executor.execution.engine.internal;

import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.TypeConverts.parseLongOrNull;
import static com.wl4g.rengine.common.util.ScriptEngineUtil.getAllLogDirs;
import static com.wl4g.rengine.common.util.ScriptEngineUtil.getAllLogFilenames;

import java.io.File;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.wl4g.infra.common.task.QuartzUtils2;
import com.wl4g.rengine.common.entity.UploadObject;
import com.wl4g.rengine.executor.execution.ExecutionConfig;
import com.wl4g.rengine.executor.meter.RengineExecutorMeterService;
import com.wl4g.rengine.executor.minio.MinioConfig;
import com.wl4g.rengine.executor.minio.MinioManager;

import io.minio.ObjectWriteArgs;
import io.quarkus.runtime.StartupEvent;
import lombok.CustomLog;

/**
 * {@link GlobalSdkLoggingManager}
 * 
 * @author James Wong
 * @version 2023-01-16
 * @since v1.0.0
 */
@CustomLog
@Singleton
public class GlobalSdkLoggingManager {

    @Inject
    ExecutionConfig executionConfig;

    @Inject
    RengineExecutorMeterService meterService;

    @Inject
    MinioConfig minioConfig;

    @Inject
    MinioManager minioManager;

    @Inject
    org.quartz.Scheduler scheduler;

    void init(/* @Observes */ StartupEvent event) {
        // Script logging uploader job.
        // see:https://quarkus.io/guides/quartz#creating-the-maven-project
        try {
            // Create the job
            final String jobId = "job-".concat(ScriptLoggingUploader.class.getSimpleName())
                    .concat("@")
                    .concat(getClass().getSimpleName());
            final var uploaderJob = QuartzUtils2.newDefaultJobDetail(jobId, ScriptLoggingUploader.class);

            // Create the trigger
            final String triggerId = "trigger-".concat(ScriptLoggingUploader.class.getSimpleName())
                    .concat("@")
                    .concat(getClass().getSimpleName());
            final var uploaderTrigger = QuartzUtils2.newDefaultJobTrigger(triggerId, executionConfig.log().uploaderCron(), true,
                    new JobDataMap() {
                        private static final long serialVersionUID = 1L;
                        {
                            put(ExecutionConfig.class.getName(), executionConfig);
                            put(MinioConfig.class.getName(), minioConfig);
                            put(MinioManager.class.getName(), minioManager);
                        }
                    });

            // The schedule
            this.scheduler.scheduleJob(uploaderJob, uploaderTrigger);

            log.info("Scheduled script logging uploader of job: {}, trigger: {}", uploaderJob, uploaderTrigger);
        } catch (SchedulerException e) {
            log.error("Failed to schedule script logging uploader.", e);
        }
    }

    public static class ScriptLoggingUploader implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            log.info("Scanning upload script logs to MinIO ...");

            final JobDataMap jobDataMap = context.getTrigger().getJobDataMap();
            final ExecutionConfig config = (ExecutionConfig) jobDataMap.get(ExecutionConfig.class.getName());
            final MinioConfig minioConfig = (MinioConfig) jobDataMap.get(MinioConfig.class.getName());
            final MinioManager minioManager = (MinioManager) jobDataMap.get(MinioManager.class.getName());
            notNullOf(config, "config");
            notNullOf(minioManager, "minioManager");
            notNullOf(minioConfig, "minioConfig");

            // Scanner all script logs upload to MinIO.
            getAllLogDirs(config.log().baseDir(), false).parallelStream().forEach(dirname -> {
                final Long workflowId = notNullOf(parseLongOrNull(dirname), "workflowId");
                log.info("Scan script log dir for workflowId: {}", workflowId);

                getAllLogFilenames(config.log().baseDir(), workflowId, true).parallelStream()
                        .map(f -> new File(f))
                        // TODO
                        // 1) S3/minio limit min size for 5MB.
                        // 2) Must check that the log file was generated
                        // yesterday or before and has run to finished.
                        .filter(f -> f.length() > ObjectWriteArgs.MIN_MULTIPART_SIZE)
                        .forEach(f -> {
                            final String objectPrefix = minioConfig.bucket()
                                    .concat("/")
                                    .concat(UploadObject.UploadType.SCRIPT_LOG.getPrefix())
                                    .concat("/")
                                    .concat(f.getName());

                            log.info("Uploading script log to {} from {}", objectPrefix, f);
                            minioManager.uploadObject(objectPrefix, f.getAbsolutePath());
                        });
            });
        }
    }

}
