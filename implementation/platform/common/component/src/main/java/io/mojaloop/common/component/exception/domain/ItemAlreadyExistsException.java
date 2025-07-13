package io.mojaloop.common.component.exception.domain;

import io.mojaloop.common.component.exception.DomainException;
import io.mojaloop.common.component.exception.ErrorTemplate;

import java.text.MessageFormat;

public class ItemAlreadyExistsException extends DomainException {

    private final static String TEMPLATE = "{0} ({1}) already exists.";

    private final String itemName;

    private final String itemValue;

    public ItemAlreadyExistsException(String itemName, String itemValue) {

        super(MessageFormat.format(TEMPLATE, itemName, itemValue));

        this.itemName = itemName;
        this.itemValue = itemValue;
    }

    @Override
    public String[] getFillers() {

        return new String[]{itemName, itemValue};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("ITEM_ALREADY_EXISTS", TEMPLATE);
    }

}
