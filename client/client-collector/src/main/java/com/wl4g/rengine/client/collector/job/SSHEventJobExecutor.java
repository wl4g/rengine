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

import javax.validation.constraints.NotBlank;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.infra.exception.JobConfigurationException;
import org.apache.shardingsphere.elasticjob.infra.exception.JobSystemException;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.wl4g.infra.common.cli.ssh2.SSH2Holders;
import com.wl4g.infra.common.cli.ssh2.SSH2Holders.Ssh2ExecResult;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link SSHEventJobExecutor}
 * 
 * @author James Wong
 * @version 2022-10-20
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.HttpJobExecutor}
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.ScriptJobExecutor}
 */
@Getter
public class SSHEventJobExecutor extends EventJobExecutor<SSHEventJobExecutor.SSHJobParam> {

    @Override
    public EventJobType type() {
        return EventJobType.SSH;
    }

    @Override
    public void execute(SSHJobParam shardingParam, JobConfiguration jobConfig, JobFacade jobFacade, ShardingContext context) {
        String host = shardingParam.getHost();
        int port = shardingParam.getPort();
        String user = shardingParam.getUser();
        String password = shardingParam.getPassword();
        char[] privateKey = shardingParam.getPrivateKey().toCharArray();
        long timeoutMs = shardingParam.getReadTimeoutMs();
        String command = shardingParam.getCommand();
        if (Strings.isNullOrEmpty(command)) {
            throw new JobConfigurationException("Cannot find script command line, job is not executed.");
        }
        try {
            Ssh2ExecResult result = SSH2Holders.getDefault()
                    .execWaitForResponse(host, port, user, privateKey, password, command, timeoutMs);

            offer(shardingParam, jobConfig, jobFacade, context, result.getMessage());
        } catch (Exception ex) {
            throw new JobSystemException("Failed to ssh execute.", ex);
        }
    }

    @Override
    protected BodyConverter getBodyConverter(SSHJobParam shardingParam, JobConfiguration jobConfig, ShardingContext context) {
        // Ignore, if it is special job, it should be override.
        return fromResult -> fromResult;
    }

    @Override
    protected List<String> getPrincipals(SSHJobParam shardingParam, JobConfiguration jobConfig, ShardingContext context) {
        String host = shardingParam.getHost();
        int port = shardingParam.getPort();
        // Notice: Since the ":" separator is not allowed if the backend storage
        // is HBase (because it is stipulated that RowKey uses ":" to conform to
        // splicing), see: com.wl4g.rengine.common.event.RengineEvent#validate()
        return Lists.newArrayList(host + "_" + port);
    }

    @Override
    protected EventLocation getEventLocation(SSHJobParam shardingParam, JobConfiguration jobConfig, ShardingContext context) {
        return EventLocation.builder().ipAddress(shardingParam.getHost()).build();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SSHJobParam extends EventJobExecutor.JobParamBase {
        private String host;
        private int port;
        private String user;
        private String password;
        private String privateKey;
        @NotBlank(message = "Cannot find script command line, job is not executed.")
        private String command;
    }

}
