
package org.apache.flink.cep.dynamic.impl.json.spec;

import org.apache.flink.cep.pattern.Quantifier;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;

public class EdgeSpec {
    private final String source;
    private final String target;
    private final Quantifier.ConsumingStrategy type;

    public EdgeSpec(@JsonProperty("source") String source, @JsonProperty("target") String target,
            @JsonProperty("type") Quantifier.ConsumingStrategy type) {
        this.source = source;
        this.target = target;
        this.type = type;
    }

    public String getSource() {
        return this.source;
    }

    public String getTarget() {
        return this.target;
    }

    public Quantifier.ConsumingStrategy getType() {
        return this.type;
    }
}
