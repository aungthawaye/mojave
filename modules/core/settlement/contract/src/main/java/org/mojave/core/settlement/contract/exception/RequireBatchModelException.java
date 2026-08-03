package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.Map;

@Getter
public class RequireBatchModelException extends UncheckedDomainException {

    public static final String CODE = "REQUIRE_BATCH_MODEL";

    private static final String TEMPLATE = "Batch Settlement Model is required to create Settlement Window.";

    public RequireBatchModelException() {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[0]));
    }

    public static RequireBatchModelException from(final Map<String, String> extras) {

        return new RequireBatchModelException();
    }

    @Override
    public Map<String, String> extras() {

        return Map.of();
    }

}
