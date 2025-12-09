package io.mojaloop.core.transfer.contract.command.step.stateful;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.common.exception.FspiopException;

public interface ReserveTransferStep {

    void execute(Input input) throws FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 TransferId transferId,
                 PositionUpdateId positionReservationId) { }

}
