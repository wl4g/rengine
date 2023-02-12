
package org.apache.flink.cep.dynamic.processor;

import java.io.Serializable;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.core.io.Versioned;

public interface PatternProcessor<IN> extends Serializable, Versioned {

    String getId();

    default Long getTimestamp() {
        return Long.valueOf(Long.MIN_VALUE);
    }

    Pattern<IN, ?> getPattern(ClassLoader paramClassLoader);

    PatternProcessFunction<IN, ?> getPatternProcessFunction();

}
