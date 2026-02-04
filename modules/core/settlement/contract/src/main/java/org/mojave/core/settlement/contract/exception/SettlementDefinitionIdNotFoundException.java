package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.identifier.settlement.SettlementDefinitionId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementDefinitionIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_DEFINITION_ID_NOT_FOUND";

    private static final String TEMPLATE = "Settlement definition with ID ({0}) not found.";

    private final SettlementDefinitionId settlementDefinitionId;

    public SettlementDefinitionIdNotFoundException(final SettlementDefinitionId settlementDefinitionId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            settlementDefinitionId == null ? null : settlementDefinitionId.getId().toString()}));

        this.settlementDefinitionId = settlementDefinitionId;
    }

    public static SettlementDefinitionIdNotFoundException from(final Map<String, String> extras) {

        final var raw = extras.get(Keys.SETTLEMENT_DEFINITION_ID);
        final var id = raw == null ? null : new SettlementDefinitionId(Long.parseLong(raw));
        return new SettlementDefinitionIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(
            Keys.SETTLEMENT_DEFINITION_ID, this.settlementDefinitionId == null ? null :
                                               this.settlementDefinitionId.getId().toString());
        return extras;
    }

    public static class Keys {

        public static final String SETTLEMENT_DEFINITION_ID = "settlementDefinitionId";

    }

}
