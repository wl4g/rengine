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
import static com.wl4g.infra.common.codec.Encodes.decodeBase64String;
import static com.wl4g.infra.common.collection.CollectionUtils2.ensureMap;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.isTrue;
import static com.wl4g.infra.common.lang.Assert2.notNull;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseMapObject;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_BASE;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_CUSTOM;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_INTERNAL_WORKFLOW;
import static com.wl4g.rengine.common.constants.RengineConstants.API_EXECUTOR_EXECUTE_INTERNAL_RULESCRIPT;
import static com.wl4g.rengine.common.model.ExecuteRequest.DEFAULT_BESTEFFORT;
import static com.wl4g.rengine.common.model.ExecuteRequest.DEFAULT_TIMEOUT;
import static com.wl4g.rengine.executor.rest.EngineExecutionEndpoint.RequestSettings.PARAM_REQ_SETTINGS;
import static com.wl4g.rengine.executor.rest.EngineExecutionEndpoint.ResponseSettings.PARAM_RESP_SETTINGS;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsAnyIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replaceIgnoreCase;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.infra.common.lang.Assert2;
import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.infra.common.serialize.JaxbUtils;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.Dict.DictType;
import com.wl4g.rengine.common.exception.RengineException;
import com.wl4g.rengine.common.model.WorkflowExecuteResult;
import com.wl4g.rengine.common.model.WorkflowExecuteResult.ResultDescription;
import com.wl4g.rengine.common.model.RuleScriptExecuteRequest;
import com.wl4g.rengine.common.model.WorkflowExecuteRequest;
import com.wl4g.rengine.common.util.IdGenUtils;
import com.wl4g.rengine.executor.rest.intercept.CustomValid;
import com.wl4g.rengine.executor.service.DictService;
import com.wl4g.rengine.executor.service.EngineExecutionService;

import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import io.smallrye.mutiny.Uni;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EngineExecutionEndpoint}
 * 
 * @author James Wong
 * @version 2022-09-18
 * @since v1.0.0
 * @see https://github.com/quarkusio/quarkus-quickstarts/blob/2.12.Final/jta-quickstart/src/main/java/org/acme/quickstart/TransactionalResource.java
 */
@Slf4j
@CustomValid
@Path(API_EXECUTOR_EXECUTE_BASE)
public class EngineExecutionEndpoint {

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

    @Inject
    DictService dictService;

    @Context
    @NotNull
    CurrentVertxRequest currentVertxRequest;

