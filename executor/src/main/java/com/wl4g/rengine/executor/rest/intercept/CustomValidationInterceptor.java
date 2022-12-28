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
package com.wl4g.rengine.executor.rest.intercept;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeArrayToList;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.validation.Validator;

// CDI: bind this class to this annotation
//    otherwise error while building: 
//       "Interceptor has no bindings: de.materne.quarkus.HelloWorldInterceptor"
@CustomValid
// CDI: declare this as an CDI interceptor
// otherwise this is not an interceptor and therefore the test will fail
@Interceptor
public class CustomValidationInterceptor {

    @Inject
    Validator validator;

    // CDI: Mark this method as interceptor code
    // otherwise this code is not executed and the test will fail
    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        safeArrayToList(context.getParameters()).forEach(p -> validator.validate(p));
        return context.proceed();
    }

}