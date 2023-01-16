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
package com.wl4g.rengine.common.util;

import static com.wl4g.infra.common.collection.CollectionUtils2.safeList;
import static com.wl4g.infra.common.serialize.JacksonUtils.parseMapObject;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.wl4g.infra.common.collection.CollectionUtils2;
import com.wl4g.rengine.common.entity.WorkflowGraph;
import com.wl4g.rengine.common.entity.WorkflowGraphTests;

/**
 * {@link BsonEntitySerializersTests}
 * 
 * @author James Wong
 * @version 2023-01-16
 * @since v1.0.0
 */
public class BsonEntitySerializersTests {

    @SuppressWarnings("unchecked")
    @Test
    public void testSerialze() {
        final String json = BsonEntitySerializers.serialize(WorkflowGraphTests.buildDefaultWorkflowGraph());
        System.out.println("json : " + json);

        final Map<String, Object> map = parseMapObject(json);
        assert map.keySet().contains("_id");
        assert !map.keySet().contains("id");
        // Ignored fields should not be generated.
        assert !CollectionUtils2.containsAny(map.keySet(), BsonEntitySerializers.IGNORE_PROPERTIES);

        final List<Map<String, Object>> nodes = (List<Map<String, Object>>) map.get("nodes");
        System.out.println("nodes : " + nodes);
        assert safeList(nodes).stream().allMatch(n -> n.keySet().contains("id") && !n.keySet().contains("_id"));
    }

    @Test
    public void testDeserialze() {
        System.out.println("json : " + WorkflowGraphTests.testWorkflowGraphJson);
        final WorkflowGraph graph = BsonEntitySerializers.deserialize(WorkflowGraphTests.testWorkflowGraphJson,
                WorkflowGraph.class);
        System.out.println("graph : " + graph);

        assert nonNull(graph.getId());
        assert nonNull(graph.getNodes().get(0).getId());
    }

}
