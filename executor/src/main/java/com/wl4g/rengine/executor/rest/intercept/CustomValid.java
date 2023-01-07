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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.interceptor.InterceptorBinding;

//@Inherited
// CDI: We want to bind this annotation to an implementation class.
//    otherwise error while building: 
//        "Interceptor has no bindings: de.materne.quarkus.HelloWorldInterceptor"
@InterceptorBinding
// Annotation: We need this annotion until runtime. No deletion while
// compilation.
// SOURCE : Interceptor has no bindings
// CLASS : Interceptor has no bindings
// RUNTIME: ok
//
@Retention(RUNTIME)
// Annotation: We have to annotate the implementation class itself (TYPE) and
// all
// method calls on the CDI bean.
// If you want to be more restrictive you could place the annotation on the
// methods, but
// then you also have to allow METHOD.
@Target(TYPE)
// @Target({METHOD, TYPE})
public @interface CustomValid {
}