package org.mojave.core.transaction.contract.engine;

import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.util.Set;

public interface TransactionEngine<P> {

    Definition getDefinition();

    ScenarioType getScenario();

    Operation prepare(P payload);

    interface Operation {

        void abort();

        void commit();

        void reserve();

    }

    record State(TransactionId transactionId, ScenarioType scenario) { }

    record Definition(String scenario, Set<String> participants, Set<String> amounts) { }

}
