package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementProviderEndpointNotFoundException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_PROVIDER_ENDPOINT_NOT_FOUND";

    private static final String TEMPLATE = "Settlement provider endpoint not found for sspId ({0}).";

    private final SspId sspId;

    public SettlementProviderEndpointNotFoundException(final SspId sspId) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            sspId == null ? null : sspId.getId().toString()}));

        this.sspId = sspId;
    }

    public static SettlementProviderEndpointNotFoundException from(final Map<String, String> extras) {

        final var raw = extras.get(Keys.SSP_ID);
        final var id = raw == null ? null : new SspId(Long.parseLong(raw));
        return new SettlementProviderEndpointNotFoundException(id);
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
