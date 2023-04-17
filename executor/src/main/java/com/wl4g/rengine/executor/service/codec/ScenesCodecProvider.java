package com.wl4g.rengine.executor.service.codec;
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
//package com.wl4g.rengine.executor.service.codec;
//
//import org.bson.codecs.Codec;
//import org.bson.codecs.configuration.CodecProvider;
//import org.bson.codecs.configuration.CodecRegistry;
//
//import com.wl4g.rengine.common.entity.Scenes;
//
///**
// * {@link ScenesCodecProvider}
// * 
// * @author James Wong
// * @date 2022-09-28
// * @since v1.0.0
// * @see https://quarkus.io/guides/mongodb#testing-helpers
// */
//public class ScenesCodecProvider implements CodecProvider {
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
//        if (clazz.equals(Scenes.class)) {
//            return (Codec<T>) new ScenesCodec();
//        }
//        return null;
//    }
//
//}
