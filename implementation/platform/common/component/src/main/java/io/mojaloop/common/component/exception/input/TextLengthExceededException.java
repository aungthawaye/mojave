package io.mojaloop.common.component.exception.input;

import io.mojaloop.common.component.exception.ErrorTemplate;
import io.mojaloop.common.component.exception.InputException;

import java.text.MessageFormat;

public class TextLengthExceededException extends InputException {

    private static final String TEMPLATE = "The text length of {0} must not exceed {1} characters.";

    private final String itemName;

    private final int maxLength;

    public TextLengthExceededException(String itemName, int maxLength) {

        super(MessageFormat.format(TEMPLATE, itemName, maxLength));

        this.itemName = itemName;
        this.maxLength = maxLength;
    }

    @Override
    public String[] getFillers() {

        return new String[]{itemName, String.valueOf(maxLength)};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("TEXT_LENGTH_EXCEEDED", TEMPLATE);
    }

}
