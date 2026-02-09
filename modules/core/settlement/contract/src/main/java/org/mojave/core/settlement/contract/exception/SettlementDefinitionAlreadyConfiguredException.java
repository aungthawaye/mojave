package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementDefinitionAlreadyConfiguredException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_DEFINITION_ALREADY_CONFIGURED";

    private static final String TEMPLATE = "Settlement definition already configured for currency ({0}), payer group ({1}), payee group ({2}).";

    private final Currency currency;

    private final FspGroupId payerFspGroupId;

    private final FspGroupId payeeFspGroupId;

    public SettlementDefinitionAlreadyConfiguredException(final Currency currency,
                                                          final FspGroupId payerFspGroupId,
                                                          final FspGroupId payeeFspGroupId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            currency == null ? null : currency.name(),
            payerFspGroupId == null ? null : payerFspGroupId.getId().toString(),
            payeeFspGroupId == null ? null : payeeFspGroupId.getId().toString()}));

        this.currency = currency;
        this.payerFspGroupId = payerFspGroupId;
        this.payeeFspGroupId = payeeFspGroupId;
    }

    public static SettlementDefinitionAlreadyConfiguredException from(final Map<String, String> extras) {

        final var currency = extras.get(Keys.CURRENCY);
        final var payer = extras.get(Keys.PAYER_FSP_GROUP_ID);
        final var payee = extras.get(Keys.PAYEE_FSP_GROUP_ID);

        return new SettlementDefinitionAlreadyConfiguredException(
            currency == null ? null : Currency.valueOf(currency),
            payer == null ? null : new FspGroupId(Long.parseLong(payer)),
            payee == null ? null : new FspGroupId(Long.parseLong(payee)));
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(Keys.CURRENCY, this.currency == null ? null : this.currency.name());
        extras.put(
            Keys.PAYER_FSP_GROUP_ID,
            this.payerFspGroupId == null ? null : this.payerFspGroupId.getId().toString());
        extras.put(
            Keys.PAYEE_FSP_GROUP_ID,
            this.payeeFspGroupId == null ? null : this.payeeFspGroupId.getId().toString());
        return extras;
    }

    public static class Keys {

        public static final String CURRENCY = "currency";

        public static final String PAYER_FSP_GROUP_ID = "payerFspGroupId";

        public static final String PAYEE_FSP_GROUP_ID = "payeeFspGroupId";

    }

}
