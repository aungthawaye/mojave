package io.mojaloop.core.participant.contract.exception.oracle;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class OracleBaseUrlTooLongException extends InputException {

    private static final String TEMPLATE =
        "Base URL of Oracle is too long. Must not exceed " + StringSizeConstraints.MAX_HTTP_URL_LENGTH + " characters.";

    public OracleBaseUrlTooLongException() {

        super(new ErrorTemplate("ORACLE_BASE_URL_TOO_LONG", TEMPLATE));
    }

}
