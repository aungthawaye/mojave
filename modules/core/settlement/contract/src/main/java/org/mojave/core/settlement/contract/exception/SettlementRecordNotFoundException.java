package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.identifier.settlement.SettlementRecordId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementRecordNotFoundException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_RECORD_NOT_FOUND";

    private static final String TEMPLATE = "Settlement record ({0}) not found.";

    private final SettlementRecordId settlementRecordId;

    public SettlementRecordNotFoundException(final SettlementRecordId settlementRecordId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            settlementRecordId == null ? null : settlementRecordId.getId().toString()}));

        this.settlementRecordId = settlementRecordId;
    }

    public static SettlementRecordNotFoundException from(final Map<String, String> extras) {

        final var raw = extras.get(Keys.SETTLEMENT_RECORD_ID);
        final var id = raw == null ? null : new SettlementRecordId(Long.parseLong(raw));
        return new SettlementRecordNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(
            Keys.SETTLEMENT_RECORD_ID,
            this.settlementRecordId == null ? null : this.settlementRecordId.getId().toString());
        return extras;
    }

    public static class Keys {

        public static final String SETTLEMENT_RECORD_ID = "settlementRecordId";

    }

}
