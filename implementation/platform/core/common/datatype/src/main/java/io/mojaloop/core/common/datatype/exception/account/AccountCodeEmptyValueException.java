package io.mojaloop.core.common.datatype.exception.account;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class AccountCodeEmptyValueException extends InputException {

    private static final String TEMPLATE = "Value is required to create Account code.";

    public AccountCodeEmptyValueException() {

        super(new ErrorTemplate("ACCOUNT_CODE_EMPTY_VALUE", TEMPLATE));
    }

}
