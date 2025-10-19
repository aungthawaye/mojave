package io.mojaloop.core.transaction.contract.command;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;

public interface FailTransactionCommand {

    Output execute(Input input);

    record Input(TransactionId transactionId, String error) { }

    record Output() { }
}
