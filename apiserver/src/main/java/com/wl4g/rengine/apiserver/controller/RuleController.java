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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.Rule;
import com.wl4g.rengine.service.RuleService;
import com.wl4g.rengine.service.model.RuleDelete;
import com.wl4g.rengine.service.model.RuleDeleteResult;
import com.wl4g.rengine.service.model.RuleQuery;
import com.wl4g.rengine.service.model.RuleSave;
import com.wl4g.rengine.service.model.RuleSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link RuleController}
 * 
 * @author James Wong
 * @version 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "RuleAPI", description = "The rule models management API")
@Slf4j
@RestController
@RequestMapping("/v1/rule")
public class RuleController {

    private @Autowired RuleService ruleService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query rules.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json") }) })
    @RequestMapping(path = { "query" }, method = { GET })
    @PreAuthorize("hasPermission(#model,'arn:rengine:rule:read:v1')")
    public RespBase<PageHolder<Rule>> query(@Validated RuleQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<Rule>> resp = RespBase.create();
        resp.setData(ruleService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save rule.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @PreAuthorize("hasPermission(#model,'arn:rengine:rule:write:v1')")
    public RespBase<RuleSaveResult> save(@Validated @RequestBody RuleSave model) {
        log.debug("called: model={}", model);
        RespBase<RuleSaveResult> resp = RespBase.create();
        resp.setData(ruleService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete rule.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    @PreAuthorize("hasPermission(#model,'arn:rengine:rule:delete:v1')")
    public RespBase<RuleDeleteResult> delete(@Validated @RequestBody RuleDelete model) {
        log.debug("called: model={}", model);
        RespBase<RuleDeleteResult> resp = RespBase.create();
        resp.setData(ruleService.delete(model));
        return resp;
    }

}
