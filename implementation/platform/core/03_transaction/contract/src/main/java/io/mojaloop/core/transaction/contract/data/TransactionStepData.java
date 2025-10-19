package io.mojaloop.core.transaction.contract.data;

import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionStepId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record TransactionStepData(TransactionStepId stepId,
                                  String name,
                                  StepPhase phase,
                                  Instant createdAt,
                                  TransactionId transactionId,
                                  List<StepParamData> params) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof TransactionStepData that)) {
            return false;
        }
        return Objects.equals(stepId, that.stepId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(stepId);
    }
}
