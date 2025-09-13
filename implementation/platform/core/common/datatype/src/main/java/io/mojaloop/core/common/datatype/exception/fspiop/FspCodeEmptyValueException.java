package io.mojaloop.core.common.datatype.exception.fspiop;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class FspCodeEmptyValueException extends InputException {

    private static final String TEMPLATE = "Value is required to create FSP code.";

    public FspCodeEmptyValueException() {

        super(new ErrorTemplate("FSP_CODE_EMPTY_VALUE", TEMPLATE));
    }

}
