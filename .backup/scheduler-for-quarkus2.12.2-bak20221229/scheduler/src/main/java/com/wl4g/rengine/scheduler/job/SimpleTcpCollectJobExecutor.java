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
package com.wl4g.rengine.scheduler.job;

import java.net.Socket;

import javax.inject.Singleton;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;

import com.google.common.io.ByteStreams;
import com.wl4g.infra.common.codec.CodecSource;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link SimpleTcpCollectJobExecutor}
 * 
 * @author James Wong
 * @date 2022-10-26
 * @since v3.0.0
 */
@Singleton
public class SimpleTcpCollectJobExecutor extends CollectJobExecutor<SimpleTcpCollectJobExecutor.SimpleTcpJobParam> {

    @Override
    protected void execute(
            SimpleTcpJobParam shardingParam,
            int currentShardingTotalCount,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) throws Exception {

        try (Socket socket = new Socket(shardingParam.getHost(), shardingParam.getPort());) {
            // Write message to server.
            socket.getOutputStream().write(CodecSource.fromBase64(shardingParam.getBase64Message()).getBytes());
            // Read message from server.
            byte[] result = ByteStreams.toByteArray(socket.getInputStream());
            log.debug("Collect to result: {}", result);

            offer(shardingParam, jobConfig, jobFacade, context, result);
        }

    }

    @Override
    protected EventJobType type() {
        return EventJobType.SIMPLE_TCP;
    }

    protected BodyConverter getBodyConverter(
            SimpleTcpJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        return BodyConverter.DEFAULT_STRING;
    }

    @Override
    protected EventLocation getEventLocation(
            SimpleTcpJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        return EventLocation.builder().ipAddress(shardingParam.getHost()).build();
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleTcpJobParam extends CollectJobExecutor.JobParamBase {
        private String host = "localhost";
        private int port = 32000;
        private String base64Message;
        // private String readEOFChar; // TODO supported custom end char?
    }

}
