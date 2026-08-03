package org.mojave.core.settlement.contract.exception.model;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementModelNotFoundException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_MODEL_NOT_FOUND";

    private static final String TEMPLATE = "Settlement Model ({0}) is not found.";

    private final SettlementModelId settlementModelId;

    public SettlementModelNotFoundException(final SettlementModelId settlementModelId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{String.valueOf(settlementModelId.getId())}));

        this.settlementModelId = settlementModelId;
    }

    public static SettlementModelNotFoundException from(final Map<String, String> extras) {

        final var settlementModelId = new SettlementModelId(Long.parseLong(extras.get(Keys.SETTLEMENT_MODEL_ID)));

        return new SettlementModelNotFoundException(settlementModelId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.SETTLEMENT_MODEL_ID, String.valueOf(this.settlementModelId.getId()));

        return extras;
    }

    public static class Keys {

        public static final String SETTLEMENT_MODEL_ID = "settlementModelId";

    }

}
