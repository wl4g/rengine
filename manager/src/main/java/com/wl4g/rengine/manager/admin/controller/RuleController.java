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

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.manager.admin.model.AddRule;
import com.wl4g.rengine.manager.admin.model.AddRuleResult;
import com.wl4g.rengine.manager.admin.model.DeleteRule;
import com.wl4g.rengine.manager.admin.model.DeleteRuleResult;
import com.wl4g.rengine.manager.admin.model.QueryRule;
import com.wl4g.rengine.manager.admin.model.QueryRuleResult;
import com.wl4g.rengine.manager.admin.service.RuleService;

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
 * @since v3.0.0
 */
@Tag(name = "RuleAPI", description = "The rule models management API")
@Slf4j
@RestController
@RequestMapping("/admin/rule")
public class RuleController {

    private @Autowired RuleService ruleService;

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Query rules model list.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful",
            content = { @Content(mediaType = "application/json") }) })
    @RequestMapping(path = { "query" }, method = { GET })
    public RespBase<QueryRuleResult> query(@Validated QueryRule model) {
        log.info("called: model={}", model);
        RespBase<QueryRuleResult> resp = RespBase.create();
        resp.setData(ruleService.query(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Save rules model.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    public RespBase<AddRuleResult> save(@Validated @RequestBody AddRule model) {
        log.info("called: model={}", model);
        RespBase<AddRuleResult> resp = RespBase.create();
        resp.setData(ruleService.save(model));
        return resp;
    }

    // @SecurityRequirement(name = "default_oauth")
    @Operation(description = "Delete project.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "delete" }, produces = "application/json", method = { DELETE, POST })
    public RespBase<DeleteRuleResult> delete(@Validated DeleteRule model) {
        log.info("called: model={}", model);
        RespBase<DeleteRuleResult> resp = RespBase.create();
        resp.setData(ruleService.delete(model));
        return resp;
    }

}