package org.mojave.core.settlement.contract.exception;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.Map;

@Getter
public class FspAlreadyExistsInGroupException extends UncheckedDomainException {

    public static final String CODE = "FSP_ID_ALREADY_EXISTS_IN_GROUP";

    private static final String TEMPLATE = "FSP already exists in group.";

    public FspAlreadyExistsInGroupException() {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{}));

    }

    public static FspAlreadyExistsInGroupException from(final Map<String, String> extras) {

        return new FspAlreadyExistsInGroupException();
    }

    @Override
    public Map<String, String> extras() {

        return Map.of();
    }

}