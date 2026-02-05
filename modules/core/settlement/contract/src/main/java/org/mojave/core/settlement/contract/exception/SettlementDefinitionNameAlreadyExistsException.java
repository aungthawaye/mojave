package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SettlementDefinitionNameAlreadyExistsException extends UncheckedDomainException {

    public static final String CODE = "SETTLEMENT_DEFINITION_NAME_ALREADY_EXISTS";

    private static final String TEMPLATE = "Settlement definition name ({0}) already exists.";

    private final String name;

    public SettlementDefinitionNameAlreadyExistsException(final String name) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{name}));

        this.name = name;
    }

    public static SettlementDefinitionNameAlreadyExistsException from(final Map<String, String> extras) {

        final var name = extras.get(Keys.NAME);
        return new SettlementDefinitionNameAlreadyExistsException(name);
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
