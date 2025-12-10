package org.mojave.core.transfer.contract.command.step.financial;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.Currency;

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
