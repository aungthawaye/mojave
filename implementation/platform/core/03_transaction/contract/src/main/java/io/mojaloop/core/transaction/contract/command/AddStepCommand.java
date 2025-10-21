package io.mojaloop.core.transaction.contract.command;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;

import java.util.Map;

public interface AddStepCommand {

    Output execute(Input input);

    record Input(TransactionId transactionId, String name, Map<String, String> params) { }

    record Output(TransactionId transactionId) { }

}
