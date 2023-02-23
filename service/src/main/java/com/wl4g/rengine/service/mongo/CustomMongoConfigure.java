package com.wl4g.rengine.service.mongo;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
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

import com.wl4g.infra.common.bean.BaseBean;
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

    //// @formatter:off
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
    //// @formatter:on

    @Override
    protected void configureConverters(MongoConverterConfigurationAdapter adapter) {
        // Used generic serialization converter.
        //// @formatter:off
        //adapter.registerConverter(new WorkflowGraphToDocumentConverter());
        //adapter.registerConverter(new DocumentToWorkflowGraphConverter());
        //
        //adapter.registerConverter(new DataSourcePropertiesToDocumentConverter());
        //adapter.registerConverter(new DocumentToDataSourcePropertiesConverter());
        //
        //adapter.registerConverter(new IdentityProviderToDocumentConverter());
        //adapter.registerConverter(new DocumentToIdentityProviderConverter());
        //
        //adapter.registerConverter(new NotificationToDocumentConverter());
        //adapter.registerConverter(new DocumentToNotificationConverter());
        //
        //adapter.registerConverter(new ControllerScheduleToDocumentConverter());
        //adapter.registerConverter(new DocumentToControllerScheduleConverter());
        //// @formatter:on

        // The universal converter that implements deserialization of data read
        // from any mongo table into an entity class.
        adapter.registerConverterFactory(new ConverterFactory<Document, Object>() {
            @Override
            public <T> Converter<Document, T> getConverter(Class<T> targetType) {
                return (Converter<Document, T>) doc -> BsonEntitySerializers.fromDocument(doc, targetType);
            }
        });

        // The generic converter that implements serialization from any entity
        // bean to a bson document for writes it to a mongo table
        adapter.registerConverterFactory(new ConverterFactory<BaseBean, Document>() {
            @SuppressWarnings("unchecked")
            @Override
            public <T extends Document> Converter<BaseBean, T> getConverter(Class<T> targetType) {
                return (Converter<BaseBean, T>) new Converter<BaseBean, Document>() {
                    @Override
                    public Document convert(BaseBean entity) {
                        return BsonEntitySerializers.toDocument((BaseBean) entity);
                    }
                };
            }
        });

    }

    //// @formatter:off
    //@WritingConverter
    //static class WorkflowGraphToDocumentConverter implements Converter<WorkflowGraph, Document> {
    //    @Override
    //    public Document convert(final WorkflowGraph source) {
    //        return BsonEntitySerializers.toDocument(source);
    //    }
    //}
    //
    //@ReadingConverter
    //static class DocumentToWorkflowGraphConverter implements Converter<Document, WorkflowGraph> {
    //    @Override
    //    public WorkflowGraph convert(Document source) {
    //        return BsonEntitySerializers.fromDocument(source, WorkflowGraph.class);
    //    }
    //}
    //
    //@WritingConverter
    //static class DataSourcePropertiesToDocumentConverter implements Converter<DataSourceProperties, Document> {
    //    @Override
    //    public Document convert(final DataSourceProperties source) {
    //        return BsonEntitySerializers.toDocument(source);
    //    }
    //}
    //
    //@ReadingConverter
    //static class DocumentToDataSourcePropertiesConverter implements Converter<Document, DataSourceProperties> {
    //    @Override
    //    public DataSourceProperties convert(Document source) {
    //        return BsonEntitySerializers.fromDocument(source, DataSourceProperties.class);
    //    }
    //}
    //
    //@WritingConverter
    //static class IdentityProviderToDocumentConverter implements Converter<IdentityProvider, Document> {
    //    @Override
    //    public Document convert(final IdentityProvider source) {
    //        return BsonEntitySerializers.toDocument(source);
    //    }
    //}
    //
    //@ReadingConverter
    //static class DocumentToIdentityProviderConverter implements Converter<Document, IdentityProvider> {
    //    @Override
    //    public IdentityProvider convert(Document source) {
    //        return BsonEntitySerializers.fromDocument(source, IdentityProvider.class);
    //    }
    //}
    //
    //@WritingConverter
    //static class NotificationToDocumentConverter implements Converter<Notification, Document> {
    //    @Override
    //    public Document convert(final Notification source) {
    //        return BsonEntitySerializers.toDocument(source);
    //    }
    //}
    //
    //@ReadingConverter
    //static class DocumentToNotificationConverter implements Converter<Document, Notification> {
    //    @Override
    //    public Notification convert(Document source) {
    //        return BsonEntitySerializers.fromDocument(source, Notification.class);
    //    }
    //}
    //
    //@WritingConverter
    //static class ControllerScheduleToDocumentConverter implements Converter<ControllerSchedule, Document> {
    //    @Override
    //    public Document convert(final ControllerSchedule source) {
    //        return BsonEntitySerializers.toDocument(source);
    //    }
    //}
    //
    //@ReadingConverter
    //static class DocumentToControllerScheduleConverter implements Converter<Document, ControllerSchedule> {
    //    @Override
    //    public ControllerSchedule convert(Document source) {
    //        return BsonEntitySerializers.fromDocument(source, ControllerSchedule.class);
    //    }
    //}
    //// @formatter:on

}