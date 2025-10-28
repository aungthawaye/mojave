package io.mojaloop.core.wallet.contract.data;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record PositionData(PositionId positionId,
                           WalletOwnerId walletOwnerId,
                           Currency currency,
                           String name,
                           BigDecimal position,
                           BigDecimal reserved,
                           BigDecimal netDebitCap,
                           Instant createdAt) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof PositionData positionData)) {
            return false;
        }
        return Objects.equals(positionId, positionData.positionId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(positionId);
    }
}
