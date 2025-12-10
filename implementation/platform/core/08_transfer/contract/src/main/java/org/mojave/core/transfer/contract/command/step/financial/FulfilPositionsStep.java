package org.mojave.core.transfer.contract.command.step.financial;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.Currency;

import java.time.Instant;

public interface FulfilPositionsStep {

    Output execute(Input input) throws FailedToCommitReservationException, FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 Instant transactionAt,
                 FspData payerFsp,
                 FspData payeeFsp,
                 PositionUpdateId positionReservationId,
                 Currency currency,
                 String description) { }

    record Output(PositionUpdateId payerCommitId, PositionUpdateId payeeCommitId) { }

}
