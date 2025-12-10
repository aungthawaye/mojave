package org.mojave.core.transfer.contract.command.step.stateful;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public interface FetchTransferStep {

    Output execute(Input input) throws FspiopException;

    record Input(UdfTransferId udfTransferId) { }

    record Output(TransferId transferId,
                  org.mojave.core.common.datatype.enums.transfer.TransferStatus state,
                  PositionUpdateId reservationId,
                  Currency currency,
                  BigDecimal transferAmount,
                  BigDecimal payeeFspFee,
                  BigDecimal payeeFspCommission,
                  TransactionId transactionId,
                  Instant transactionAt) { }

}
