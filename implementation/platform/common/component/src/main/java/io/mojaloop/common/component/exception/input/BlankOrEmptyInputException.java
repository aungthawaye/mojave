package io.mojaloop.common.component.exception.input;

import io.mojaloop.common.component.exception.ErrorTemplate;
import io.mojaloop.common.component.exception.InputException;

import java.text.MessageFormat;

public class BlankOrEmptyInputException extends InputException {

    private static final String TEMPLATE = "The ({0}) is required and must not be blank or empty.";

    private final String itemName;

    public BlankOrEmptyInputException(String itemName) {

        super(MessageFormat.format(TEMPLATE, itemName));

        this.itemName = itemName;
    }

    @Override
    public String[] getFillers() {

        return new String[]{itemName};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("BLANK_OR_EMPTY_INPUT", TEMPLATE);
    }

}
