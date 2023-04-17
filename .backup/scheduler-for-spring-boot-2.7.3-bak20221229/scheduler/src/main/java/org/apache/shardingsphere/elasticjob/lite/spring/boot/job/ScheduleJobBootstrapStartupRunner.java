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
package org.apache.shardingsphere.elasticjob.lite.spring.boot.job;

import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link ScheduleJobBootstrapStartupRunner}
 * 
 * @author James Wong
 * @date 2022-10-16
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobBootstrapConfiguration}
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ElasticJobLiteAutoConfiguration}
 * @see {@link org.apache.shardingsphere.elasticjob.lite.spring.boot.job.ScheduleJobBootstrapStartupRunner}
 */
@Slf4j
public class ScheduleJobBootstrapStartupRunner implements ApplicationRunner {

    private @Autowired ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting collector jobs bootstrap ...");
        applicationContext.getBeansOfType(ScheduleJobBootstrap.class).values().forEach(ScheduleJobBootstrap::schedule);
        log.info("Collector jobs bootstrap started.");
    }

}
