package org.mojave.core.settlement.contract.exception.definition;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.identifier.settlement.SettlementDefinitionId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementModelDefinitionNotFoundException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_MODEL_DEFINITION_NOT_FOUND";

    private static final String TEMPLATE = "Settlement Model Definition ({0}) is not found.";

    private final SettlementDefinitionId settlementModelDefinitionId;

    public SettlementModelDefinitionNotFoundException(final SettlementDefinitionId settlementModelDefinitionId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{String.valueOf(settlementModelDefinitionId.getId())}));

        this.settlementModelDefinitionId = settlementModelDefinitionId;
    }

    public static SettlementModelDefinitionNotFoundException from(final Map<String, String> extras) {

        final var id = new SettlementDefinitionId(Long.parseLong(extras.get(Keys.SETTLEMENT_MODEL_DEFINITION_ID)));

        return new SettlementModelDefinitionNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(
            Keys.SETTLEMENT_MODEL_DEFINITION_ID,
            String.valueOf(this.settlementModelDefinitionId.getId()));

        return extras;
    }

    public static class Keys {

        public static final String SETTLEMENT_MODEL_DEFINITION_ID = "settlementModelDefinitionId";

    }

}
