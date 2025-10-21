package io.mojaloop.core.transaction.contract.data;

import io.mojaloop.core.common.datatype.identifier.transaction.StepParamId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionStepId;

import java.util.Objects;

public record StepParamData(StepParamId paramId,
                            String name,
                            String value,
                            TransactionStepId stepId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof StepParamData that)) {
            return false;
        }
        return Objects.equals(paramId, that.paramId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(paramId);
    }
}
