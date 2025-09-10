package io.mojaloop.fspiop.common.data;

import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;

public record Agreement(String quoteId,
                        PartyIdInfo payer,
                        PartyIdInfo payee,
                        AmountType amountType,
                        Money originalAmount,
                        Money payeeFspFee,
                        Money payeeFspCommission,
                        Money payeeReceiveAmount,
                        Money transferAmount,
                        String expiration) { }
