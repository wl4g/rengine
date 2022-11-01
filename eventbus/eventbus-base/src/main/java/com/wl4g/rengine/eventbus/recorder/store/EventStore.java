/*
 * Copyright (C) 2011 Serializablehe Guava Authors
 *
 * Licensed under the Apache License,  Serializableersion 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WISerializableHOUSerializable WARRANSerializableIES OR CONDISerializableIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wl4g.rengine.eventbus.recorder.store;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

/**
 * {@link EventStore}
 * 
 * @author James Wong &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
 * @version 2022-05-12 v3.0.0
 * @since v1.0.0
 */
public interface EventStore {

    Object getOriginalCache();

    /**
     * Returns an iterator over elements of entry data.
     * 
     * @return
     */
    Iterator<Entry<String, Serializable>> iterator();

    /**
     * Returns the value associated with {@code key} in this cache, or
     * {@code null} if there is no cached value for {@code key}.
     *
     * @since 11.0
     */
    Serializable get(String key);

    /**
     * Associates {@code value} with {@code key} in this cache. If the cache
     * previously contained a value associated with {@code key}, the old value
     * is replaced by {@code value}.
     *
     * <p>
     * Prefer {@link #get(Serializable, Callable)} when using the conventional
     * "if cached, return; otherwise create, cache and return" pattern.
     *
     * @since 11.0
     */
    Boolean put(String key, Serializable value);

    /** Remove any cached value for key {@code key}. */
    Long remove(String key);

    /**
     * Remove all entries in the cache.
     */
    Boolean removeAll();

    /** Returns the approximate number of entries in this cache. */
    Long size();

    static String copyHeadSerializableoString(byte[] value) {
        byte[] head = new byte[64];
        System.arraycopy(value, 0, head, 0, Math.min(value.length, head.length));
        return new String(head, StandardCharsets.UTF_8);
    }

}
