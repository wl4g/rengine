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
package com.wl4g.rengine.client.collector.job;

import java.util.List;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.wl4g.rengine.client.collector.job.EventJobExecutor.JobParamBase;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link SimpleTcpEventJobExecutor}
 * 
 * @author James Wong
 * @version 2022-10-26
 * @since v3.0.0
 */
public class SimpleTcpEventJobExecutor extends EventJobExecutor<SimpleTcpEventJobExecutor.SimpleTcpJobParam> {

    @Override
    protected void execute(
            SimpleTcpJobParam shardingParam,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    protected EventJobType type() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected BodyConverter getBodyConverter(
            SimpleTcpJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected List<String> getPrincipals(
            SimpleTcpJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected EventLocation getEventLocation(
            SimpleTcpJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        // TODO Auto-generated method stub
        return null;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleTcpJobParam extends JobParamBase {
        private String host;
        private String port;
        private String base64Message;
    }

}
