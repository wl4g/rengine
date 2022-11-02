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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.eventbus.recorder.store;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static com.wl4g.infra.common.lang.StringUtils2.getBytes;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseJSON;
import static com.wl4g.infra.common.serialize.JacksonUtils.toJSONString;
import static java.util.Objects.nonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.validation.constraints.NotNull;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import com.wl4g.infra.common.rocksdb.RocksDBService;
import com.wl4g.rengine.eventbus.config.ClientEventBusConfig.EventRecorderConfig.RocksDBRecorderConfig;

import lombok.CustomLog;

/**
 * {@link RocksDBEventStore}
 * 
 * @author James Wong
 * @version 2022-11-01
 * @since v1.0.0
 * @see https://github1s.com/apache/flink/blob/release-1.15.1/flink-state-backends/flink-statebackend-rocksdb/pom.xml#L60-L61
 * @see https://github1s.com/apache/flink/blob/release-1.15.1/flink-state-backends/flink-statebackend-rocksdb/src/main/java/org/apache/flink/contrib/streaming/state/EmbeddedRocksDBStateBackend.java#L890-L891
 * @see https://github1s.com/facebook/rocksdb/blob/v6.20.3/java/src/test/java/org/rocksdb/RocksIteratorTest.java#L35-L36
 */
@CustomLog
public class RocksDBEventStore implements EventStore, Closeable {
    public static final String DEFAULT_FAMILY_NAME = "f1";
    protected final RocksDBRecorderConfig config;
    protected final Class<?> valueClass;
    protected final RocksDBService rocksDBService;

    public RocksDBEventStore(@NotNull RocksDBRecorderConfig config, Class<?> valueClass) {
        this.config = notNullOf(config, "config");
        this.valueClass = notNullOf(valueClass, "valueClass");
        this.rocksDBService = new RocksDBService(config);
    }

    @Override
    public Object getOriginalCache() {
        return rocksDBService;
    }

    @Override
    public Iterator<Entry<String, Serializable>> iterator() {
        try {
            return rocksDBService.iterator(DEFAULT_FAMILY_NAME, Serializable.class,
                    value -> (Serializable) parseJSON(new String(value, UTF_8), valueClass));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Serializable get(String key) {
        log.debug("get key '{}'", key);
        Serializable value = null;
        try {
            byte[] bytes = rocksDBService.get(DEFAULT_FAMILY_NAME, key);
            if (bytes != null) {
                value = (Serializable) parseJSON(new String(bytes, UTF_8), valueClass);
            }
        } catch (RocksDBException e) {
            log.error("Error retrieving the entry with key: {}, cause: {}, message: {}", key, e.getCause(), e.getMessage());
        }
        log.info("get key '{}' returns '{}'", key, value);
        return value;
    }

    @Override
    public Boolean put(String key, Serializable value) {
        log.debug("saving value '{}' with key '{}'", value, key);
        try {
            rocksDBService.put(DEFAULT_FAMILY_NAME, key, getBytes(toJSONString(value)));
        } catch (RocksDBException e) {
            log.error("Error saving entry. Cause: '{}', message: '{}'", e.getCause(), e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Long remove(String key) {
        log.debug("deleting key '{}'", key);
        try {
            rocksDBService.delete(DEFAULT_FAMILY_NAME, key);
        } catch (RocksDBException e) {
            log.error("Error deleting entry, cause: '{}', message: '{}'", e.getCause(), e.getMessage());
            return 1L;
        }
        return 0L;
    }

    @Override
    public Boolean removeAll() {
        try {
            rocksDBService.deleteFamilyIfExist(DEFAULT_FAMILY_NAME);
        } catch (RocksDBException e) {
            log.error("Error deleting entry, cause: '{}', message: '{}'", e.getCause(), e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Long size() {
        log.debug("counting size ...");
        try {
            return rocksDBService.getRocksDB().getLongProperty("rocksdb.estimate-num-keys");
        } catch (RocksDBException e) {
            log.error("Error counting total, cause: '{}', message: '{}'", e.getCause(), e.getMessage());
        }
        return -1L;
    }

    @Override
    public void close() throws IOException {
        if (nonNull(rocksDBService)) {
            rocksDBService.close();
        }
    }

    static RocksDB buildRocksDB(RocksDBRecorderConfig config) {
        try {
            RocksDB.loadLibrary();
            Options options = new Options();
            options.setCreateIfMissing(true);
            Files.createDirectories(config.getDataDir().getParentFile().toPath());
            Files.createDirectories(config.getDataDir().getAbsoluteFile().toPath());
            RocksDB rocksDB = RocksDB.open(options, config.getDataDir().getAbsolutePath());
            log.info("RocksDB to initialized. - {}", config.getDataDir());
            return rocksDB;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
