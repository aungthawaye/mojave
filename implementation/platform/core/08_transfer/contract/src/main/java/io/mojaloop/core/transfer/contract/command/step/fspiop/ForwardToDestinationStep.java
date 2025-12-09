package io.mojaloop.core.transfer.contract.command.step.fspiop;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;

public interface ForwardToDestinationStep {

    void execute(Input input) throws FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 String destinationFspCode,
                 String baseUrl,
                 FspiopHttpRequest request) { }
}
