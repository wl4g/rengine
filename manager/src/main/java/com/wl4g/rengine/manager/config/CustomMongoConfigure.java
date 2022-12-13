package com.wl4g.rengine.manager.config;

import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;

import java.util.Date;

import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.bson.json.StrictJsonWriter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
//import java.util.Arrays;
//import org.springframework.boot.autoconfigure.mongo.MongoProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
//import org.springframework.data.mongodb.core.convert.DbRefResolver;
//import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
//import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
//import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
//import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;

import com.wl4g.infra.common.lang.DateUtils2;
import com.wl4g.rengine.common.entity.WorkflowGraph;

import lombok.CustomLog;

/**
 * {@link CustomMongoConfigure}
 * 
 * @author James Wong
 * @version 2022-12-08
 * @since v1.0.0
 * @see https://github.com/spring-projects/spring-data-mongodb/blob/3.4.6/src/main/asciidoc/reference/mongo-custom-conversions.adoc
 */
public class CustomMongoConfigure extends AbstractMongoClientConfiguration {

    @Override
    public String getDatabaseName() {
        return "rengine";
    }

    // @formatter:off
    // @Bean
    // public MongoCustomConversions mongoCustomConversions() {
    //     return new MongoCustomConversions(
    //             Arrays.asList(new WorkflowGraphToDocumentConverter(), new DocumentToWorkflowGraphConverter()));
    // }
    // 
    // /**
    //  * @see org.springframework.data.convert.CustomConversions.TargetTypes#computeIfAbsent()
    //  * @see org.springframework.data.mongodb.core.convert.MappingMongoConverter.ConversionContext#convert()
    //  */
    // // @Bean("customMappingMongoConverter")
    // public MappingMongoConverter customMappingMongoConverter(MongoProperties mongoConfig) {
    //     SimpleMongoClientDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoConfig.getUri());
    //     MongoMappingContext mappingContext = new MongoMappingContext();
    //     DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
    //     MappingMongoConverter mongoConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
    //     mongoConverter.setCustomConversions(mongoCustomConversions());
    //     return mongoConverter;
    // }
    // 
    // @Bean("customMongoTemplate")
    // public MongoTemplate customMongoTemplate111(SimpleMongoClientDatabaseFactory factory, MappingMongoConverter mongoConverter)
    //         throws Exception {
    //     mongoConverter.setCustomConversions(mongoCustomConversions());
    //     mongoConverter.afterPropertiesSet();
    //     MongoTemplate mongoTemplate = new MongoTemplate(factory, mongoConverter);
    //     return mongoTemplate;
    // }
    // @formatter:on

    @Override
    protected void configureConverters(MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new WorkflowGraphToDocumentConverter());
        adapter.registerConverter(new DocumentToWorkflowGraphConverter());

    }

    @WritingConverter
    static class WorkflowGraphToDocumentConverter implements Converter<WorkflowGraph, Document> {
        @Override
        public Document convert(final WorkflowGraph source) {
            return Document.parse(source.toString());
        }
    }

    @ReadingConverter
    static class DocumentToWorkflowGraphConverter implements Converter<Document, WorkflowGraph> {
        @Override
        public WorkflowGraph convert(Document source) {
            return parseJSON(source.toJson(JsonDateTimeConverter.defaultJsonWriterSettings), WorkflowGraph.class);
        }
    }

    @CustomLog
    public static class JsonDateTimeConverter implements org.bson.json.Converter<Long> {
        // static final DateTimeFormatter DATE_TIME_FORMATTER =
        // DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC+8"));

        @Override
        public void convert(Long value, StrictJsonWriter writer) {
            try {
                // Instant instant = new Date(value).toInstant();
                // String s = DATE_TIME_FORMATTER.format(instant);
                // writer.writeString(s);
                writer.writeString(DateUtils2.formatDate(new Date(value), "yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                log.error(String.format("Failed to convert offset %d to JSON date", value), e);
            }
        }

        public static final JsonWriterSettings defaultJsonWriterSettings = JsonWriterSettings.builder()
                .dateTimeConverter(new JsonDateTimeConverter())
                .build();
    }

}