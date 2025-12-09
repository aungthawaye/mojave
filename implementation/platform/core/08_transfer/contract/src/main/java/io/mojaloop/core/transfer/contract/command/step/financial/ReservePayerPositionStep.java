package io.mojaloop.core.transfer.contract.command.step.financial;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public interface ReservePayerPositionStep {

    Output execute(Input input) throws
                                FspiopException,
                                NoPositionUpdateForTransactionException,
                                PositionLimitExceededException;

    record Input(String context,
                 TransactionId transactionId,
                 Instant transactionAt,
                 FspData payerFsp,
                 FspData payeeFsp,
                 Currency currency,
                 BigDecimal transferAmount) { }

    record Output(PositionUpdateId positionReservationId) { }

}
