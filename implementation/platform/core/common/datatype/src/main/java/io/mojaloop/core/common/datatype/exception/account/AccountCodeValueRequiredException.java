package io.mojaloop.core.common.datatype.exception.account;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class AccountCodeValueRequiredException extends InputException {

    private static final String TEMPLATE = "Value is required to create Account Code.";

    public AccountCodeValueRequiredException() {

        super(new ErrorTemplate("ACCOUNT_CODE_VALUE_REQUIRED", TEMPLATE));
    }

}
