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
package com.wl4g.rengine.evaluator.rest.exception;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;

import com.wl4g.infra.common.lang.StringUtils2;
import com.wl4g.infra.common.runtime.JvmRuntimeTool;
import com.wl4g.infra.common.web.rest.RespBase;

import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link GlobalExceptionMapper}
 * 
 * @author James Wong
 * @version 2022-09-19
 * @since v3.0.0
 * @see https://developers.redhat.com/articles/2022/03/03/rest-api-error-modeling-quarkus-20#model_the_error_response
 * @see https://quarkus.io/guides/resteasy-reactive#accessing-request-parameters
 */
@Slf4j
@javax.ws.rs.ext.Provider
@ApplicationScoped
// @Singleton
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    UriInfo uriInfo;

    @Context
    HttpServerRequest request;

    // @Inject
    // Provider<ContainerRequestContext> requestContextProvider;

    @Override
    public Response toResponse(Throwable th) {
        return wrapExceptionToResponse(th);
    }

    private Response wrapExceptionToResponse(Throwable th) {
        loggingException(th);

        // Use response from WebApplicationException as they are
        if (th instanceof WebApplicationException) {
            // Overwrite error message
            Response origErrorResponse = ((WebApplicationException) th).getResponse();
            return Response.status(200)/* .fromResponse(origErrorResponse) */
                    .entity(RespBase.create().withCode(500).withMessage(origErrorResponse.getStatusInfo().getReasonPhrase()))
                    .build();
        }
        // Special mappings
        else if (th instanceof IllegalArgumentException) {
            return Response.status(200)
                    ./* status(400). */entity(RespBase.create().withCode(400).withMessage(th.getMessage()))
                    .build();
        }
        // Use 500 (Internal Server Error) for all other
        else {
            return Response.status(200)
                    ./* serverError(). */entity(RespBase.create().withCode(500).withMessage("Internal Server Error"))
                    .build();
        }
    }

    private void loggingException(Throwable th) {
        String _stacktrace = request.getHeader("_stacktrace");
        if (isBlank(_stacktrace)) {
            final var c = request.getCookie("_stacktrace");
            if (nonNull(c)) {
                _stacktrace = c.getValue();
            }
        }
        if (StringUtils2.isTrue(_stacktrace, false) || JvmRuntimeTool.isJvmInDebugging) {
            log.error(format("Processing failed exception for request: %s", uriInfo.getRequestUri()), th);
        }
    }

}
