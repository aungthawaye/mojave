package io.mojaloop.core.participant.contract.exception;

import io.mojaloop.common.component.exception.DomainException;
import io.mojaloop.common.component.exception.ErrorTemplate;

import java.text.MessageFormat;

public class CurrencyAlreadySupportedException extends DomainException {

    private static final String TEMPLATE = "Participant already has a supported currency with currency, {0}.";

    private final String currency;

    public CurrencyAlreadySupportedException(String currency) {

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

        return new ErrorTemplate("CURRENCY_ALREADY_SUPPORTED", TEMPLATE);
    }

}
