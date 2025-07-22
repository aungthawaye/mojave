package io.mojaloop.core.participant.contract.exception;

import io.mojaloop.common.component.exception.DomainException;
import io.mojaloop.common.component.exception.ErrorTemplate;

import java.text.MessageFormat;

public class EndpointAlreadyConfiguredException extends DomainException {

    private static final String TEMPLATE = "Endpoint type ({0}) is already configured.";

    private final String endpointType;

    public EndpointAlreadyConfiguredException(String endpointType) {

        super(MessageFormat.format(TEMPLATE, endpointType));

        assert endpointType != null;

        this.endpointType = endpointType;
    }

    @Override
    public String[] getFillers() {

        return new String[]{endpointType};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("ENDPOINT_ALREADY_CONFIGURED", TEMPLATE);
    }

}
