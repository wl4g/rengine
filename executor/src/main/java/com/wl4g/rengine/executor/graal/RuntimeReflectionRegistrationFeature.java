package com.wl4g.rengine.executor.graal;
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
//package com.wl4g.rengine.executor.graal;
//
//import org.apache.commons.logging.LogFactory;
//import org.apache.commons.logging.impl.JBossLogFactory;
//import org.apache.commons.logging.impl.LogFactoryImpl;
//import org.graalvm.nativeimage.hosted.Feature;
//import org.graalvm.nativeimage.hosted.RuntimeReflection;
//
///**
// * {@link RuntimeReflectionRegistrationFeature}
// * 
// * Usages: -Dquarkus.native.additional-build-args="--features=com.wl4g.rengine.executor.graal.RuntimeReflectionRegistrationFeature"
// * 
// * @author James Wong
// * @version 2022-09-25
// * @since v1.0.0
// * @see io.quarkus.runtime.graal.ResourcesFeature
// */
//public class RuntimeReflectionRegistrationFeature implements Feature {
//
//    @Override
//    public void beforeAnalysis(BeforeAnalysisAccess access) {
//        try {
//            RuntimeReflection.register(String.class);
//            RuntimeReflection.register(String.class.getDeclaredField("value"));
//            RuntimeReflection.register(String.class.getDeclaredField("hash"));
//            RuntimeReflection.register(String.class.getDeclaredConstructor(char[].class));
//            RuntimeReflection.register(String.class.getDeclaredMethod("charAt", int.class));
//            RuntimeReflection.register(String.class.getDeclaredMethod("format", String.class, Object[].class));
//            RuntimeReflection.register(LogFactory.class);
//            RuntimeReflection.register(LogFactoryImpl.class);
//            RuntimeReflection.register(JBossLogFactory.class);
//        } catch (NoSuchMethodException | NoSuchFieldException e) {
//            throw new IllegalStateException(e);
//        }
//    }
//
//}
