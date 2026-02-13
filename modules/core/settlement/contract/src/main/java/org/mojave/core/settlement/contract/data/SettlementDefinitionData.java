package org.mojave.core.settlement.contract.data;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;

import java.time.Instant;
import java.util.Objects;

public record SettlementDefinitionData(SettlementDefinitionId settlementDefinitionId,
                                       String name,
                                       FspGroupId payerFspGroupId,
                                       FspGroupId payeeFspGroupId,
                                       Currency currency,
                                       Instant startAt,
                                       SspId desiredProviderId,
                                       ActivationStatus activationStatus) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof SettlementDefinitionData that)) {
            return false;
        }

        return Objects.equals(settlementDefinitionId, that.settlementDefinitionId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(settlementDefinitionId);
    }

}
