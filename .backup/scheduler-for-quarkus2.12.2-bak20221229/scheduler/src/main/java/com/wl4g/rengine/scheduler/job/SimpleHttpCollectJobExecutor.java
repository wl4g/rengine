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

import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static com.wl4g.infra.common.lang.TypeConverts.safeLongToInt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import javax.validation.constraints.NotBlank;

import org.apache.shardingsphere.elasticjob.api.JobConfiguration;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.executor.JobFacade;
import org.apache.shardingsphere.elasticjob.infra.exception.JobExecutionException;
import org.apache.shardingsphere.elasticjob.infra.json.GsonFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.wl4g.rengine.common.event.RengineEvent.EventLocation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * {@link SimpleHttpCollectJobExecutor}
 * 
 * @author James Wong
 * @date 2022-10-20
 * @since v1.0.0
 * @see {@link org.apache.shardingsphere.elasticjob.http.executor.HttpJobExecutor}
 */
@Singleton
public class SimpleHttpCollectJobExecutor extends CollectJobExecutor<SimpleHttpCollectJobExecutor.SimpleHttpJobParam> {

    @Override
    public EventJobType type() {
        return EventJobType.SIMPLE_HTTP;
    }

    @Override
    protected void execute(
            SimpleHttpJobParam shardingParam,
            int currentShardingTotalCount,
            JobConfiguration jobConfig,
            JobFacade jobFacade,
            ShardingContext context) throws Exception {

        HttpURLConnection connection = null;
        try {
            URL url = new URL(shardingParam.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(shardingParam.getMethod());
            connection.setDoOutput(true);
            connection.setConnectTimeout(safeLongToInt(shardingParam.getConnectTimeoutMs()));
            connection.setReadTimeout(safeLongToInt(shardingParam.getReadTimeoutMs()));
            // The default headers.
            connection.setRequestProperty(shardingParam.getContextHeaderName(), GsonFactory.getGson().toJson(context));
            // The custom headers.
            final HttpURLConnection _connection = connection;
            safeMap(shardingParam.getHeaders()).forEach((name, value) -> _connection.setRequestProperty(name, value));
            // Request
            connection.connect();
            String data = shardingParam.getBody();
            if (isWriteMethod(shardingParam.getMethod()) && !Strings.isNullOrEmpty(data)) {
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(data.getBytes(StandardCharsets.UTF_8));
                }
            }
            int code = connection.getResponseCode();
            InputStream resultInputStream;
            if (isRequestSucceed(code)) {
                resultInputStream = connection.getInputStream();
            } else {
                log.warn("HTTP collect job {} executed with response code {}", jobConfig.getJobName(), code);
                resultInputStream = connection.getErrorStream();
            }
            StringBuilder result = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(resultInputStream, StandardCharsets.UTF_8))) {
                String line;
                while (null != (line = bufferedReader.readLine())) {
                    result.append(line);
                }
            }
            if (isRequestSucceed(code)) {
                log.debug("HTTP collect job executed result : {}",
                        result.toString().substring(0, Math.min(result.length(), 1024)));
                offer(shardingParam, jobConfig, jobFacade, context, result.toString());
            } else {
                log.warn("HTTP collect job {} executed with response body {}", jobConfig.getJobName(),
                        result.substring(0, Math.min(result.length(), 1024)));
            }
        } catch (final IOException ex) {
            throw new JobExecutionException(ex);
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
    }

    @Override
    protected EventLocation getEventLocation(
            SimpleHttpJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext context) {
        URI uri = URI.create(shardingParam.getUrl());
        return EventLocation.builder().ipAddress(uri.getHost()).build();
    }

    protected boolean isWriteMethod(final String method) {
        return Arrays.asList("POST", "PUT", "DELETE").contains(method.toUpperCase());
    }

    protected boolean isRequestSucceed(final int httpStatusCode) {
        return HttpURLConnection.HTTP_BAD_REQUEST > httpStatusCode;
    }

    @Override
    protected BodyConverter getBodyConverter(
            SimpleHttpJobParam shardingParam,
            JobConfiguration jobConfig,
            ShardingContext shardingContext) {
        return BodyConverter.DEFAULT_STRING;
    }

    @Override
    protected List<String> getPrincipals(SimpleHttpJobParam shardingParam, JobConfiguration jobConfig, ShardingContext context) {
        URI uri = URI.create(shardingParam.getUrl());
        // Notice: Since the ":" separator is not allowed if the backend storage
        // is HBase (because it is stipulated that RowKey uses ":" to conform to
        // splicing), see: com.wl4g.rengine.common.event.RengineEvent#validate()
        return Lists.newArrayList(uri.getHost() + "_" + uri.getPort());
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class SimpleHttpJobParam extends CollectJobExecutor.JobParamBase {

        /**
         * The collect HTTP to target URL.
         */
        private @NotBlank String url = "http://localhost:8080/event";

        private @NotBlank String method = "GET";

        private @Nullable Map<String, String> headers = new HashMap<>();

        private @Nullable String body;

        private @NotBlank String contextHeaderName = "X-Job-Context";
    }

}
