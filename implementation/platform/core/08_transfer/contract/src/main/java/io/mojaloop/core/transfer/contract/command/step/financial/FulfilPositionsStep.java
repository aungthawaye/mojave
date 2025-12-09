package io.mojaloop.core.transfer.contract.command.step.financial;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;

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
