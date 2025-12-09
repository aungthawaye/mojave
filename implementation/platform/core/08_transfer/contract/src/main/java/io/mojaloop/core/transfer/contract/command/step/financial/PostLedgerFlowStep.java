package io.mojaloop.core.transfer.contract.command.step.financial;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.Currency;

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
