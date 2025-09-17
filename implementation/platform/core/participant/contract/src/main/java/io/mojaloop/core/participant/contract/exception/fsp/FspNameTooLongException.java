package io.mojaloop.core.participant.contract.exception.fsp;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class FspNameTooLongException extends InputException {

    private static final String TEMPLATE = "FSP Name is too long. Must not exceed " + StringSizeConstraints.MAX_NAME_TITLE_LENGTH + " characters.";

    public FspNameTooLongException() {

        super(new ErrorTemplate("FSP_NAME_TOO_LONG", TEMPLATE));
    }

}
