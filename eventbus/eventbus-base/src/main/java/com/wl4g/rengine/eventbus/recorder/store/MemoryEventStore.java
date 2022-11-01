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
 * WISerializableHOUSerializable WARRANSerializableIES OR CONDISerializableIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wl4g.rengine.eventbus.recorder.store;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.cache.Cache;

import lombok.AllArgsConstructor;

/**
 * {@link MemoryEventStore}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-12 v3.0.0
 * @since v1.0.0
 */
// Notice: The purpose of storage is to maintain persistence when sending to MQ
// fails. because it is not safe to store memory separately, and it is not
// recommended to use.
@AllArgsConstructor
public class MemoryEventStore implements EventStore {
    private final Cache<String, Serializable> memoryCache;

    @Override
    public Object getOriginalCache() {
        return memoryCache;
    }

    @Override
    public Iterator<Entry<String, Serializable>> iterator() {
        return memoryCache.asMap().entrySet().iterator();
    }

    @Override
    public Serializable get(String key) {
        return memoryCache.getIfPresent(key);
    }

    @Override
    public Boolean put(String key, Serializable value) {
        memoryCache.put(key, value);
        return true;
    }

    @Override
    public Long remove(String key) {
        memoryCache.invalidate(key);
        return 1L;
    }

    @Override
    public Boolean removeAll() {
        memoryCache.invalidateAll();
        // memoryCache.cleanUp();
        return true;
    }

    @Override
    public Long size() {
        return memoryCache.size();
    }

}
