package org.apache.flink.cep.dynamic.processor;

import java.io.Serializable;

public interface PatternProcessorDiscovererFactory<T> extends Serializable {
    PatternProcessorDiscoverer<T> createPatternProcessorDiscoverer(ClassLoader paramClassLoader) throws Exception;
}
