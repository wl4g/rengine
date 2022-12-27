package org.apache.flink.cep.dynamic.coordinator;

import static org.apache.flink.util.Preconditions.checkNotNull;

import org.apache.flink.cep.dynamic.processor.PatternProcessorDiscovererFactory;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.operators.coordination.OperatorCoordinator;
import org.apache.flink.runtime.operators.coordination.RecreateOnResetOperatorCoordinator;

import lombok.Getter;

@Getter
public class DynamicCepOperatorCoordinatorProvider extends RecreateOnResetOperatorCoordinator.Provider {
    private static final long serialVersionUID = 1L;

    private final String operatorName;
    private final PatternProcessorDiscovererFactory<?> discovererFactory;

    public DynamicCepOperatorCoordinatorProvider(String operatorName, OperatorID operatorID,
            PatternProcessorDiscovererFactory<?> discovererFactory) {
        super(operatorID);
        this.operatorName = checkNotNull(operatorName);
        this.discovererFactory = checkNotNull(discovererFactory);
    }

    @Override
    protected OperatorCoordinator getCoordinator(OperatorCoordinator.Context context) {
        // TODO
        // use discovererFactory
        DynamicCepOperatorCoordinator testingCoordinator = new DynamicCepOperatorCoordinator(context);
        return testingCoordinator;
    }

}