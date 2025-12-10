package org.mojave.core.transfer.contract.command.step.fspiop;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.service.component.FspiopHttpRequest;

public interface ForwardToDestinationStep {

    void execute(Input input) throws FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 String destinationFspCode,
                 String baseUrl,
                 FspiopHttpRequest request) { }

}
