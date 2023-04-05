
package org.apache.flink.cep.dynamic.impl.json.deserializer;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonParser;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.flink.streaming.api.windowing.time.Time;

public class TimeStdDeserializer extends StdDeserializer<Time> {
    public static final TimeStdDeserializer INSTANCE = new TimeStdDeserializer();

    private static final long serialVersionUID = 1L;

    public TimeStdDeserializer() {
        this(null);
    }

    public TimeStdDeserializer(Class<?> vc) {
        super(vc);
    }

    public Time deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = (JsonNode) jsonParser.getCodec().readTree(jsonParser);
        return Time.of(node.get("size").asLong(), TimeUnit.valueOf(node.get("unit").asText()));
    }
}
