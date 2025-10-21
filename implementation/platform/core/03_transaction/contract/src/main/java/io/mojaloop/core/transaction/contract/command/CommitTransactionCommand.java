package io.mojaloop.core.transaction.contract.command;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;

public interface CommitTransactionCommand {

    Output execute(Input input);

    record Input(TransactionId transactionId, String error) { }

    record Output(TransactionId transactionId) { }

}
