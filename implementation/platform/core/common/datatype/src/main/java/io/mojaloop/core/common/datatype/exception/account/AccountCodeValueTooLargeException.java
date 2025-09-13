package io.mojaloop.core.common.datatype.exception.account;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class AccountCodeValueTooLargeException extends InputException {

    private static final String TEMPLATE = "Account Code is too large. Must not exceed " + StringSizeConstraints.MAX_CODE_LENGTH + " characters.";

    public AccountCodeValueTooLargeException() {

        super(new ErrorTemplate("ACCOUNT_CODE_VALUE_TOO_LARGE", TEMPLATE));
    }

}
