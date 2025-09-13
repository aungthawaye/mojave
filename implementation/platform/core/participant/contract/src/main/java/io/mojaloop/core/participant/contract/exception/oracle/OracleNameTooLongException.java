package io.mojaloop.core.participant.contract.exception.oracle;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class OracleNameTooLongException extends InputException {

    private static final String TEMPLATE = "Oracle Name is too long. Must not exceed " + StringSizeConstraints.MAX_NAME_TITLE_LENGTH + " characters.";

    public OracleNameTooLongException() {

        super(new ErrorTemplate("ORACLE_NAME_TOO_LONG", TEMPLATE));
    }

}
