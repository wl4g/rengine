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
package com.wl4g.rengine.manager.admin.controller;

import static java.lang.System.currentTimeMillis;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.rengine.manager.admin.model.SaveUpload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HelloController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v3.0.0
 */
@Tag(name = "HelloAPI", description = "The hello API")
@Slf4j
@RestController
@RequestMapping("/admin/hello")
public class HelloController {

    @Value("${app.info.version:latest}")
    private String appversion;

    @Operation(description = "Hello echo")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json", schema = @Schema(implementation = SaveUpload.class)) }) })
    @RequestMapping(path = "echo", method = { GET, POST, PUT, DELETE, PATCH })
    public EchoResultModel echo(HttpServletRequest request, @RequestBody(required = false) String body) throws Exception {
        log.info("[{}:called:echo()] appversion={}, uri={}, body={}", appversion, request.getRequestURI(), body);

        EchoResultModel msg = new EchoResultModel();
        msg.setTimestamp(currentTimeMillis());
        msg.setAppversion(appversion);
        msg.setMethod(request.getMethod());
        msg.setPath(request.getRequestURI());
        Enumeration<String> en = request.getHeaderNames();
        while (en.hasMoreElements()) {
            String name = en.nextElement();
            msg.getHeaders().put(name, request.getHeader(name));
        }
        msg.setBody(body);
        return msg;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class EchoResultModel {
        private long timestamp;
        private String appversion;
        private String method;
        private String path;
        private Map<String, String> headers = new HashMap<String, String>();
        private String body;
    }

}
