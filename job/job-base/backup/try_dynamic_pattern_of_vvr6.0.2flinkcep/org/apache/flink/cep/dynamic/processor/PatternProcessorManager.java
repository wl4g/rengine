package org.apache.flink.cep.dynamic.processor;

import java.util.List;

public interface PatternProcessorManager<T> {
    void onPatternProcessorsUpdated(List<PatternProcessor<T>> paramList);
}
