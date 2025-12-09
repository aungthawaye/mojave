package io.mojaloop.core.transfer.contract.command.step.financial;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.common.exception.FspiopException;

public interface RollbackReservationStep {

    Output execute(Input input) throws FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 PositionUpdateId positionReservationId,
                 String error) { }

    record Output(PositionUpdateId rollbackId) { }
}
