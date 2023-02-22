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
package com.wl4g.rengine.apiserver.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.Rule.RuleEngine;
import com.wl4g.rengine.common.entity.RuleScript;
import com.wl4g.rengine.common.model.RuleScriptExecuteRequest;
import com.wl4g.rengine.common.model.RuleScriptExecuteResult;
import com.wl4g.rengine.service.RuleScriptService;
import com.wl4g.rengine.service.model.RuleScriptDelete;
import com.wl4g.rengine.service.model.RuleScriptDeleteResult;
import com.wl4g.rengine.service.model.RuleScriptQuery;
import com.wl4g.rengine.service.model.RuleScriptSave;
import com.wl4g.rengine.service.model.RuleScriptSaveResult;
import com.wl4g.rengine.service.util.RuleScriptParser.ScriptASTInfo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RuleScriptScriptController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "RuleScriptAPI", description = "The rule script models management API")
@Slf4j
@RestController
@RequestMapping("/v1/rulescript")
public class RuleScriptController {

    private @Autowired RuleScriptService ruleScriptService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query rule scripts.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json") }) })
    @RequestMapping(path = { "query" }, method = { GET })
    public RespBase<PageHolder<RuleScript>> query(@Validated RuleScriptQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<RuleScript>> resp = RespBase.create();
        resp.setData(ruleScriptService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query rule scripts.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json") }) })
    @RequestMapping(path = { "parse" }, method = { GET })
    public RespBase<ScriptASTInfo> parse(@NotNull RuleEngine engine, @NotNull Long scriptId) {
        log.debug("called: engine={}, scriptId={}", engine, scriptId);
        RespBase<ScriptASTInfo> resp = RespBase.create();
        resp.setData(ruleScriptService.parse(engine, scriptId));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save rule script.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<RuleScriptSaveResult> save(@Validated @RequestBody RuleScriptSave model) {
        log.debug("called: model={}", model);
        RespBase<RuleScriptSaveResult> resp = RespBase.create();
        resp.setData(ruleScriptService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete rule script.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<RuleScriptDeleteResult> delete(@Validated @RequestBody RuleScriptDelete model) {
        log.debug("called: model={}", model);
        RespBase<RuleScriptDeleteResult> resp = RespBase.create();
        resp.setData(ruleScriptService.delete(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Execute rule script for testing.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "execute" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<RuleScriptExecuteResult> execute(@RequestBody RuleScriptExecuteRequest model) {
        log.debug("called: model={}", model);
        return ruleScriptService.execute(model);
    }

}
