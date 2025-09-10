package io.mojaloop.connector.adapter.fsp.payload;

import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.Party;

public class Quotes {

    public static class Post {

        public record Request(String quoteId, Party payer, Party payee, AmountType amountType, Money originalAmount, String expiration) { }

        public record Response(Money originalAmount,
                               Money payeeFspFee,
                               Money payeeFspCommission,
                               Money payeeReceiveAmount,
                               Money transferAmount,
                               String expiration) { }

    }

}
