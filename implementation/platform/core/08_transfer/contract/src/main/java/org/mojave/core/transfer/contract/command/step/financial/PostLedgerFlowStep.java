package org.mojave.core.transfer.contract.command.step.financial;

import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.fspiop.common.exception.FspiopException;
import org.mojave.fspiop.spec.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public interface PostLedgerFlowStep {

    void execute(Input input) throws FspiopException;

    record Input(String context,
                 TransactionId transactionId,
                 Instant transactionAt,
                 Currency currency,
                 FspData payerFsp,
                 FspData payeeFsp,
                 BigDecimal transferAmount,
                 BigDecimal payeeFspFee,
                 BigDecimal payeeFspCommission) { }

}
