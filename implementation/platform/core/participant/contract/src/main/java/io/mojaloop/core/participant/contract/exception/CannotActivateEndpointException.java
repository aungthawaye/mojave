package io.mojaloop.core.participant.contract.exception;

import io.mojaloop.common.component.exception.DomainException;
import io.mojaloop.common.component.exception.ErrorTemplate;

import java.text.MessageFormat;

public class CannotActivateEndpointException extends DomainException {

    private static final String TEMPLATE = "Cannot activate the endpoint, {0}. FSP is not active.";

    private final String endpointType;

    public CannotActivateEndpointException(String endpointType) {

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

        return new ErrorTemplate("CANNOT_ACTIVE_ENDPOINT", TEMPLATE);
    }

}
