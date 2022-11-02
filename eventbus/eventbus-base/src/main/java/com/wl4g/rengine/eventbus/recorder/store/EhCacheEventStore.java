/*
 * Copyright 2017 ~ 2025 the original authors James Wong.
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

import static com.wl4g.infra.common.lang.Assert2.hasTextOf;
import static com.wl4g.infra.common.lang.Assert2.notNullOf;
import static java.lang.String.format;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;

import com.wl4g.rengine.eventbus.config.ClientEventBusConfig.EventRecorderConfig.EhCacheRecorderConfig;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EhCacheResponseCacheTests}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-13 v3.0.0
 * @since v1.0.0
 */
@Getter
@Slf4j
public class EhCacheEventStore implements EventStore, Closeable {
    protected final EhCacheRecorderConfig config;
    protected final CacheManager cacheManager;
    protected final Cache<String, Serializable> originalCache;

    public EhCacheEventStore(@NotNull EhCacheRecorderConfig config, @NotBlank String name) {
        this.config = notNullOf(config, "config");
        hasTextOf(name, "name");
        this.cacheManager = buildCacheManager(config, name);
        this.originalCache = cacheManager.getCache(EhCacheEventStore.class.getSimpleName().concat("-").concat(name), String.class,
                Serializable.class);
    }

    @Override
    public Object getOriginalCache() {
        return originalCache;
    }

    @Override
    public Iterator<Entry<String, Serializable>> iterator() {
        Iterator<org.ehcache.Cache.Entry<String, Serializable>> it = originalCache.iterator();
        return new Iterator<Entry<String, Serializable>>() {
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Entry<String, Serializable> next() {
                org.ehcache.Cache.Entry<String, Serializable> entry = it.next();
                return new Entry<String, Serializable>() {
                    @Override
                    public String getKey() {
                        return entry.getKey();
                    }

                    @Override
                    public Serializable getValue() {
                        return entry.getValue();
                    }

                    @Override
                    public Serializable setValue(Serializable value) {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    @Override
    public Serializable get(@NotNull String key) {
        notNullOf(key, "key");
        try {
            return originalCache.get(key);
        } catch (Exception e) {
            log.error(format("Cannot to get response cache of '%s'", key), e);
        }
        return null;
    }

    @Override
    public Boolean put(@NotNull String key, @NotNull Serializable value) {
        notNullOf(key, "key");
        notNullOf(value, "value");
        try {
            originalCache.put(key, value);
            return true;
        } catch (Exception e) {
            log.error(format("Cannot to put response cache of '%s' -> %s ...", key, value), e);
        }
        return false;
    }

    @Override
    public Long remove(String key) {
        notNullOf(key, "key");
        try {
            originalCache.remove(key);
            return 1L;
        } catch (Exception e) {
            log.error(format("Cannot to invalidate response cache of '%s'", key), e);
        }
        return 0L;
    }

    @Override
    public Boolean removeAll() {
        originalCache.clear();
        return true;
    }

    @Override
    public Long size() {
        return -1L;
    }

    @Override
    public void close() throws IOException {
        cacheManager.close();
    }

    /**
     * Build EhCache cache manager for name.
     * 
     * see:https://www.ehcache.org/documentation/3.10/
     * see:https://github.com/ehcache/ehcache3-samples
     * see:https://github1s.com/ehcache/ehcache3/blob/HEAD/ehcache-impl/src/test/java/org/ehcache/config/builders/PersistentCacheManagerTest.java#L92-L100
     * 
     * @param config
     * @param alias
     * @return
     */
    static PersistentCacheManager buildCacheManager(EhCacheRecorderConfig config, String name) {
        try {
            //@formatter:off
            // String prefix = "ehcache-tmp-";
            // File rootDir = new File(SystemUtils.JAVA_IO_TMPDIR, prefix + currentTimeMillis());
            // FileIOUtils.forceMkdir(rootDir);
            // rootDir.toPath().toFile().deleteOnExit();
            //@formatter:on

            String alias = EhCacheEventStore.class.getSimpleName().concat("-").concat(name);
            return newCacheManagerBuilder().with(new CacheManagerPersistenceConfiguration(config.getDataDir()))
                    .withCache(alias,
                            CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Serializable.class,
                                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                                            .heap(config.getHeapEntries(), EntryUnit.ENTRIES)
                                            .offheap(config.getOffHeapSize().toBytes(), MemoryUnit.B)
                                            .disk(config.getDiskSize().toBytes(), MemoryUnit.B, true)))
                    .build(true);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
