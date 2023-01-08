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
package com.wl4g.rengine.client.core;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wl4g.infra.common.remoting.uri.UriComponentsBuilder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.client.core.config.ClientConfig;
import com.wl4g.rengine.client.core.exception.ClientExecuteException;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.common.util.IdGenUtil;

import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * {@link RengineClient}
 * 
 * @author James Wong
 * @version 2022-10-10
 * @since v1.0.0
 */
@Slf4j
@SuperBuilder
public class RengineClient {
    private @Default ClientConfig config = new ClientConfig();
    private @Default OkHttpClient httpClient = new OkHttpClient().newBuilder().build();
    private @Default Function<Throwable, ExecuteResult> defaultFailback = new DefaultFailback();

    public ExecuteResult execute(@NotEmpty List<String> scenesCodes, @Nullable Map<String, Object> args) {
        return execute(IdGenUtil.next(), scenesCodes, ExecuteRequest.DEFAULT_TIMEOUT, ExecuteRequest.DEFAULT_BESTEFFORT, args, null);
    }

    public ExecuteResult execute(
            @NotEmpty List<String> scenesCodes,
            @Min(1) Long timeoutMs,
            @Nullable Map<String, Object> args) {
        return execute(IdGenUtil.next(), scenesCodes, timeoutMs, ExecuteRequest.DEFAULT_BESTEFFORT, args, null);
    }

    public ExecuteResult execute(
            @NotEmpty List<String> scenesCodes,
            @NotNull Boolean bestEffort,
            @Nullable Map<String, Object> args) {
        return execute(IdGenUtil.next(), scenesCodes, ExecuteRequest.DEFAULT_TIMEOUT, bestEffort, args, null);
    }

    public ExecuteResult execute(
            String requestId,
            @NotEmpty List<String> scenesCodes,
            @NotNull Boolean bestEffort,
            @Min(1) Long timeoutMs,
            @Nullable Map<String, Object> args) {
        return execute(requestId, scenesCodes, timeoutMs, bestEffort, args, null);
    }

    public ExecuteResult execute(
            String requestId,
            @NotEmpty List<String> scenesCodes,
            @NotNull @Min(1) Long timeoutMs,
            @NotNull Boolean bestEffort,
            @Nullable Map<String, Object> args) {
        return execute(requestId, scenesCodes, timeoutMs, bestEffort, args, null);
    }

    public ExecuteResult execute(
            String requestId,
            @NotEmpty List<String> scenesCodes,
            @NotNull @Min(1) Long timeoutMs,
            @NotNull Boolean bestEffort,
            @Nullable Map<String, Object> args,
            Function<Throwable, ExecuteResult> failback) {
        notEmptyOf(scenesCodes, "scenesCodes");
        return execute(ExecuteRequest.builder()
                .requestId(valueOf(requestId))
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .scenesCodes(scenesCodes)
                .timeout(timeoutMs)
                .bestEffort(bestEffort)
                .args(args)
                .build(), failback);
    }

    public ExecuteResult execute(@NotNull ExecuteRequest executeRequest, Function<Throwable, ExecuteResult> failback) {
        notNullOf(executeRequest, "evaluation");
        hasTextOf(executeRequest.getClientId(), "clientId");
        hasTextOf(executeRequest.getClientSecret(), "clientSecret");
        notEmptyOf(executeRequest.getScenesCodes(), "scenesCodes");
        notNullOf(executeRequest.getTimeout(), "timeout");
        isTrueOf(executeRequest.getTimeout() > 0, "timeout>0");
        notNullOf(executeRequest.getBestEffort(), "bestEffort");
        if (isBlank(executeRequest.getRequestId())) {
            executeRequest.setRequestId(UUID.randomUUID().toString());
        }

        final String requestBody = toJSONString(executeRequest);
        final Request request = new Request.Builder().url(UriComponentsBuilder.fromUri(config.getEndpoint())
                .path(RengineConstants.API_EXECUTOR_EXECUTE)
                .build()
                .toString()).post(FormBody.create(requestBody, MediaType.get("application/json"))).build();
        try (final Response response = httpClient.newBuilder()
                .callTimeout(Duration.ofMillis(executeRequest.getTimeout()))
                .build()
                .newCall(request)
                .execute();) {
            if (response.isSuccessful()) {
                final RespBase<ExecuteResult> result = parseJSON(new String(response.body().bytes(), UTF_8), RESULT_TYPEREF);
                if (RespBase.isSuccess(result)) {
                    return result.getData();
                }
            }
            // Fast fail-back.
            if (executeRequest.getBestEffort()) {
                return defaultFailback.apply(null);
            }
        } catch (Throwable e) {
            String errmsg = format("Could not to evaluation of '%s'", requestBody);
            if (log.isDebugEnabled()) {
                log.debug(errmsg, e);
            } else {
                log.warn(format("%s. - %s", errmsg, e.getMessage()));
            }
            if (executeRequest.getBestEffort()) {
                return defaultFailback.apply(e);
            }
            throw new ClientExecuteException(executeRequest.getRequestId(), executeRequest.getScenesCodes(), executeRequest.getTimeout(),
                    executeRequest.getBestEffort(), e);
        }

        return null;
    }

    public static class DefaultFailback implements Function<Throwable, ExecuteResult> {
        @Override
        public ExecuteResult apply(Throwable t) {
            // System.err.println(format("Failed to evaluation of reason: %s",
            // t.getMessage()));
            return ExecuteResult.builder().errorCount(Integer.MAX_VALUE).build();
        }
    }

    private static final TypeReference<RespBase<ExecuteResult>> RESULT_TYPEREF = new TypeReference<RespBase<ExecuteResult>>() {
    };

}
