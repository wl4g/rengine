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
package com.wl4g.rengine.executor.util.redis;

import static com.google.common.base.Charsets.UTF_8;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.collection.CollectionUtils2.safeMap;
import static java.util.Objects.nonNull;

import java.util.Map;

import com.wl4g.infra.common.lang.ThreadUtils2;

import io.quarkus.redis.datasource.string.GetExArgs;
import io.quarkus.redis.datasource.string.SetArgs;
import io.quarkus.redis.datasource.string.StringCommands;
import io.vertx.core.Future;
import io.vertx.redis.client.Command;
import io.vertx.redis.client.Response;
import io.vertx.redis.client.impl.RequestImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * {@link StringCommandsImpl}
 * 
 * @author James Wong
 * @version 2023-02-11
 * @since v1.0.0
 */
@Getter
@AllArgsConstructor
public class StringCommandsImpl<K, V> implements StringCommands<K, V> {

    private final RedisDataSourceImpl redisDS;
    private final Marshaller marshaller;
    protected final Class<V> typeOfValue;

    // see:io.quarkus.redis.runtime.datasource.ReactiveStringCommandsImpl#append()
    // see:io.quarkus.redis.runtime.datasource.AbstractStringCommands#_append()
    @Override
    public long append(K key, V value) {
        return redisDS.execute(new RequestImpl(Command.APPEND).arg(marshaller.encode(key)).arg(marshaller.encode(value)))
                .result()
                .toLong();
    }

    @Override
    public long decr(K key) {
        return redisDS.execute(new RequestImpl(Command.DECR).arg(marshaller.encode(key))).result().toLong();
    }

    @Override
    public long decrby(K key, long amount) {
        return redisDS.execute(new RequestImpl(Command.DECRBY).arg(marshaller.encode(key)).arg(amount)).result().toLong();
    }

    @Override
    public V get(K key) {
        final Future<Response> future = redisDS.execute(new RequestImpl(Command.GET).arg(marshaller.encode(key)));
        // TODO 统一等待?
        while (!future.isComplete()) {
            ThreadUtils2.sleep(100);
        }
        final Response response = future.result();
        return nonNull(response) ? marshaller.decode(typeOfValue, response.toBytes()) : null;
    }

    @Override
    public V getdel(K key) {
        final Response response = redisDS.execute(new RequestImpl(Command.GETDEL).arg(marshaller.encode(key))).result();
        return marshaller.decode(typeOfValue, response.toBytes());
    }

    @Override
    public V getex(K key, GetExArgs args) {
        final RequestImpl request = new RequestImpl(Command.GETEX);
        request.arg(marshaller.encode(key));
        for (String arg : safeList(args.toArgs())) {
            request.arg(arg);
        }
        final Response response = redisDS.execute(request).result();
        return marshaller.decode(typeOfValue, response.toBytes());
    }

    @Override
    public String getrange(K key, long start, long end) {
        return redisDS.execute(new RequestImpl(Command.GETRANGE).arg(marshaller.encode(key)).arg(start).arg(end))
                .result()
                .toString(UTF_8);
    }

    @Override
    public V getset(K key, V value) {
        final Response response = redisDS.execute(new RequestImpl(Command.GETEX)).result();
        return marshaller.decode(typeOfValue, response.toBytes());
    }

    @Override
    public long incr(K key) {
        return redisDS.execute(new RequestImpl(Command.INCR).arg(marshaller.encode(key))).result().toLong();
    }

    @Override
    public long incrby(K key, long amount) {
        return redisDS.execute(new RequestImpl(Command.INCRBY).arg(marshaller.encode(key)).arg(amount)).result().toLong();
    }

    @Override
    public double incrbyfloat(K key, double amount) {
        return redisDS.execute(new RequestImpl(Command.INCRBYFLOAT).arg(marshaller.encode(key)).arg(amount)).result().toLong();
    }

