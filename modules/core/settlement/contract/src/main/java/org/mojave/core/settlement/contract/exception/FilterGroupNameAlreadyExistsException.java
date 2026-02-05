package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FilterGroupNameAlreadyExistsException extends UncheckedDomainException {

    public static final String CODE = "FILTER_GROUP_NAME_ALREADY_EXISTS";

    private static final String TEMPLATE = "Filter group name ({0}) already exists.";

    private final String name;

    public FilterGroupNameAlreadyExistsException(final String name) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{name}));

        this.name = name;
    }

    public static FilterGroupNameAlreadyExistsException from(final Map<String, String> extras) {

        final var name = extras.get(Keys.NAME);

        return new FilterGroupNameAlreadyExistsException(name);
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
