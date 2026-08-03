package org.mojave.core.settlement.contract.exception.record;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.rule.identifier.settlement.SettlementRecordId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementRecordNotFoundException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_RECORD_NOT_FOUND";

    private static final String TEMPLATE = "Settlement Record ({0}) is not found.";

    private final SettlementRecordId settlementRecordId;

    public SettlementRecordNotFoundException(final SettlementRecordId settlementRecordId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{String.valueOf(settlementRecordId.getId())}));

        this.settlementRecordId = settlementRecordId;
    }

    public static SettlementRecordNotFoundException from(final Map<String, String> extras) {

        final var settlementRecordId = new SettlementRecordId(Long.parseLong(extras.get(Keys.SETTLEMENT_RECORD_ID)));

        return new SettlementRecordNotFoundException(settlementRecordId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.SETTLEMENT_RECORD_ID, String.valueOf(this.settlementRecordId.getId()));

        return extras;
    }

    public static class Keys {

        public static final String SETTLEMENT_RECORD_ID = "settlementRecordId";

    }

}
