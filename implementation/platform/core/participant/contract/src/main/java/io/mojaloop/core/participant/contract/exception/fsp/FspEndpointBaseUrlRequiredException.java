package io.mojaloop.core.participant.contract.exception.fsp;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class FspEndpointBaseUrlRequiredException extends InputException {

    private static final String TEMPLATE = "Base URL of FSP Endpoint is required.";

    public FspEndpointBaseUrlRequiredException() {

        super(new ErrorTemplate("FSP_ENDPOINT_BASE_URL_REQUIRED", TEMPLATE));
    }

}
