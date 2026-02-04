package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementDefinitionAlreadyConfiguredException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_DEFINITION_ALREADY_CONFIGURED";

    private static final String TEMPLATE = "Settlement definition already configured for currency ({0}), payer group ({1}), payee group ({2}).";

    private final Currency currency;

    private final FilterGroupId payerFilterGroupId;

    private final FilterGroupId payeeFilterGroupId;

    public SettlementDefinitionAlreadyConfiguredException(final Currency currency,
                                                          final FilterGroupId payerFilterGroupId,
                                                          final FilterGroupId payeeFilterGroupId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            currency == null ? null : currency.name(),
            payerFilterGroupId == null ? null : payerFilterGroupId.getId().toString(),
            payeeFilterGroupId == null ? null : payeeFilterGroupId.getId().toString()}));

        this.currency = currency;
        this.payerFilterGroupId = payerFilterGroupId;
        this.payeeFilterGroupId = payeeFilterGroupId;
    }

    public static SettlementDefinitionAlreadyConfiguredException from(final Map<String, String> extras) {

        final var currency = extras.get(Keys.CURRENCY);
        final var payer = extras.get(Keys.PAYER_FILTER_GROUP_ID);
        final var payee = extras.get(Keys.PAYEE_FILTER_GROUP_ID);

        return new SettlementDefinitionAlreadyConfiguredException(
            currency == null ? null : Currency.valueOf(currency),
            payer == null ? null : new FilterGroupId(Long.parseLong(payer)),
            payee == null ? null : new FilterGroupId(Long.parseLong(payee)));
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(Keys.CURRENCY, this.currency == null ? null : this.currency.name());
        extras.put(
            Keys.PAYER_FILTER_GROUP_ID,
            this.payerFilterGroupId == null ? null : this.payerFilterGroupId.getId().toString());
        extras.put(
            Keys.PAYEE_FILTER_GROUP_ID,
            this.payeeFilterGroupId == null ? null : this.payeeFilterGroupId.getId().toString());
        return extras;
    }

    public static class Keys {

        public static final String CURRENCY = "currency";

        public static final String PAYER_FILTER_GROUP_ID = "payerFilterGroupId";

        public static final String PAYEE_FILTER_GROUP_ID = "payeeFilterGroupId";

    }

}
