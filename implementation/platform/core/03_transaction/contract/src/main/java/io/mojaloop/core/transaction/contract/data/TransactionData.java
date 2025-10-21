package io.mojaloop.core.transaction.contract.data;

import io.mojaloop.core.common.datatype.enums.trasaction.TransactionPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record TransactionData(TransactionId transactionId,
                              TransactionType type,
                              TransactionPhase phase,
                              Instant openAt,
                              Instant closeAt,
                              String error,
                              Boolean success,
                              List<TransactionStepData> steps) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof TransactionData that)) {
            return false;
        }
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(transactionId);
    }
}
