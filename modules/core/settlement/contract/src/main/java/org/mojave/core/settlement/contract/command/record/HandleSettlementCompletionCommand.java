package org.mojave.core.settlement.contract.command.record;

import org.mojave.common.datatype.identifier.transaction.TransactionId;

import java.time.Instant;

public interface HandleSettlementCompletionCommand {

    Output execute(Input input);

    record Input(TransactionId transactionId, Instant completedAt) { }

    record Output(TransactionId transactionId) { }

}
