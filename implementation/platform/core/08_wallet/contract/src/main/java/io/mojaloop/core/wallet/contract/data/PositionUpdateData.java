package io.mojaloop.core.wallet.contract.data;

import io.mojaloop.core.common.datatype.enums.wallet.PositionAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record PositionUpdateData(PositionUpdateId positionUpdateId,
                                PositionId positionId,
                                PositionAction action,
                                TransactionId transactionId,
                                Currency currency,
                                BigDecimal amount,
                                BigDecimal oldPosition,
                                BigDecimal newPosition,
                                String description,
                                Instant transactionAt,
                                Instant createdAt,
                                PositionUpdateId reversedId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof PositionUpdateData positionUpdateData)) {
            return false;
        }
        return Objects.equals(positionUpdateId, positionUpdateData.positionUpdateId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(positionUpdateId);
    }
}
