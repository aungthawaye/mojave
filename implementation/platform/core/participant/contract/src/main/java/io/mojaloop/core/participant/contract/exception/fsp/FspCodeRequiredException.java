package io.mojaloop.core.participant.contract.exception.fsp;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class FspCodeRequiredException extends InputException {

    private static final String TEMPLATE = "FSP Code is required.";

    public FspCodeRequiredException() {

        super(new ErrorTemplate("FSP_CODE_REQUIRED", TEMPLATE));
    }

}
