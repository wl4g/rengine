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
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

import java.io.File;
import java.util.Collections;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.Before;
import org.junit.Test;

import com.wl4g.infra.common.graalvm.polyglot.GraalPolyglotManager.ContextWrapper;
import com.wl4g.rengine.executor.util.TestDefaultBaseSetup;
import com.wl4g.rengine.executor.util.TestDefaultRedisSetup;

/**
 * {@link GraalJSScriptEngineTests}
 * 
 * @author James Wong
 * @version 2022-09-23
 * @since v1.0.0
 * @see https://github.com/wl4g/infra/blob/master/common-java11/src/test/java/com/wl4g/infra/common/graalvm/GraalPolyglotManagerTests.java#L97
 */
public class GraalJSScriptEngineTests {

    GraalJSScriptEngine engine;

    @Before
    public void init() {
        this.engine = new GraalJSScriptEngine();
        engine.engineConfig = TestDefaultBaseSetup.createEngineConfig();
        engine.redisDS = TestDefaultRedisSetup.buildRedisDataSourceDefault();
        engine.init();
    }

    @Test
    public void testInitWithCustomLog() throws Exception {
        try (ContextWrapper graalContext = engine.getGraalPolyglotManager()
                .getContext(singletonMap(KEY_WORKFLOW_ID, 101001010L));) {
            // @formatter:off
            final String script = "function testLog(name){"
                                    + "console.info(\"The testing info logs name is:\", name);"
                                    + "console.warn(\"The testing warn logs name is:\", name);"
                                    + "console.error(\"The testing error logs name is:\", name);"
                                    + "return name;"
                                + "}";
            // @formatter:on
            graalContext.eval(Source.newBuilder("js", script, "test.js").build());
            final Value bindings = graalContext.getBindings("js");
            final Value testFunction = bindings.getMember("testLog");
            final Value result = testFunction.execute("jack001");
            System.out.println("result: " + result);
            assert result.toString().equals("jack001");
        }
    }

    @Test
    public void testRunDuplicateEntrypointMethods() throws Exception {
        /**
         * Testing sorting by
         * {@link com.wl4g.rengine.executor.execution.engine.AbstractScriptEngine#loadScriptResources}
         */
        final var list = asList("one", "two", "three", "four", "five");
        Collections.sort(list, (o1, o2) -> o1.equals("one") ? 1 : -1);
        System.out.println(list);
        assert list.get(list.size() - 1).equals("one");

        final GraalJSScriptEngine engine = new GraalJSScriptEngine();
        engine.engineConfig = TestDefaultBaseSetup.createEngineConfig();
        engine.redisDS = TestDefaultRedisSetup.buildRedisDataSourceDefault();
        engine.init();

        try (ContextWrapper graalContext = engine.getGraalPolyglotManager()
                .getContext(singletonMap(KEY_WORKFLOW_ID, 101001010L));) {
            // @formatter:off
            final String script1 = "function process(name){"
                                    + "console.info(\"The processing of method1:\");"
                                    + "return 'method1';"
                                + "}";
            final String script2 = "function process(name){"
                    + "console.info(\"The processing of method2:\");"
                    + "return 'method2';"
                    + "}";
            // @formatter:on

            graalContext.eval(Source.newBuilder("js", script1, "test1.js").build());
            graalContext.eval(Source.newBuilder("js", script2, "test2.js").build());

            final Value bindings = graalContext.getBindings("js");
            final Value processFunction = bindings.getMember("process");
            final Value result = processFunction.execute("jack001");
            System.out.println("result: " + result);

            assert result.toString().equals("method2");
        }
    }

    @Test
    public void testExtractStackCausesAsLog() {
        // @formatter:off
        final String errorStackMessage = "2023-24-45 19:45:12 WARN co.wl.re.ex.ex.DefaultWorkflowExecution (ReactiveEngineExecutionServiceImpl-1) Failed to execution workflow graph for workflowId: 6150868953448440[39m[38;5;203m: com.wl4g.rengine.common.exception.ExecutionGraphException: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script\n"
                + "   at com.wl4g.rengine.common.graph.ExecutionGraph$BaseOperator.doExecute(ExecutionGraph.java:300)\n"
                + "   ... 10 more\n"
                + "   Caused by: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script\n"
                + "   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine.execute(GraalJSScriptEngine.java:207)\n"
                + "   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine_Subclass.execute$$superforward1(Unknown Source)\n"
                + "   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine_Subclass$$function$$6.apply(Unknown Source)\n"
                + "   at io.quarkus.arc.impl.AroundInvokeInvocationContext.proceed(AroundInvokeInvocationContext.java:53)\n"
                + "   ... 28 more\n"
                + "   Caused by: org.graalvm.polyglot.PolyglotException: [AF] - vmHost is required\n"
                + "   at com.wl4g.infra.common.lang.Assert2.hasText(Assert2.java:608)\n"
                + "   at com.wl4g.infra.common.lang.Assert2.hasTextOf(Assert2.java:772)\n"
                + "   at com.wl4g.rengine.executor.execution.sdk.tools.Assert.hasTextOf(Assert.java:80)\n"
                + "   at <js>.process(vm-health-detecter-1.0.0.js@6150868953448440:4)\n"
                + "   at <js> process(vm-health-detecter-1.0.0.js@6150868953448440:4)\n"
                + "   at org.graalvm.polyglot.Value.execute(Value.java:841)\n"
                + "   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine.execute(GraalJSScriptEngine.java:198)\n"
                + "   at com.wl4g.rengine.executor.execution.engine.GraalJSScriptEngine_Subclass.execute$$superforward1(Unknown Source)\n"
                + "   ... 5 more";
        // @formatter:on

        final String errmsg = GraalJSScriptEngine.extractStackCausesAsLog(errorStackMessage);
        System.out.println(errmsg);

        assert containsIgnoreCase(errmsg,
                "Caused by: com.wl4g.rengine.common.exception.EvaluationException: Failed to execute script");
        assert containsIgnoreCase(errmsg, "Caused by: org.graalvm.polyglot.PolyglotException: [AF] - vmHost is required");
        assert containsIgnoreCase(errmsg, "at <js>.process(vm-health-detecter-1.0.0.js@6150868953448440:4)");
        assert containsIgnoreCase(errmsg, "at <js> process(vm-health-detecter-1.0.0.js@6150868953448440:4)");
    }

    @Test
    public void testInvokeSimpleFile() throws Exception {
        try (ContextWrapper graalContext = engine.getGraalPolyglotManager()
                .getContext(singletonMap(KEY_WORKFLOW_ID, 101001010L));) {
            // @formatter:off
            final String script = "function testFile(path){"
                    + "console.info(\"The path is:\", path);"
//                    + "var file = new File(path);"
                    + "console.info(file.list());"
                    + "return name;"
                    + "}";
            // @formatter:on

            graalContext.eval(Source.newBuilder("js", script, "test.js").build());
            final Value bindings = graalContext.getBindings("js");
            bindings.putMember("file", new File("/tmp"));
            final Value testFunction = bindings.getMember("testFile");
            final Value result = testFunction.execute("/tmp");
            System.out.println("result: " + result);
        }
    }

}
