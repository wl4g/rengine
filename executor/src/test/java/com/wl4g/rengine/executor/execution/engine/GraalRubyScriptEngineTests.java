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
package com.wl4g.rengine.executor.execution.engine;

import static com.wl4g.rengine.executor.execution.engine.AbstractScriptEngine.KEY_WORKFLOW_ID;
import static java.util.Collections.singletonMap;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Test;

import com.wl4g.infra.common.graalvm.polyglot.GraalPolyglotManager.ContextWrapper;
import com.wl4g.rengine.executor.util.TestDefaultBaseSetup;
import com.wl4g.rengine.executor.util.TestDefaultRedisSetup;

/**
 * {@link GraalRubyScriptEngineTests}
 * 
 * @author James Wong
 * @date 2022-09-23
 * @since v1.0.0
 * @see https://github.com/wl4g/infra/blob/master/common-java11/src/test/java/com/wl4g/infra/common/graalvm/GraalPolyglotManagerTests.java#L97
 */
public class GraalRubyScriptEngineTests {

    GraalRubyScriptEngine engine;

    @Before
    public void init() {
        this.engine = new GraalRubyScriptEngine();
        engine.engineConfig = TestDefaultBaseSetup.createEngineConfig();
        engine.redisDS = TestDefaultRedisSetup.buildRedisDataSourceDefault();
        engine.init();
    }

    // see:https://www.graalvm.org/22.2/reference-manual/embed-languages/#define-guest-language-functions-as-java-values
    @Test
    public void testSimpleRunRuby() throws Exception {
        try (ContextWrapper graalContext = engine.getGraalPolyglotManager()
                .getContext(singletonMap(KEY_WORKFLOW_ID, 101001010L));) {
            // @formatter:off
            final String script = "proc { |x| x + 10 }";
            System.out.println(script);

            // @formatter:on
            graalContext.eval(Source.newBuilder("ruby", script, "test.rb").build());
            final Value bindings = graalContext.getBindings("ruby");
            final Value testFunction = bindings.getMember("testHello");
            final Value result = testFunction.execute(20);
            System.out.println("result: " + result);
            assert result.asLong() == 30;
        }
    }

}
