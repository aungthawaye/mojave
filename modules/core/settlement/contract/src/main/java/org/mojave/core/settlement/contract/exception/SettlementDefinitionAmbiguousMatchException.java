package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementDefinitionAmbiguousMatchException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_DEFINITION_AMBIGUOUS_MATCH";

    private static final String TEMPLATE = "Multiple settlement definitions matched for currency ({0}), payer group ({1}), payee group ({2}), transactionAt ({3}).";

    private final Currency currency;

    private final FspGroupId payerFspGroupId;

    private final FspGroupId payeeFspGroupId;

    private final Instant transactionAt;

    public SettlementDefinitionAmbiguousMatchException(final Currency currency,
                                                       final FspGroupId payerFspGroupId,
                                                       final FspGroupId payeeFspGroupId,
                                                       final Instant transactionAt) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            currency == null ? null : currency.name(),
            payerFspGroupId == null ? null : payerFspGroupId.getId().toString(),
            payeeFspGroupId == null ? null : payeeFspGroupId.getId().toString(),
            transactionAt == null ? null : String.valueOf(transactionAt.getEpochSecond())}));

        this.currency = currency;
        this.payerFspGroupId = payerFspGroupId;
        this.payeeFspGroupId = payeeFspGroupId;
        this.transactionAt = transactionAt;
    }

    public static SettlementDefinitionAmbiguousMatchException from(final Map<String, String> extras) {

        final var currency = extras.get(Keys.CURRENCY);
        final var payer = extras.get(Keys.PAYER_FSP_GROUP_ID);
        final var payee = extras.get(Keys.PAYEE_FSP_GROUP_ID);
        final var transactionAtRaw = extras.get(Keys.TRANSACTION_AT);

        return new SettlementDefinitionAmbiguousMatchException(
            currency == null ? null : Currency.valueOf(currency),
            payer == null ? null : new FspGroupId(Long.parseLong(payer)),
            payee == null ? null : new FspGroupId(Long.parseLong(payee)),
            transactionAtRaw == null ? null : Instant.ofEpochSecond(Long.parseLong(transactionAtRaw)));
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(Keys.CURRENCY, this.currency == null ? null : this.currency.name());
        extras.put(Keys.PAYER_FSP_GROUP_ID,
                   this.payerFspGroupId == null ? null : this.payerFspGroupId.getId().toString());
        extras.put(Keys.PAYEE_FSP_GROUP_ID,
                   this.payeeFspGroupId == null ? null : this.payeeFspGroupId.getId().toString());
        extras.put(Keys.TRANSACTION_AT,
                   this.transactionAt == null ? null : String.valueOf(this.transactionAt.getEpochSecond()));
        return extras;
    }

    public static class Keys {

        public static final String CURRENCY = "currency";

        public static final String PAYER_FSP_GROUP_ID = "payerFspGroupId";

        public static final String PAYEE_FSP_GROUP_ID = "payeeFspGroupId";

        public static final String TRANSACTION_AT = "transactionAt";

    }

}
