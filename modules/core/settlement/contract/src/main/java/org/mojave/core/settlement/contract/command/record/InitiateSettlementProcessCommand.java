package org.mojave.core.settlement.contract.command.record;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.settlement.SettlementType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.transfer.TransferId;

import java.math.BigDecimal;
import java.time.Instant;

public interface InitiateSettlementProcessCommand {

    Output execute(Input input);

    record Input(SettlementType settlementType,
                 FspId payerFspId,
                 FspId payeeFspId,
                 Currency currency,
                 BigDecimal amount,
                 TransferId transferId,
                 TransactionId transactionId,
                 Instant transactionAt) { }

    record Output(SettlementRecordId settlementRecordId, SspId settlementProviderId) { }

}
