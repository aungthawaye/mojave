package io.mojaloop.core.participant.contract.exception;

import io.mojaloop.common.component.exception.DomainException;
import io.mojaloop.common.component.exception.ErrorTemplate;

import java.text.MessageFormat;

public class CannotActivateSupportedCurrencyException extends DomainException {

    private static final String TEMPLATE = "Cannot activate the supported currency, {0}. FSP is not active.";

    private final String currency;

    public CannotActivateSupportedCurrencyException(String currency) {

        super(MessageFormat.format(TEMPLATE, currency));

        assert currency != null;

        this.currency = currency;
    }

    @Override
    public String[] getFillers() {

        return new String[]{currency};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("CANNOT_ACTIVATE_SUPPORTED_CURRENCY", TEMPLATE);
    }

}
