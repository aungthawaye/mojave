package io.mojaloop.core.participant.contract.exception.fsp;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class FspEndpointBaseUrlTooLongException extends InputException {

    private static final String TEMPLATE =
        "Base URL of FSP Endpoint is too long. Must not exceed " + StringSizeConstraints.MAX_HTTP_URL_LENGTH + " characters.";

    public FspEndpointBaseUrlTooLongException() {

        super(new ErrorTemplate("FSP_ENDPOINT_BASE_URL_TOO_LONG", TEMPLATE));
    }

}
