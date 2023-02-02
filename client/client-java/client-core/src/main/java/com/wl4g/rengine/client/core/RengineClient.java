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
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrueOf;
import static com.wl4g.infra.common.lang.Assert2.notEmptyOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_BASE;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_INTERNAL;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Duration;
import java.util.List;
import java.util.Map;
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
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.common.model.ExecuteResult.ResultDescription;
import com.wl4g.rengine.common.util.IdGenUtils;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
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
    private @Default Function<FailbackInfo, ExecuteResult> defaultFailback = DEFAULT_FAILBACK;

    public ExecuteResult execute(@NotEmpty List<String> scenesCodes, @Nullable Map<String, Object> args) {
        return execute(IdGenUtils.next(), scenesCodes, ExecuteRequest.DEFAULT_TIMEOUT, ExecuteRequest.DEFAULT_BESTEFFORT, args,
                null);
    }

    public ExecuteResult execute(@NotEmpty List<String> scenesCodes, @Min(1) Long timeoutMs, @Nullable Map<String, Object> args) {
        return execute(IdGenUtils.next(), scenesCodes, timeoutMs, ExecuteRequest.DEFAULT_BESTEFFORT, args, null);
    }

    public ExecuteResult execute(
            @NotEmpty List<String> scenesCodes,
            @NotNull Boolean bestEffort,
            @Nullable Map<String, Object> args) {
        return execute(IdGenUtils.next(), scenesCodes, ExecuteRequest.DEFAULT_TIMEOUT, bestEffort, args, null);
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
            Function<FailbackInfo, ExecuteResult> failback) {
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

    public ExecuteResult execute(@NotNull ExecuteRequest executeRequest) {
        return execute(executeRequest, null);
    }

    public ExecuteResult execute(
            @NotNull final ExecuteRequest executeRequest,
            @Nullable Function<FailbackInfo, ExecuteResult> failback) {
        notNullOf(executeRequest, "executeRequest");
        hasTextOf(executeRequest.getClientId(), "clientId");
        hasTextOf(executeRequest.getClientSecret(), "clientSecret");
        notEmptyOf(executeRequest.getScenesCodes(), "scenesCodes");
        notNullOf(executeRequest.getTimeout(), "timeout");
        isTrueOf(executeRequest.getTimeout() > 0, "timeout>0");
        notNullOf(executeRequest.getBestEffort(), "bestEffort");
        if (isBlank(executeRequest.getRequestId())) {
            executeRequest.setRequestId(IdGenUtils.next());
        }
        failback = isNull(failback) ? defaultFailback : failback;

        final String requestBody = toJSONString(executeRequest);
        final Request request = new Request.Builder().url(UriComponentsBuilder.fromUri(config.getEndpoint())
                .path(API_EXECUTOR_EXECUTE_BASE)
                .path(API_EXECUTOR_EXECUTE_INTERNAL)
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
            // Fast failback.
            if (executeRequest.getBestEffort()) {
                return failback.apply(new FailbackInfo(executeRequest, null));
            }
            throw new ClientExecuteException(executeRequest.getRequestId(), executeRequest.getScenesCodes(),
                    executeRequest.getTimeout(), executeRequest.getBestEffort(),
                    format("Engine execution failed, but you can set 'bestEffort=true' to force return a fallback result."));

        } catch (Throwable ex) {
            final String errmsg = format("Could not to execution for '%s'", requestBody);
            if (log.isDebugEnabled()) {
                log.debug(errmsg, ex);
            } else {
                log.warn(format("%s. - %s", errmsg, ex.getMessage()));
            }
            if (executeRequest.getBestEffort()) {
                return failback.apply(new FailbackInfo(executeRequest, ex));
            }
            throw new ClientExecuteException(executeRequest.getRequestId(), executeRequest.getScenesCodes(),
                    executeRequest.getTimeout(), executeRequest.getBestEffort(), ex);
        }
    }

    public static class DefaultFailback implements Function<FailbackInfo, ExecuteResult> {
        @Override
        public ExecuteResult apply(FailbackInfo f) {
            log.debug("Failed to execution of reason: {}", f.getError().getMessage());
            return ExecuteResult.builder()
                    .requestId(f.getRequest().getRequestId())
                    .description("Failure to execution")
                    .results(safeList(f.getRequest().getScenesCodes()).stream()
                            .map(scenesCode -> ResultDescription.builder()
                                    .scenesCode(scenesCode)
                                    .success(false)
                                    .reason(f.getError().getMessage())
                                    .build())
                            .collect(toList()))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class FailbackInfo {
        private ExecuteRequest request;
        private Throwable error;
    }

    private static final TypeReference<RespBase<ExecuteResult>> RESULT_TYPEREF = new TypeReference<RespBase<ExecuteResult>>() {
    };
    public static final Function<FailbackInfo, ExecuteResult> DEFAULT_FAILBACK = new DefaultFailback();
}
