package org.mojave.core.transfer.contract.command.step.stateful;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.fspiop.common.exception.FspiopException;

public interface ReserveTransferStep {

    void execute(Input input) throws FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 TransferId transferId,
                 PositionUpdateId positionReservationId) { }

}
