
package org.apache.flink.cep.dynamic.impl.json.deserializer;

import java.io.IOException;

import org.apache.flink.cep.pattern.Quantifier;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class QuantifierTimesDeserializer extends StdDeserializer<Quantifier.Times> {
    public static final QuantifierTimesDeserializer INSTANCE = new QuantifierTimesDeserializer();

    private static final long serialVersionUID = 1L;

    public QuantifierTimesDeserializer() {
        this(null);
    }

    public QuantifierTimesDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Quantifier.Times deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = (JsonNode) jsonParser.getCodec().readTree(jsonParser);
        return Quantifier.Times.of(node.get("from").asInt(), node.get("to").asInt(),
                TimeStdDeserializer.doDeserialize(node.get("windowTime"), deserializationContext));
    }

}
