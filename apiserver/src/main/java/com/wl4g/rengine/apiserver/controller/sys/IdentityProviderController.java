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
package com.wl4g.rengine.apiserver.controller.sys;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wl4g.infra.common.bean.page.PageHolder;
import com.wl4g.infra.common.web.rest.RespBase;
import com.wl4g.rengine.common.entity.sys.IdentityProvider;
import com.wl4g.rengine.service.IdentityProviderService;
import com.wl4g.rengine.service.model.sys.IdentityProviderQuery;
import com.wl4g.rengine.service.model.sys.IdentityProviderSave;
import com.wl4g.rengine.service.model.sys.IdentityProviderSaveResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link IdentityProviderController}
 * 
 * @author James Wong
 * @date 2022-08-28
 * @since v1.0.0
 */
@Tag(name = "IdentityProviderAPI", description = "The Identity Provider API")
@Slf4j
@Controller
@RequestMapping("/v1/idp")
public class IdentityProviderController {

    private @Autowired IdentityProviderService identityProviderService;

    @Operation(description = "Query identity provider settings.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "query" }, produces = "application/json", method = { GET })
    @ResponseBody
    @PreAuthorize("hasPermission(#model,'arn:sys:idp:read:v1')")
    public RespBase<PageHolder<IdentityProvider>> query(@Validated IdentityProviderQuery model) {
        log.debug("called: model={}", model);
        RespBase<PageHolder<IdentityProvider>> resp = RespBase.create();
        resp.setData(identityProviderService.query(model));
        return resp;
    }

    @Operation(description = "Save identity provider settings.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    @RequestMapping(path = { "save" }, consumes = "application/json", produces = "application/json", method = { POST })
    @ResponseBody
    @PreAuthorize("hasPermission(#model,'arn:sys:idp:write:v1')")
    public RespBase<IdentityProviderSaveResult> save(@Validated @RequestBody IdentityProviderSave model) {
        log.debug("called: model={}", model);
        RespBase<IdentityProviderSaveResult> resp = RespBase.create();
        resp.setData(identityProviderService.save(model));
        return resp;
    }

    //
    // Using Spring Security built-in oauth2 endpoint.
    // see:org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter
    // e.g: localhost:28001/oauth2/authorization/iam ->
    // https://iam.xx.com/realms/master/protocol/openid-connect/user?response_type=code&client_id=rengine&scope=email%20profile%20roles&state=PDZJDK3n6oGtUhq_rISUCrtiPJx4U90LXc96e5x1sIs%3D&redirect_uri=http://localhost:28001/login/oauth2/code/iam
    // see:org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter

    // @formatter:off
    // @Operation(description = "The authenticating")
    // @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful") })
    // @RequestMapping(path = { "user/{provider}" }, method = { GET })
    // public String user(HttpServletRequest request, @NotBlank @PathVariable String provider) {
    //     switch (IdPKind.valueOf(provider.toUpperCase())) {
    //     case OIDC:
    //         return buildRedirectForOidc(request);
    //     default:
    //         throw new UnsupportedOperationException(format("No supported idp provider for %s", provider));
    //     }
    // }
    // 
    // private String buildRedirectForOidc(HttpServletRequest request) {
    //     final String baseUri = WebUtils2.getRFCBaseURI(request, true);
    // 
    //     final String state = UUID.randomUUID().toString().replaceAll("-", "");
    //     // TODO-use-config
    //     redisTemplate.opsForValue().set(state, currentTimeMillis() + "", 600L, TimeUnit.SECONDS);
    // 
    //     return "redirect:"
    //             .concat(UriComponentsBuilder.fromUriString("https://iam.wl4g.com/realms/master/protocol/openid-connect/user")
    //                     .queryParam("client_id", "rengine-wl4g")
    //                     .queryParam("redirect_uri",
    //                             urlEncode(baseUri.concat("/oauth2/authorization/").concat(DEFAULT_OAUTH2_REGISTRATION_ID)))
    //                     .queryParam("response_type", "code")
    //                     .queryParam("scope", "openid+profile+email")
    //                     .queryParam("state", state)
    //                     .build()
    //                     .toString());
    // }
    // private @Autowired RedisTemplate<String, String> redisTemplate;
    // 
    // public static final String DEFAULT_OAUTH2_REGISTRATION_ID = "iam";
    // @formatter:on

}
