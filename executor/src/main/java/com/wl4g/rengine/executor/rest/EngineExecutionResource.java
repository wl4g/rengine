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
package com.wl4g.rengine.executor.rest;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseMapObject;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.wl4g.infra.common.codec.Encodes;
import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.infra.common.serialize.JaxbUtils;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.model.ExecuteRequest;
import com.wl4g.rengine.common.model.ExecuteResult;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.executor.rest.intercept.CustomValid;
import com.wl4g.rengine.executor.service.EngineExecutionService;

import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.smallrye.mutiny.Uni;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EngineExecutionResource}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://github.com/quarkusio/quarkus-quickstarts/blob/2.12.Final/jta-quickstart/src/main/java/org/acme/quickstart/TransactionalResource.java
 */
@Slf4j
@CustomValid
@Path(RengineConstants.API_EXECUTOR_EXECUTE_BASE)
public class EngineExecutionResource {

    /**
     * Tips: Quarkus 使用 GraalVM 构建 native 可执行文件。GraalVM
     * 的限制之一是反射的使用。支持反射操作，但必须明确注册所有相关成员以进行反射。这些注册会产生更大的本机可执行文件。 如果 Quarkus DI
     * 需要访问私有成员，它必须使用反射。这就是为什么鼓励 Quarkus 用户不要在他们的
     * bean中使用私有成员的原因。这涉及注入字段、构造函数和初始化程序、观察者方法、生产者方法和字段、处置器和拦截器方法。
     * 
     * @see https://quarkus.io/guides/cdi-reference#native-executables-and-private-members
     */
    @Inject
    EngineExecutionService engineExecutionService;

    @Context
    @NotNull
    CurrentVertxRequest currentVertxRequest;

