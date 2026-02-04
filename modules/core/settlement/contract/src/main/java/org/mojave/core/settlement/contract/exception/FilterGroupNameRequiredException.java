package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.Map;

@Getter
public class FilterGroupNameRequiredException extends UncheckedDomainException {

    public static final String CODE = "FILTER_GROUP_NAME_REQUIRED";

    private static final String TEMPLATE = "Filter group name is required.";

    public FilterGroupNameRequiredException() {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{}));
    }

    public static FilterGroupNameRequiredException from(final Map<String, String> extras) {

        return new FilterGroupNameRequiredException();
    }

    @Override
    public Map<String, String> extras() {

        return Map.of();
    }

}
