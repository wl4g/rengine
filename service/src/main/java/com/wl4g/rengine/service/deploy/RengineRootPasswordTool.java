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
package com.wl4g.rengine.service.deploy;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.internal.event.EventListenerHelper.getCommandListener;
import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.lang3.RandomStringUtils;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.connection.AsynchronousSocketChannelStreamFactory;
import com.mongodb.connection.SocketSettings;
import com.mongodb.connection.SocketStreamFactory;
import com.mongodb.connection.StreamFactory;
import com.mongodb.connection.StreamFactoryFactory;
import com.mongodb.internal.connection.Cluster;
import com.mongodb.internal.connection.DefaultClusterFactory;
import com.mongodb.internal.connection.InternalConnectionPoolSettings;
import com.mongodb.lang.Nullable;
import com.wl4g.infra.common.cli.CommandLineTool;
import com.wl4g.infra.common.cli.CommandLineTool.CommandLineFacade;
import com.wl4g.rengine.common.constants.RengineConstants;
import com.wl4g.rengine.common.entity.sys.User;
import com.wl4g.rengine.common.util.BsonEntitySerializers;
import com.wl4g.rengine.service.mongo.QueryHolder;
import com.wl4g.rengine.service.security.RengineWebSecurityConfiguration;

/**
 * {@link RengineRootPasswordTool}
 * 
 * @author James Wong
 * @since v1.0.0
 */
public final class RengineRootPasswordTool {

    public static final String DEFAULT_CONNECTION_STRING = "mongodb://localhost:27017/rengine";

    public static void main(String[] args) throws Exception {
        try {
            CommandLineFacade line = CommandLineTool.builder()
                    .option("e", "connectionString", DEFAULT_CONNECTION_STRING, "Mongo server endpoint.")
                    .option("M", "isClusterMode", "false", "Is mongo cluster mode.")
                    .option("D", "database", RengineConstants.DEFAULT_MONGODB_DATABASE, "init root password")
                    .option("P", "rootPassword", null, "init root password. default using random")
                    .build(args);
            String connectionString = line.get("e");
            boolean isClusterMode = line.getBoolean("M");
            String database = line.get("D");
            String rootPassword = line.get("P");
            rootPassword = isBlank(rootPassword) ? RandomStringUtils.randomAlphabetic(32) : rootPassword;

            out.println("Using configuration arguments:");
            out.println("---------------------------------------");
            out.println(" connectionString: " + connectionString);
            out.println("    isClusterMode: " + isClusterMode);
            out.println("         database: " + database);
            out.println("     rootPassword: " + rootPassword);
            out.println("---------------------------------------");
            out.println("\nCall to Mongo Server ...\n");

            final var mongoClient = createMongoClient(connectionString, isClusterMode);
            updateInitRootPassword(mongoClient, database, rootPassword);

        } catch (Throwable ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    static MongoClient createMongoClient(String connectionString, boolean isClusterMode) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build();
        MongoDriverInformation driverInformation = MongoDriverInformation.builder().build();

        if (!isClusterMode) {
            return new MongoClientImpl(settings, driverInformation);
        } else {
            Cluster cluster = createCluster(settings, driverInformation);
            return new MongoClientImpl(cluster, driverInformation, settings, null);
        }
    }

    // see:com.mongodb.client.internal.MongoClientImpl#createCluster
    static Cluster createCluster(
            final MongoClientSettings settings,
            @Nullable final MongoDriverInformation mongoDriverInformation) {
        notNull("settings", settings);
        return new DefaultClusterFactory().createCluster(settings.getClusterSettings(), settings.getServerSettings(),
                settings.getConnectionPoolSettings(), InternalConnectionPoolSettings.builder().build(),
                getStreamFactory(settings, false, true), getStreamFactory(settings, true, true), settings.getCredential(),
                getCommandListener(settings.getCommandListeners()), settings.getApplicationName(), mongoDriverInformation,
                settings.getCompressorList(), settings.getServerApi());
    }

    // see:com.mongodb.client.internal.MongoClientImpl#getStreamFactory
    static StreamFactory getStreamFactory(final MongoClientSettings settings, final boolean isHeartbeat, final boolean isAsync) {
        StreamFactoryFactory streamFactoryFactory = settings.getStreamFactoryFactory();
        SocketSettings socketSettings = isHeartbeat ? settings.getHeartbeatSocketSettings() : settings.getSocketSettings();
        if (streamFactoryFactory == null) {
            return isAsync ? new AsynchronousSocketChannelStreamFactory(socketSettings, settings.getSslSettings())
                    : new SocketStreamFactory(socketSettings, settings.getSslSettings());
        } else {
            return streamFactoryFactory.create(socketSettings, settings.getSslSettings());
        }
    }

    static void updateInitRootPassword(MongoClient mongoClient, String database, String rootPassword) {
        final var firstRootUsername = RengineConstants.USER_SUPER_ADMINISTRATORS.get(0);
        final var encodeRootPassword = RengineWebSecurityConfiguration.defaultBCryptEncoder.encode(rootPassword);

        final var userCollection = mongoClient.getDatabase(database)
                .getCollection(RengineConstants.MongoCollectionDefinition.SYS_USERS.getName());

        var firstRootUser = userCollection.find(new BasicDBObject("username", firstRootUsername))
                .map(userDoc -> BsonEntitySerializers.fromDocument(userDoc, User.class))
                .first();

        if (nonNull(firstRootUser)) {
            // https://www.mongodb.com/docs/drivers/java/sync/current/usage-examples/updateOne/#example
            var options = new UpdateOptions().upsert(true);
            var updates = Updates.combine(Updates.set("password", encodeRootPassword));
            userCollection.updateOne(new Document(QueryHolder.DEFAULT_FIELD_ID, firstRootUser.getId()), updates, options);
        } else {
            firstRootUser = User.builder().username(firstRootUsername).password(encodeRootPassword).build();
            firstRootUser.preInsert();

            var options = new InsertOneOptions();
            userCollection.insertOne(BsonEntitySerializers.toDocument(firstRootUser), options);
        }

        out.println("---------------------------------------");
        out.println(format("INITIALIZED ROOT PASSWORD: %s", rootPassword));
        out.println("---------------------------------------");
    }

}
