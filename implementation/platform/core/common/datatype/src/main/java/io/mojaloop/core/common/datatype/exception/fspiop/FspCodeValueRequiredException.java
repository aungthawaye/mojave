package io.mojaloop.core.common.datatype.exception.fspiop;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class FspCodeValueRequiredException extends InputException {

    private static final String TEMPLATE = "Value is required to create FSP Code.";

    public FspCodeValueRequiredException() {

        super(new ErrorTemplate("FSP_CODE_VALUE_REQUIRED", TEMPLATE));
    }

}