    /**
     * Receive execution request from client SDK. For example, a request from a
     * business application JVM process via a dependent client SDK.
     * 
     * @param executeRequest
     * @return
     * @throws Exception
     * @see https://quarkus.io/guides/resteasy-reactive#asyncreactive-support
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(RengineConstants.API_EXECUTOR_EXECUTE)
    public Uni<RespBase<ExecuteResult>> execute(final @NotNull ExecuteRequest executeRequest) throws Exception {
        log.debug("Executing for : {}", executeRequest);
        return engineExecutionService.execute(executeRequest);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_XML,
            MediaType.TEXT_HTML })
    @Path(RengineConstants.API_EXECUTOR_EXECUTE_WRAPPER)
    public Uni<RespBase<ExecuteResult>> getExecuteWrapper() throws Exception {
        return doExecuteWrapper(EMPTY);
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_XML,
            MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_XML,
            MediaType.TEXT_HTML })
    @Path(RengineConstants.API_EXECUTOR_EXECUTE_WRAPPER)
    public Uni<RespBase<ExecuteResult>> postExecuteWrapper(final @NotNull String bodyString) throws Exception {
        return doExecuteWrapper(bodyString);
    }

    /**
     * Receive execution requests from arbitrary external systems. For example:
     * the request sent when the pushes(or webhook) subscription events from
     * WeChat, Dingtalk and other social platforms servers. </br>
     * </br>
     * This API is very useful, such as realizing chat interaction with WeChat
     * official account or Dingding robot.
     * 
     * @param bodyString
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    Uni<RespBase<ExecuteResult>> doExecuteWrapper(final String bodyString) throws Exception {
        log.debug("Executing wrapper for body: {}", bodyString);

        final RoutingContext context = currentVertxRequest.getCurrent();
        // context.data(); // attributes?
        // context.body().asString("UTF-8"); // invalid?
        final HttpServerRequest request = context.request();
        final MultiMap headers = request.headers();
        final MultiMap queryParams = context.queryParams(UTF_8);
        final String format = queryParams.get(PARAM_FORMAT); // Optional

        // The basic parameters.
        String requestId = headers.get(PARAM_REQUEST_ID); // Optional
        String clientId = headers.get(PARAM_CLIENT_ID); // Required
        String clientSecret = headers.get(PARAM_CLIENT_SECRET); // Required
        List<String> scenesCodes = headers.getAll(PARAM_SCENES_CODES); // Required

        // bestEffect
        String bestEffectString = headers.get(PARAM_BEST_EFFECT); // Optional
        Boolean bestEffect = isBlank(bestEffectString) ? ExecuteRequest.DEFAULT_BESTEFFORT : parseBoolean(bestEffectString);

        // timeout
        String timeoutString = headers.get(PARAM_TIMEOUT); // Optional
        Long timeout = isBlank(timeoutString) ? ExecuteRequest.DEFAULT_TIMEOUT : parseLong(timeoutString);

        // args
        Map<String, Object> args = null; // Optional
        if (!isBlank(bodyString) && request.method() == HttpMethod.POST) {
            // final String bodyString =
            // readFullyToString(request.getInputStream(), "UTF-8");
            final String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
            if (containsIgnoreCase(contentType, "application/json")) {
                args = parseMapObject(bodyString);
            } else if (containsIgnoreCase(contentType, "application/xml")) {
                args = JaxbUtils.fromXml(bodyString, Map.class);
            } else {
                if (equalsIgnoreCase(format, "json")) {
                    args = parseMapObject(bodyString);
                } else if (equalsIgnoreCase(format, "xml")) {
                    args = JaxbUtils.fromXml(bodyString, Map.class);
                } else {
                    throw new UnsupportedOperationException(format("Unsupported content type for %s", contentType));
                }
            }
            // Downgrade to get the required parameters from the body.
            if (isBlank(requestId)) {
                requestId = args.getOrDefault(PARAM_REQUEST_ID, EMPTY).toString();
            }
            if (isBlank(clientId)) {
                clientId = args.getOrDefault(PARAM_CLIENT_ID, EMPTY).toString();
            }
            if (isBlank(clientSecret)) {
                clientSecret = args.getOrDefault(PARAM_CLIENT_SECRET, EMPTY).toString();
            }
            if (CollectionUtils2.isEmpty(scenesCodes)) {
                final Object scenesCodesObj = args.getOrDefault(PARAM_SCENES_CODES, emptyList());
                if (scenesCodesObj instanceof List) {
                    scenesCodes = ((List) scenesCodesObj);
                }
            }
        }

        // Allow request query parameters to override body parameters.
        if (nonNull(queryParams)) {
            // override for requestId if necessary.
            final String queryRequestId = queryParams.get(PARAM_REQUEST_ID);
            requestId = isBlank(queryRequestId) ? requestId : queryRequestId;
            // override for clientId if necessary.
            final String queryClientId = queryParams.get(PARAM_CLIENT_ID);
            clientId = isBlank(queryClientId) ? clientId : queryClientId;
            // override for clientSecret if necessary.
            final String queryClientSecret = queryParams.get(PARAM_CLIENT_SECRET);
            clientSecret = isBlank(queryClientSecret) ? clientSecret : queryClientSecret;
            // override for scenesCodes if necessary.
            final var queryScenesCodes = queryParams.get(PARAM_SCENES_CODES);
            scenesCodes = isBlank(queryScenesCodes) ? scenesCodes : asList(split(queryScenesCodes, ","));
            // override for args if necessary.
            var queryArgs = queryParams.get(PARAM_ARGS);
            if (!isBlank(queryArgs)) {
                if (request.method() == HttpMethod.GET) {
                    queryArgs = Encodes.decodeBase64String(queryArgs);
                }
                args = parseMapObject(queryArgs);
            }
        }

        return engineExecutionService.execute(ExecuteRequest.builder()
                .requestId(isBlank(requestId) ? IdGenUtils.next() : requestId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scenesCodes(scenesCodes)
                .bestEffort(bestEffect)
                .timeout(timeout)
                .args(args)
                .build()
                .validate());
    }

    // The special parameter of the wrapper execution API, used to control the
    // body data format of the specified request.
    public static final String PARAM_FORMAT = "format";

    // The standard execution API parameters.
    public static final String PARAM_REQUEST_ID = "requestId";
    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_CLIENT_SECRET = "clientSecret";
    public static final String PARAM_SCENES_CODES = "scenesCodes";
    public static final String PARAM_BEST_EFFECT = "bestEffect";
    public static final String PARAM_TIMEOUT = "timeout";
    public static final String PARAM_ARGS = "args";

}
