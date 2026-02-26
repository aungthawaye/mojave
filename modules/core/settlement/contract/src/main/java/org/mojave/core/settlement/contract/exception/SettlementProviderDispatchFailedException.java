package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementProviderDispatchFailedException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_PROVIDER_DISPATCH_FAILED";

    private static final String TEMPLATE =
        "Settlement provider dispatch failed for sspId ({0}) endpoint ({1}) reason ({2}).";

    private final SspId sspId;

    private final String endpoint;

    private final String reason;

    public SettlementProviderDispatchFailedException(final SspId sspId,
                                                     final String endpoint,
                                                     final String reason) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            sspId == null ? null : sspId.getId().toString(),
            endpoint,
            reason}));

        this.sspId = sspId;
        this.endpoint = endpoint;
        this.reason = reason;
    }

    public static SettlementProviderDispatchFailedException from(final Map<String, String> extras) {

        final var sspIdRaw = extras.get(Keys.SSP_ID);
        final var endpoint = extras.get(Keys.ENDPOINT);
        final var reason = extras.get(Keys.REASON);

        final var sspId = sspIdRaw == null ? null : new SspId(Long.parseLong(sspIdRaw));

        return new SettlementProviderDispatchFailedException(sspId, endpoint, reason);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(Keys.SSP_ID, this.sspId == null ? null : this.sspId.getId().toString());
        extras.put(Keys.ENDPOINT, this.endpoint);
        extras.put(Keys.REASON, this.reason);
        return extras;
    }

    public static class Keys {

        public static final String SSP_ID = "sspId";

        public static final String ENDPOINT = "endpoint";

        public static final String REASON = "reason";

    }

}
