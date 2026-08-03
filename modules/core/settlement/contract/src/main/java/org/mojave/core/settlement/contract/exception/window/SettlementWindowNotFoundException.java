package org.mojave.core.settlement.contract.exception.window;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementWindowNotFoundException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_WINDOW_NOT_FOUND";

    private static final String TEMPLATE = "Settlement Window ({0}) is not found.";

    private final SettlementWindowId settlementWindowId;

    public SettlementWindowNotFoundException(final SettlementWindowId settlementWindowId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{String.valueOf(settlementWindowId.getId())}));

        this.settlementWindowId = settlementWindowId;
    }

    public static SettlementWindowNotFoundException from(final Map<String, String> extras) {

        final var id = new SettlementWindowId(Long.parseLong(extras.get(Keys.SETTLEMENT_WINDOW_ID)));

        return new SettlementWindowNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.SETTLEMENT_WINDOW_ID, String.valueOf(this.settlementWindowId.getId()));

        return extras;
    }

    public static class Keys {

        public static final String SETTLEMENT_WINDOW_ID = "settlementWindowId";

    }

}
