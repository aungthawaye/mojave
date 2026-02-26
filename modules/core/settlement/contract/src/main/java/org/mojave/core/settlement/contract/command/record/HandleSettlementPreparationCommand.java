package org.mojave.core.settlement.contract.command.record;

import org.mojave.common.datatype.identifier.settlement.SettlementBatchId;
import org.mojave.common.datatype.identifier.settlement.SettlementId;
import org.mojave.common.datatype.identifier.transaction.TransactionId;

import java.time.Instant;

public interface HandleSettlementPreparationCommand {

    Output execute(Input input);

    record Input(TransactionId transactionId,
                 SettlementId settlementId,
                 SettlementBatchId settlementBatchId,
                 Instant preparedAt) { }

    record Output(TransactionId transactionId) { }

}
