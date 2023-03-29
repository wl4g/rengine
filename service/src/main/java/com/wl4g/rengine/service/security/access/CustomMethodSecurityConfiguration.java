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
package com.wl4g.rengine.service.security.access;

//import java.util.Arrays;
//import java.util.List;
//
//import org.aopalliance.intercept.MethodInvocation;
//import org.springframework.security.access.AccessDecisionManager;
//import org.springframework.security.access.AccessDecisionVoter;
//import org.springframework.security.access.prepost.PreInvocationAttribute;
//import org.springframework.security.access.prepost.PreInvocationAuthorizationAdvice;
//import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
//import org.springframework.security.access.vote.ConsensusBased;
//import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * {@link CustomMethodSecurityConfiguration}
 * 
 * @author James Wong
 * @version 2023-03-11
 * @since v1.0.0
 */
@Configuration
@ConditionalOnClass({ WebSecurityCustomizer.class, GlobalMethodSecurityConfiguration.class })
// see:org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration#methodSecurityInterceptor
// see:org.springframework.security.access.expression.method.MethodSecurityExpressionHandler
// see:org.springframework.security.access.intercept.AbstractSecurityInterceptor#attemptAuthorization
// see:org.springframework.security.access.vote.AffirmativeBased#decide
// see;org.springframework.security.access.expression.SecurityExpressionOperations
// see:org.springframework.security.access.expression.SecurityExpressionRoot#hasAuthority
// see:org.springframework.security.access.expression.SecurityExpressionRoot#hasPermission
@EnableGlobalMethodSecurity(prePostEnabled = true,
        securedEnabled = true/* , mode = AdviceMode.ASPECTJ */)
public class CustomMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

    private @Autowired SimplePermissionEvaluator simplePermissionEvaluator;

    @Bean
    public SimplePermissionEvaluator simplePermissionEvaluator() {
        return new SimplePermissionEvaluator();
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(simplePermissionEvaluator);
        return expressionHandler;
    }

    // Custom access decistion configuration.
    //// @formatter:off
    //@Bean
    //public AccessDecisionManager accessDecisionManager() {
    //    List<AccessDecisionVoter<? extends Object>> decisionVoters = Arrays
    //            .asList(new PreInvocationAuthorizationAdviceVoter(new PreInvocationAuthorizationAdvice() {
    //                @Override
    //                public boolean before(
    //                        Authentication authentication,
    //                        MethodInvocation mi,
    //                        PreInvocationAttribute preInvocationAttribute) {
    //                    // TODO
    //                    return false;
    //                }
    //            }));
    //
    //    // AffirmativeBased: as-long-as-one-agrees.
    //    // UnanimousBased: Requires-at-all-to-agree.
    //    // ConsensusBased: Requires-at-least-half-to-agree.
    //    return new ConsensusBased(decisionVoters);
    //}
    //// @formatter:on

}