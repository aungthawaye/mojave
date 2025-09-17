package io.mojaloop.core.participant.contract.exception.fsp;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class FspNameRequiredException extends InputException {

    private static final String TEMPLATE = "FSP Name is required.";

    public FspNameRequiredException() {

        super(new ErrorTemplate("FSP_NAME_REQUIRED", TEMPLATE));
    }

}
