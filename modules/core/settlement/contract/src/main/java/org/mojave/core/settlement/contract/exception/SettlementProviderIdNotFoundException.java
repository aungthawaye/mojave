package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementProviderIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_PROVIDER_ID_NOT_FOUND";

    private static final String TEMPLATE = "Settlement provider id ({0}) not found.";

    private final SspId sspId;

    public SettlementProviderIdNotFoundException(final SspId sspId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE,
            new String[]{sspId == null ? null : sspId.getId().toString()}));

        this.sspId = sspId;
    }

    public static SettlementProviderIdNotFoundException from(final Map<String, String> extras) {

        final var raw = extras.get(Keys.SSP_ID);
        final var id = raw == null ? null : new SspId(Long.parseLong(raw));
        return new SettlementProviderIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(Keys.SSP_ID, this.sspId == null ? null : this.sspId.getId().toString());
        return extras;
    }

    public static class Keys {

        public static final String SSP_ID = "sspId";

    }

}
