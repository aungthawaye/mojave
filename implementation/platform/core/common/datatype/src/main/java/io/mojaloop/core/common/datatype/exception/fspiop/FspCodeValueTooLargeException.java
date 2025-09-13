package io.mojaloop.core.common.datatype.exception.fspiop;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class FspCodeValueTooLargeException extends InputException {

    private static final String TEMPLATE = "The value of FSP code is too large. Must not exceed " + StringSizeConstraints.MAX_CODE_LENGTH + " characters.";

    public FspCodeValueTooLargeException() {

        super(new ErrorTemplate("FSP_CODE_VALUE_TOO_LARGE", TEMPLATE));
    }

}