    /**
     * This API is Unit tests for users to run individual rules directly in the
     * management console.
     * 
     * @param workflowExecuteRequest
     * @return
     * @throws Exception
     * @see https://quarkus.io/guides/resteasy-reactive#asyncreactive-support
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(API_EXECUTOR_EXECUTE_INTERNAL_RULESCRIPT)
    public Uni<RespBase<ResultDescription>> executeInternal(final @NotNull RuleScriptExecuteRequest executeRequest)
            throws Exception {
        log.debug("Executing for : {}", executeRequest);
        return engineExecutionService.execute(executeRequest);
    }

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
    @Path(API_EXECUTOR_EXECUTE_INTERNAL_WORKFLOW)
    public Uni<RespBase<WorkflowExecuteResult>> executeInternal(final @NotNull WorkflowExecuteRequest executeRequest)
            throws Exception {
        log.debug("Executing for : {}", executeRequest);
        return engineExecutionService.execute(executeRequest);
    }

    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_XML,
            MediaType.TEXT_HTML })
    @Path(API_EXECUTOR_EXECUTE_CUSTOM)
    public Uni<String> getExecuteCustom() throws Exception {
        return doExecuteCustom(EMPTY);
    }

    @POST
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_XML,
            MediaType.TEXT_HTML })
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN, MediaType.TEXT_XML,
            MediaType.TEXT_HTML })
    @Path(API_EXECUTOR_EXECUTE_CUSTOM)
    public Uni<String> postExecuteCustom(final @NotNull String bodyString) throws Exception {
        return doExecuteCustom(bodyString);
    }

    /**
     * Receive execution requests custom from arbitrary external systems. For
     * example: the request sent when the pushes(or webhook) subscription events
     * from WeChat, Dingtalk and other social platforms servers. </br>
     * </br>
     * This API is very useful, such as realizing chat interaction with WeChat
     * official account or Dingding robot.
     * 
     * @param bodyString
     * @return
     * @throws Exception
     */
    private Uni<String> doExecuteCustom(final String bodyString) throws Exception {
        log.debug("Executing custom for body: {}", bodyString);

        final RoutingContext context = currentVertxRequest.getCurrent();
        // context.data(); // attributes?
        // context.body().asString("UTF-8"); // invalid?
        final HttpServerRequest request = context.request();
        final MultiMap queryParams = context.queryParams(UTF_8);

        // Gets request and response custom settings configuration.
        final RequestSettings reqSettings = RequestSettings.from(queryParams.get(PARAM_REQ_SETTINGS)); // Required
        final ResponseSettings respSettings = ResponseSettings.from(queryParams.get(PARAM_RESP_SETTINGS)); // Required

        final Map<String, Object> bodyArgs = getBodyArgs(request, reqSettings, bodyString); // Optional
        final String queryForBodyArgsString = getMergedParam(bodyArgs, PARAM_ARGS, EMPTY); // Optional
        final Map<String, Object> queryForBodyArgs = parseMapObject(decodeBase64String(queryForBodyArgsString));

        // The args in query parameters take precedence over body args.
        final Map<String, Object> mergedBodyArgs = ensureMap(bodyArgs);
        mergedBodyArgs.putAll(queryForBodyArgs);

        final String requestId = getMergedParam(mergedBodyArgs, PARAM_REQUEST_ID, IdGenUtils.next()); // Optional
        final String clientId = getMergedParam(mergedBodyArgs, PARAM_CLIENT_ID, null); // Required
        final String clientSecret = getMergedParam(mergedBodyArgs, PARAM_CLIENT_SECRET, null); // Required
        final List<String> scenesCodes = getMergedParam(mergedBodyArgs, PARAM_SCENES_CODES, emptyList()); // Required
        final Boolean bestEffort = parseBoolean(getMergedParam(bodyArgs, PARAM_BEST_EFFORT, DEFAULT_BESTEFFORT + "")); // Optional
        final Long timeout = parseLong(getMergedParam(mergedBodyArgs, PARAM_TIMEOUT, DEFAULT_TIMEOUT + "")); // Optional

        final WorkflowExecuteRequest executeRequest = WorkflowExecuteRequest.builder()
                .requestId(requestId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .scenesCodes(scenesCodes)
                .bestEffort(bestEffort)
                .timeout(timeout)
                .args(mergedBodyArgs)
                .build()
                .validate();
        return engineExecutionService.execute(executeRequest).flatMap(resp -> {
            // The customize the response body, which be returned
            // flexibly according to the custom setting parameters
            // obtained from the request.
            final Map<String, Object> mergedValueMap = new HashMap<>();
            safeList(resp.getData().getResults()).stream()
                    .filter(desc -> desc.getSuccess())
                    .forEach(desc -> mergedValueMap.putAll(desc.getValueMap()));

            // Load the response template from the dictionary service
            // according to the request template key, and then render the
            // response string.
            return dictService.findDicts(DictType.ENGINE_EXECUTION_CUSTOM_RESP_TPL, respSettings.getTemplateKey()).map(dicts -> {
                try {
                    Assert2.notEmpty(dicts, "Not found response template dictionary by %s", respSettings.getTemplateKey());
                    final String respTemplate = dicts.get(0).getValue();
                    final String resolvedBody = ResponseSettings.resolve(respTemplate, mergedValueMap);
                    log.debug("Resolved custom response body : {}", resolvedBody);
                    return resolvedBody;
                } catch (Exception e) {
                    log.error(format("Failed to resolve custom response body. mergedResult: %s", mergedValueMap), e);
                    throw e;
                }
            });
        });
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <T> T getMergedParam(
            final @NotNull Map<String, Object> bodyArgs,
            final @NotBlank String key,
            final @Nullable T defaultValue) {
        notNullOf(bodyArgs, "bodyArgs");
        hasTextOf(key, "key");

        final RoutingContext context = currentVertxRequest.getCurrent();
        // context.data(); // attributes?
        // context.body().asString("UTF-8"); // invalid?
        final HttpServerRequest request = context.request();
        final MultiMap queryParams = context.queryParams(UTF_8);
        final MultiMap headers = request.headers();

        // 1. Get parameter by query (first priority).
        Object value = queryParams.get(key);
        // 2. Get parameter by headers (second priority).
        if (StringUtils2.isEmpty(value)) {
            // Notice: It may be converted to lowercase by services such as
            // front-end load balancing and gateways.
            // value = headers.get(key);
            value = headers.entries()
                    .stream()
                    .filter(e -> equalsAnyIgnoreCase(e.getKey(), key, "x-".concat(key)))
                    .map(e -> e.getValue())
                    .findFirst()
                    .orElse(null);
        }
        // 3. Get parameter by body (third priority).
        if (StringUtils2.isEmpty(value)) {
            final Object valueObj = bodyArgs.get(key);
            if (nonNull(valueObj)) {
                if (valueObj instanceof String) {
                    value = valueObj.toString();
                } else if (valueObj instanceof Collection) {
                    value = CollectionUtils2.safeList((Collection) valueObj);
                }
            }
        }
        // 4. Use default if none.
        value = StringUtils2.isEmpty(value) ? defaultValue : value;

        // Transform string value to collection.
        if (defaultValue instanceof Collection && value instanceof String) {
            value = asList(split((String) value, ","));
        }
        return (T) value;
    }

    private Map<String, Object> getBodyArgs(
            final @NotNull HttpServerRequest request,
            final @Nullable RequestSettings reqSettings,
            final @Nullable String bodyString) {
        notNullOf(request, "request");

        Map<String, Object> args = emptyMap(); // Optional
        if (!isBlank(bodyString) && request.method() == HttpMethod.POST) {
            // final String
            // bodyString=readFullyToString(request.getInputStream(), "UTF-8");
            final String contentType = request.headers().get(HttpHeaders.CONTENT_TYPE);
            if (containsIgnoreCase(contentType, "application/json")) {
                args = parseMapObject(bodyString);
            } else if (containsIgnoreCase(contentType, "application/xml")) {
                args = JaxbUtils.fromXml(bodyString, Map.class);
            } else {
                if (nonNull(reqSettings) && equalsIgnoreCase(reqSettings.getFormat(), "json")) {
                    args = parseMapObject(bodyString);
                } else if (nonNull(reqSettings) && equalsIgnoreCase(reqSettings.getFormat(), "xml")) {
                    args = JaxbUtils.fromXml(bodyString, Map.class);
                } else {
                    throw new UnsupportedOperationException(format("Unsupported content type for %s", contentType));
                }
            }
        }

        return args;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class RequestSettings {
        public static final String PARAM_REQ_SETTINGS = "reqSettings";

        /**
         * The special parameter of the wrapper execution API, used to control
         * the body data format of the specified request.
         */
        public @Nullable String format = "json";

        public static RequestSettings from(@NotBlank String reqSettingsString) {
            final RequestSettings reqSettings = parseJSON(decodeBase64String(reqSettingsString), RequestSettings.class); // Required
            notNull(reqSettings,
                    "Tip: The request parameter not carry the parameters set by the request. Please add the query parameter '%s' to the request"
                            + " to configure the specification of a custom request.",
                    PARAM_REQ_SETTINGS);
            return reqSettings;
        }
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class ResponseSettings {
        public static final String PARAM_RESP_SETTINGS = "respSettings";

        public @NotBlank String templateKey;

        public static ResponseSettings from(@NotBlank String respSettingsString) {
            final ResponseSettings respSettings = parseJSON(decodeBase64String(respSettingsString), ResponseSettings.class); // Required
            notNull(respSettings,
                    "Tips: The request is processed successfully, but the request parameter does not carry the response setting parameters."
                            + "Please add the query parameter '%s' to the request to customize the response specification.",
                    PARAM_RESP_SETTINGS);
            hasTextOf(respSettings.getTemplateKey(), PARAM_RESP_SETTINGS + ".templateKey");
            return respSettings;
        }

        public static String resolve(@NotBlank String template, @NotNull Map<String, Object> mergedValueMap) {
            hasTextOf(template, "template");
            notNullOf(mergedValueMap, "mergedValueMap");

            // Resolve from template
            final StringBuffer tpl = new StringBuffer(template);
            mergedValueMap.forEach((k, v) -> {
                final String _tpl = tpl.toString();
                tpl.setLength(0);
                tpl.append(replaceIgnoreCase(_tpl, "${" + k + "}", valueOf(v)));
            });

            // Check resolved is full.
            final String body = tpl.toString();
            checkResolved(body);
            return body;
        }

        static void checkResolved(String body) throws RengineException {
            int firstStart = body.indexOf("$");
            if (firstStart > -1) {
                int firstEnd = body.indexOf("}");
                isTrue(firstEnd > -1, errmsg -> new RengineException(errmsg),
                        "Custom execute response body template syntax error, No ending '}' after start index: %s", firstStart);
                String firstVar = EMPTY;
                if ((firstEnd + 1) == body.length()) {
                    firstVar = body.substring(firstStart);
                } else {
                    firstVar = body.substring(firstStart, firstEnd + 1);
                }
                throw new RengineException(
                        format("Custom execute response body template parsing symbol error. unresolved variable: %s, => %s",
                                firstVar, body));
            }
        }
    }

    // The standard execution API parameters.
    public static final String PARAM_REQUEST_ID = "requestId";
    public static final String PARAM_CLIENT_ID = "clientId";
    public static final String PARAM_CLIENT_SECRET = "clientSecret";
    public static final String PARAM_SCENES_CODES = "scenesCodes";
    public static final String PARAM_BEST_EFFORT = "bestEffort";
    public static final String PARAM_TIMEOUT = "timeout";
    public static final String PARAM_ARGS = "args";
}
