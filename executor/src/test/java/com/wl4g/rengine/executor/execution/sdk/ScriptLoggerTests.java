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
//package com.wl4g.rengine.executor.execution.sdk;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.wl4g.rengine.executor.minio.MinioManager;
//import com.wl4g.rengine.executor.minio.MinioManagerIT;
//
///**
// * {@link ScriptLoggerTests}
// * 
// * @author James Wong
// * @date 2022-10-14
// * @since v1.0.0
// */
//public class ScriptLoggerTests {
//
//    MinioManager minioManager;
//
//    @Before
//    public void setup() {
//        minioManager = MinioManagerIT.createDefaultMinioManager();
//    }
//
//    @Test
//    public void testInfoWrite() {
//        final ScriptLogger logger = new ScriptLogger("ecommerce_trade_gift", 10100101L, minioManager);
//        logger.info("The test info logs... user: ", "jack01");
//        logger.error("The test error logs... user: ", "jack01");
//    }
//
//}
