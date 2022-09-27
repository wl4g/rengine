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
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.rengine.evaluator.service.codec;
//
//import org.bson.BsonReader;
//import org.bson.BsonString;
//import org.bson.BsonValue;
//import org.bson.BsonWriter;
//import org.bson.Document;
//import org.bson.codecs.Codec;
//import org.bson.codecs.CollectibleCodec;
//import org.bson.codecs.DecoderContext;
//import org.bson.codecs.EncoderContext;
//
//import com.mongodb.MongoClientSettings;
//import com.wl4g.rengine.common.entity.Scenes;
//import com.wl4g.rengine.common.util.IdGenUtil;
//
///**
// * {@link ScenesCodec}
// * 
// * @author James Wong
// * @version 2022-09-29
// * @since v3.0.0
// */
//public class ScenesCodec implements CollectibleCodec<Scenes> {
//
//    private final Codec<Document> documentCodec;
//
//    public ScenesCodec() {
//        this.documentCodec = MongoClientSettings.getDefaultCodecRegistry().get(Document.class);
//    }
//
//    @Override
//    public void encode(BsonWriter writer, Scenes bean, EncoderContext encoderContext) {
//        Document doc = new Document();
//        doc.put("name", bean.getName());
//        doc.put("remark", bean.getRemark());
//        // TODO more fields
//        documentCodec.encode(writer, doc, encoderContext);
//    }
//
//    @Override
//    public Class<Scenes> getEncoderClass() {
//        return Scenes.class;
//    }
//
//    @Override
//    public Scenes generateIdIfAbsentFromDocument(Scenes document) {
//        if (!documentHasId(document)) {
//            document.setId(IdGenUtil.next());
//        }
//        return document;
//    }
//
//    @Override
//    public boolean documentHasId(Scenes document) {
//        return document.getId() != null;
//    }
//
//    @Override
//    public BsonValue getDocumentId(Scenes document) {
//        return new BsonString(String.valueOf(document.getId()));
//    }
//
//    @Override
//    public Scenes decode(BsonReader reader, DecoderContext decoderContext) {
//        Document document = documentCodec.decode(reader, decoderContext);
//        Scenes bean = new Scenes();
//        if (document.getString("id") != null) {
//            bean.setId(document.getLong("id"));
//        }
//        bean.setName(document.getString("name"));
//        bean.setRemark(document.getString("remark"));
//        // TODO more fields
//        return bean;
//    }
//
//}