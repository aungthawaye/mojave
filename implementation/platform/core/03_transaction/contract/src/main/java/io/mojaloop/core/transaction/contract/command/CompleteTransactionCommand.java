package io.mojaloop.core.transaction.contract.command;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;

public interface CompleteTransactionCommand {

    Output execute(Input input);

    record Input(TransactionId transactionId) { }

    record Output() { }

}
