package io.mojaloop.core.account.contract.exception.account;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class AccountCodeRequiredException extends InputException {

    private static final String TEMPLATE = "Account Code is required.";

    public AccountCodeRequiredException() {

        super(new ErrorTemplate("ACCOUNT_CODE_REQUIRED", TEMPLATE));
    }

}
