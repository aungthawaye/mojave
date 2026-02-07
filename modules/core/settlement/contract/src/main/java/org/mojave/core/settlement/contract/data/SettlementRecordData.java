package org.mojave.core.settlement.contract.data;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.settlement.SettlementType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementBatchId;
import org.mojave.common.datatype.identifier.settlement.SettlementId;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.transfer.TransferId;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public record SettlementRecordData(SettlementRecordId settlementRecordId,
                                   SettlementType type,
                                   SettlementId settlementId,
                                   SettlementBatchId settlementBatchId,
                                   FspId payerFspId,
                                   FspId payeeFspId,
                                   Currency currency,
                                   BigDecimal amount,
                                   TransferId transferId,
                                   TransactionId transactionId,
                                   Instant transactionAt,
                                   SspId settlementProviderId,
                                   Instant initiatedAt,
                                   Instant preparedAt,
                                   Instant completedAt) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof SettlementRecordData that)) {
            return false;
        }

        return Objects.equals(settlementRecordId, that.settlementRecordId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(settlementRecordId);
    }

}
