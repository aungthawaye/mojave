package io.mojaloop.core.transaction.contract.command;

import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;

public interface OpenTransactionCommand {

    Output execute(Input input);

    record Input(TransactionType type) { }

    record Output(TransactionId transactionId) { }

}
