package io.mojaloop.core.wallet.contract.command.position;

import io.mojaloop.core.common.datatype.enums.wallet.PositionAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public interface RollbackPositionCommand {

    Output execute(Input input);

    record Input(PositionUpdateId reservationId,
                 PositionUpdateId positionUpdateId,
                 String description) { }

    record Output(PositionUpdateId positionUpdateId,
                  PositionId positionId,
                  PositionAction action,
                  TransactionId transactionId,
                  Currency currency,
                  BigDecimal amount,
                  BigDecimal oldPosition,
                  BigDecimal newPosition,
                  BigDecimal oldReserved,
                  BigDecimal newReserved,
                  BigDecimal netDebitCap,
                  Instant transactionAt) { }
}
