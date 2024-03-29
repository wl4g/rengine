package com.wl4g.rengine.executor.meter.intercept;
// @formatter:off
///*
// * Copyright 2017 ~ 2025 the original author or authors. James Wong <jameswong1376@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ALL_OR KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.rengine.executor.meter.intercept;
//
//import javax.interceptor.AroundInvoke;
//import javax.interceptor.Interceptor;
//import javax.interceptor.InvocationContext;
//
///**
// * Notice: Since there is no way to dynamically inject beans into CDI, manual collection of metrics is currently used.
// * 
// * @author James Wong
// * @date 2022-12-28
// * @since v1.0.0
// */
//@CollectMetrics
//@Interceptor
//public class CollectMetricsInterceptor {
//
//    @AroundInvoke
//    public Object intercept(InvocationContext context) throws Exception {
//        return context.proceed();
//    }
//
//}
// @formatter:on