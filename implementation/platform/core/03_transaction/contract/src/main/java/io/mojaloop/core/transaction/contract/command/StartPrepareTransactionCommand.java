package io.mojaloop.core.transaction.contract.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public interface StartPrepareTransactionCommand {

    Output execute(Input input);

    record Input(@JsonProperty(required = true) @NotNull TransactionType transactionType, @JsonProperty(required = true) @NotNull Map<String, String> params) { }

    record Output(TransactionId transactionId) { }

}
