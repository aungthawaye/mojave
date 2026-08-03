package org.mojave.core.settlement.contract.exception.definition;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementModelDefinitionNameTakenException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_MODEL_DEFINITION_NAME_TAKEN";

    private static final String TEMPLATE = "Settlement Model Definition name ({0}) is already taken.";

    private final String name;

    public SettlementModelDefinitionNameTakenException(final String name) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{name}));

        this.name = name;
    }

    public static SettlementModelDefinitionNameTakenException from(final Map<String, String> extras) {

        return new SettlementModelDefinitionNameTakenException(extras.get(Keys.NAME));
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
