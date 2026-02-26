package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class InitiateSettlementProcessInvalidInputException extends UncheckedDomainException {

    public static final String CODE = "INITIATE_SETTLEMENT_PROCESS_INVALID_INPUT";

    private static final String TEMPLATE = "Initiate settlement process input is invalid: field ({0}) {1}.";

    private final String field;

    private final String reason;

    public InitiateSettlementProcessInvalidInputException(final String field,
                                                          final String reason) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{field, reason}));

        this.field = field;
        this.reason = reason;
    }

    public static InitiateSettlementProcessInvalidInputException from(final Map<String, String> extras) {

        final var field = extras.get(Keys.FIELD);
        final var reason = extras.get(Keys.REASON);
        return new InitiateSettlementProcessInvalidInputException(field, reason);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();
        extras.put(Keys.FIELD, this.field);
        extras.put(Keys.REASON, this.reason);
        return extras;
    }

    public static class Keys {

        public static final String FIELD = "field";

        public static final String REASON = "reason";

    }

}
