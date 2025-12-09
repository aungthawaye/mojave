package io.mojaloop.core.transfer.contract.command.step.stateful;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.transfer.TransferId;
import io.mojaloop.core.common.datatype.identifier.transfer.UdfTransferId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public interface FetchTransferStep {

    Output execute(Input input) throws FspiopException;

    record Input(UdfTransferId udfTransferId) { }

    record Output(TransferId transferId,
                  io.mojaloop.core.common.datatype.enums.transfer.TransferStatus state,
                  PositionUpdateId reservationId,
                  Currency currency,
                  BigDecimal transferAmount,
                  BigDecimal payeeFspFee,
                  BigDecimal payeeFspCommission,
                  TransactionId transactionId,
                  Instant transactionAt) { }

}
