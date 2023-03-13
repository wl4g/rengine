package org.apache.flink.cep.dynamic.impl.json.deserializer;

import static java.util.Objects.nonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.flink.cep.dynamic.impl.json.spec.AfterMatchSkipStrategySpec;
import org.apache.flink.cep.dynamic.impl.json.spec.ConditionSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.EdgeSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.GraphSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.NodeSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.QuantifierSpec;
import org.apache.flink.cep.dynamic.impl.json.spec.WindowSpec;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.TreeNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class NodeSpecStdDeserializer extends StdDeserializer<NodeSpec> {
    public static final NodeSpecStdDeserializer INSTANCE = new NodeSpecStdDeserializer();

    private static final long serialVersionUID = 1L;

    public NodeSpecStdDeserializer() {
        this(null);
    }

    public NodeSpecStdDeserializer(Class<?> vc) {
        super(vc);
    }

    public NodeSpec deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final JsonNode node = (JsonNode) jsonParser.getCodec().readTree(jsonParser);
        final NodeSpec.PatternNodeType type = NodeSpec.PatternNodeType.valueOf(node.get("type").asText());
        final String name = node.get("name").asText();

        final QuantifierSpec quantifierSpec = (QuantifierSpec) jsonParser.getCodec()
                .treeToValue((TreeNode) node.get("quantifier"), QuantifierSpec.class);

        final ConditionSpec conditionSpec = (ConditionSpec) jsonParser.getCodec()
                .treeToValue((TreeNode) node.get("condition"), ConditionSpec.class);
        if (type.equals(NodeSpec.PatternNodeType.COMPOSITE)) {
            // nodes
            final List<NodeSpec> nodeSpecs = new ArrayList<>();
            final JsonNode nodes = node.get("nodes");
            if (nonNull(nodes)) {
                final Iterator<JsonNode> embeddedElementNames = nodes.elements();
                while (embeddedElementNames.hasNext()) {
                    JsonNode jsonNode = embeddedElementNames.next();
                    NodeSpec embedNode = (NodeSpec) jsonParser.getCodec().treeToValue((TreeNode) jsonNode, NodeSpec.class);
                    nodeSpecs.add(embedNode);
                }
            }
            // edges
            final List<EdgeSpec> edgeSpecs = new ArrayList<>();
            final JsonNode edges = node.get("edges");
            if (nonNull(edges)) {
                Iterator<JsonNode> jsonNodeIterator = edges.elements();
                while (jsonNodeIterator.hasNext()) {
                    JsonNode jsonNode = jsonNodeIterator.next();
                    EdgeSpec embedNode = (EdgeSpec) jsonParser.getCodec().treeToValue((TreeNode) jsonNode, EdgeSpec.class);
                    edgeSpecs.add(embedNode);
                }
            }
            // window
            final WindowSpec window = (WindowSpec) jsonParser.getCodec()
                    .treeToValue((TreeNode) node.get("window"), WindowSpec.class);
            // after match strategy
            final AfterMatchSkipStrategySpec afterMatchStrategy = (AfterMatchSkipStrategySpec) jsonParser.getCodec()
                    .treeToValue((TreeNode) node.get("afterMatchStrategy"), AfterMatchSkipStrategySpec.class);

            return (NodeSpec) new GraphSpec(name, quantifierSpec, conditionSpec, nodeSpecs, edgeSpecs, window,
                    afterMatchStrategy);
        }

        return new NodeSpec(name, quantifierSpec, conditionSpec);
    }
}
