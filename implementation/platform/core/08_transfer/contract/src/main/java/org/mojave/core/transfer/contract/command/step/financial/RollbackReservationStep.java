package org.mojave.core.transfer.contract.command.step.financial;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.fspiop.common.exception.FspiopException;

public interface RollbackReservationStep {

    Output execute(Input input) throws FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 PositionUpdateId positionReservationId,
                 String error) { }

    record Output(PositionUpdateId rollbackId) { }

}
