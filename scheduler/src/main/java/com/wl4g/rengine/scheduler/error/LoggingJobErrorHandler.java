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
package com.wl4g.rengine.scheduler.error;

import java.util.Properties;

import org.apache.shardingsphere.elasticjob.error.handler.JobErrorHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link LoggingJobErrorHandler}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v3.0.0
 */
@Slf4j
public class LoggingJobErrorHandler implements JobErrorHandler {

    @Override
    public String getType() {
        return "PRINT";
    }

    @Override
    public void init(Properties props) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleException(String jobName, Throwable cause) {
        log.error("------------ SchedulingJob Error Printing: ------------\n", cause);
    }

}
