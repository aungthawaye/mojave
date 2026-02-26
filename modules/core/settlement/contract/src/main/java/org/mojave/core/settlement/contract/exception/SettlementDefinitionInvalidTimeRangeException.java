package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementDefinitionInvalidTimeRangeException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_DEFINITION_INVALID_TIME_RANGE";

    private static final String TEMPLATE = "Settlement definition time range is invalid: startAt ({0}) must be before endAt ({1}).";

    private final Instant startAt;

    private final Instant endAt;

    public SettlementDefinitionInvalidTimeRangeException(final Instant startAt,
                                                         final Instant endAt) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            startAt == null ? null : String.valueOf(startAt.getEpochSecond()),
            endAt == null ? null : String.valueOf(endAt.getEpochSecond())}));

        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static SettlementDefinitionInvalidTimeRangeException from(final Map<String, String> extras) {

        final var startAtRaw = extras.get(Keys.START_AT);
        final var endAtRaw = extras.get(Keys.END_AT);

        final var startAt = startAtRaw == null ? null : Instant.ofEpochSecond(Long.parseLong(startAtRaw));
        final var endAt = endAtRaw == null ? null : Instant.ofEpochSecond(Long.parseLong(endAtRaw));

        return new SettlementDefinitionInvalidTimeRangeException(startAt, endAt);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(Keys.START_AT, this.startAt == null ? null : String.valueOf(this.startAt.getEpochSecond()));
        extras.put(Keys.END_AT, this.endAt == null ? null : String.valueOf(this.endAt.getEpochSecond()));
        return extras;
    }

    public static class Keys {

        public static final String START_AT = "startAt";

        public static final String END_AT = "endAt";

    }

}