    @Override
    public String lcs(K key1, K key2) {
        return redisDS.execute(new RequestImpl(Command.LCS).arg(marshaller.encode(key1)).arg(marshaller.encode(key2)))
                .result()
                .toString(UTF_8);
    }

    // see:io.quarkus.redis.runtime.datasource.AbstractStringCommands#_lcsLength()
    @Override
    public long lcsLength(K key1, K key2) {
        return redisDS.execute(new RequestImpl(Command.LCS).arg(marshaller.encode(key1)).arg(marshaller.encode(key2)).arg("LEN"))
                .result()
                .toLong();
    }

    // see:io.quarkus.redis.runtime.datasource.ReactiveStringCommandsImpl#mget()
    @SuppressWarnings("unchecked")
    @Override
    public Map<K, V> mget(K... keys) {
        final RequestImpl request = new RequestImpl(Command.MGET);
        for (Object arg : marshaller.encode(keys)) {
            request.arg(arg.toString());
        }
        final Response response = redisDS.execute(request).result();
        return marshaller.decodeAsOrderedMap(new io.vertx.mutiny.redis.client.Response(response), typeOfValue, keys);
    }

    @Override
    public void mset(Map<K, V> map) {
        final RequestImpl request = new RequestImpl(Command.MSET);
        for (Map.Entry<K, V> entry : safeMap(map).entrySet()) {
            request.arg(marshaller.encode(entry.getKey()));
            request.arg(marshaller.encode(entry.getValue()));
        }
        redisDS.execute(request).result();
    }

    @Override
    public boolean msetnx(Map<K, V> map) {
        final RequestImpl request = new RequestImpl(Command.MSETNX);
        for (Map.Entry<K, V> entry : safeMap(map).entrySet()) {
            request.arg(marshaller.encode(entry.getKey()));
            request.arg(marshaller.encode(entry.getValue()));
        }
        return redisDS.execute(request).result().toBoolean();
    }

    @Override
    public void set(K key, V value) {
        redisDS.execute(new RequestImpl(Command.INCRBYFLOAT).arg(marshaller.encode(key)).arg(marshaller.encode(value))).result();
    }

    @Override
    public void set(K key, V value, SetArgs setArgs) {
        final RequestImpl request = new RequestImpl(Command.SET);
        request.arg(marshaller.encode(key));
        for (String arg : safeList(setArgs.toArgs())) {
            request.arg(arg);
        }
        redisDS.execute(request).result();
    }

    @Override
    public V setGet(K key, V value) {
        return setGet(key, value, new SetArgs());
    }

    @Override
    public V setGet(K key, V value, SetArgs setArgs) {
        final RequestImpl request = new RequestImpl(Command.SET);
        request.arg(marshaller.encode(key));
        request.arg(marshaller.encode(value));
        for (String arg : safeList(setArgs.get().toArgs())) {
            request.arg(arg);
        }
        final Response response = redisDS.execute(request).result();
        return marshaller.decode(typeOfValue, response.toBytes());
    }

    @Override
    public void setex(K key, long seconds, V value) {
        redisDS.execute(new RequestImpl(Command.SETEX).arg(marshaller.encode(key)).arg(marshaller.encode(value))).result();
    }

    @Override
    public void psetex(K key, long milliseconds, V value) {
        redisDS.execute(new RequestImpl(Command.PSETEX).arg(marshaller.encode(key)).arg(marshaller.encode(value))).result();
    }

    @Override
    public boolean setnx(K key, V value) {
        return redisDS.execute(new RequestImpl(Command.SETNX).arg(marshaller.encode(key)).arg(marshaller.encode(value)))
                .result()
                .toBoolean();
    }

    @Override
    public long setrange(K key, long offset, V value) {
        return redisDS.execute(new RequestImpl(Command.SETRANGE).arg(marshaller.encode(key)).arg(marshaller.encode(value)))
                .result()
                .toLong();
    }

    @Override
    public long strlen(K key) {
        return redisDS.execute(new RequestImpl(Command.STRLEN).arg(marshaller.encode(key))).result().toLong();
    }

}
