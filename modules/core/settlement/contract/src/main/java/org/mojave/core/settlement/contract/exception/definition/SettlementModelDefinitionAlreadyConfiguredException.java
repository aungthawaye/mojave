package org.mojave.core.settlement.contract.exception.definition;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.TransactionPeriod;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.scenario.ScenarioType;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementModelDefinitionAlreadyConfiguredException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_MODEL_DEFINITION_ALREADY_CONFIGURED";

    private static final String TEMPLATE =
        "Settlement Model Definition for scenario ({0}), currency ({1}), FSP group ({2}) and transaction period ({3}) is already configured.";

    private final ScenarioType scenario;

    private final Currency currency;

    private final FspGroupId fspGroupId;

    private final TransactionPeriod transactionPeriod;

    public SettlementModelDefinitionAlreadyConfiguredException(final ScenarioType scenario,
                                                              final Currency currency,
                                                              final FspGroupId fspGroupId,
                                                              final TransactionPeriod transactionPeriod) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            scenario.name(),
            currency.name(),
            String.valueOf(fspGroupId.getId()),
            transactionPeriod.name()}));

        this.scenario = scenario;
        this.currency = currency;
        this.fspGroupId = fspGroupId;
        this.transactionPeriod = transactionPeriod;
    }

    public static SettlementModelDefinitionAlreadyConfiguredException from(final Map<String, String> extras) {

        return new SettlementModelDefinitionAlreadyConfiguredException(
            ScenarioType.valueOf(extras.get(Keys.SCENARIO)),
            Currency.valueOf(extras.get(Keys.CURRENCY)),
            new FspGroupId(Long.parseLong(extras.get(Keys.FSP_GROUP_ID))),
            TransactionPeriod.valueOf(extras.get(Keys.TRANSACTION_PERIOD)));
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.SCENARIO, this.scenario.name());
        extras.put(Keys.CURRENCY, this.currency.name());
        extras.put(Keys.FSP_GROUP_ID, String.valueOf(this.fspGroupId.getId()));
        extras.put(Keys.TRANSACTION_PERIOD, this.transactionPeriod.name());

        return extras;
    }

    public static class Keys {

        public static final String SCENARIO = "scenario";

        public static final String CURRENCY = "currency";

        public static final String FSP_GROUP_ID = "fspGroupId";

        public static final String TRANSACTION_PERIOD = "transactionPeriod";

    }

}
