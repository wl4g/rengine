package org.apache.flink.cep.dynamic.processor;

import java.io.Closeable;

public interface PatternProcessorDiscoverer<T> extends Closeable {
    void discoverPatternProcessorUpdates(PatternProcessorManager<T> paramPatternProcessorManager);
}
