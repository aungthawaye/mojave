package org.mojave.core.settlement.contract.exception.model;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementModelNameTakenException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_MODEL_NAME_TAKEN";

    private static final String TEMPLATE = "Settlement Model name ({0}) is already taken.";

    private final String name;

    public SettlementModelNameTakenException(final String name) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{name}));

        this.name = name;
    }

    public static SettlementModelNameTakenException from(final Map<String, String> extras) {

        return new SettlementModelNameTakenException(extras.get(Keys.NAME));
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.NAME, this.name);

        return extras;
    }

    public static class Keys {

        public static final String NAME = "name";

    }

}
