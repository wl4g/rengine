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
package com.wl4g.rengine.executor.execution.sdk;

import static com.wl4g.infra.common.lang.StringUtils2.eqIgnCase;
import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;

import java.util.List;
import java.util.concurrent.locks.Lock;

import org.junit.BeforeClass;
import org.junit.Test;

import com.wl4g.rengine.executor.metrics.MeterUtilTests;
import com.wl4g.rengine.executor.util.TestDefaultRedisSetup;

import io.vertx.mutiny.redis.client.RedisAPI;
import io.vertx.mutiny.redis.client.Response;

/**
 * {@link ScriptRedisLockClientTests}
 * 
 * @author James Wong
 * @version 2022-12-30
 * @since v1.0.0
 */
public class ScriptRedisLockClientTests {
    static String UNLOCK_LUA = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @BeforeClass
    public static void setup() {
        MeterUtilTests.setup();
    }

    @Test
    public void testQuarksRedisEvalScript() {
        final RedisAPI redisApi = TestDefaultRedisSetup
                .buildRedisAPIDefault(TestDefaultRedisSetup.buildRedisDefault(TestDefaultRedisSetup.buildVertxDefault()));

        final List<String> keys = asList("testLocks");
        final int numkeys = keys.size();
        final List<String> args = asList("b9bc3e0e-d705-4ff2-9edf-970dcf95dea5");
        Response response1 = redisApi.evalAndAwait(asList(UNLOCK_LUA, valueOf(numkeys), keys.get(0), args.get(0)));
        System.out.println(response1);
        // System.out.println(response1.getKeys());
        System.out.println(response1.attributes());

        assert eqIgnCase(response1.toString(), "0");

        // final io.quarkus.redis.datasource.RedisDataSource redisDS = new
        // io.quarkus.redis.runtime.datasource.BlockingRedisDataSourceImpl(new
        // io.vertx.mutiny.core.Vertx(vertx), redis, redisApi,
        // Duration.ofSeconds(5));

        // Response response2 =
        // redisApi.script(asList(UNLOCK_LUA)).await().atMost(Duration.ofSeconds(3));
        // System.out.println(response2);

        // Response response3 =
        // redisDS.execute(io.vertx.mutiny.redis.client.Command.EVAL,
        // UNLOCK_LUA);
        // System.out.println(response3);
    }

    @Test
    public void testGetLockWithRedisDataSource() {
        final io.quarkus.redis.datasource.RedisDataSource redisDS = TestDefaultRedisSetup.buildRedisDataSourceDefault();

        final ScriptRedisLockClient lockClient = new ScriptRedisLockClient(redisDS);
        final Lock lock = lockClient.getLock("testLock");
        final boolean locks = lock.tryLock();
        System.out.println(format("Gets locks : %s for : %s", lock, locks));
        assert locks;
    }

}
