package com.wl4g.rengine.service.mongo;

import org.bson.Document;
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

import com.wl4g.rengine.common.entity.DataSourceProperties;
import com.wl4g.rengine.common.entity.Notification;
import com.wl4g.rengine.common.entity.ScheduleTrigger;
import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.util.BsonEntitySerializers;

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
        adapter.registerConverter(new DataSourcePropertiesToDocumentConverter());
        adapter.registerConverter(new DocumentToDataSourcePropertiesConverter());
        adapter.registerConverter(new DocumentToNotificationConverter());
        adapter.registerConverter(new DocumentToSchedulingTriggerConverter());
    }

    @WritingConverter
    static class WorkflowGraphToDocumentConverter implements Converter<WorkflowGraph, Document> {
        @Override
        public Document convert(final WorkflowGraph source) {
            return BsonEntitySerializers.toDocument(source);
        }
    }

    @ReadingConverter
    static class DocumentToWorkflowGraphConverter implements Converter<Document, WorkflowGraph> {
        @Override
        public WorkflowGraph convert(Document source) {
            return BsonEntitySerializers.fromDocument(source, WorkflowGraph.class);
        }
    }

    @WritingConverter
    static class DataSourcePropertiesToDocumentConverter implements Converter<DataSourceProperties, Document> {
        @Override
        public Document convert(final DataSourceProperties source) {
            return BsonEntitySerializers.toDocument(source);
        }
    }

    @ReadingConverter
    static class DocumentToDataSourcePropertiesConverter implements Converter<Document, DataSourceProperties> {
        @Override
        public DataSourceProperties convert(Document source) {
            return BsonEntitySerializers.fromDocument(source, DataSourceProperties.class);
        }
    }

    @WritingConverter
    static class NotificationToDocumentConverter implements Converter<Notification, Document> {
        @Override
        public Document convert(final Notification source) {
            return BsonEntitySerializers.toDocument(source);
        }
    }

    @ReadingConverter
    static class DocumentToNotificationConverter implements Converter<Document, Notification> {
        @Override
        public Notification convert(Document source) {
            return BsonEntitySerializers.fromDocument(source, Notification.class);
        }
    }

    @WritingConverter
    static class SchedulingTriggerToDocumentConverter implements Converter<ScheduleTrigger, Document> {
        @Override
        public Document convert(final ScheduleTrigger source) {
            return BsonEntitySerializers.toDocument(source);
        }
    }

    @ReadingConverter
    static class DocumentToSchedulingTriggerConverter implements Converter<Document, ScheduleTrigger> {
        @Override
        public ScheduleTrigger convert(Document source) {
            return BsonEntitySerializers.fromDocument(source, ScheduleTrigger.class);
        }
    }

}