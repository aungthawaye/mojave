package io.mojaloop.common.component.exception.domain;

import io.mojaloop.common.component.exception.DomainException;
import io.mojaloop.common.component.exception.ErrorTemplate;

import java.text.MessageFormat;

public class ItemNotFoundException extends DomainException {

    private static final String TEMPLATE = "{0} ({1}) cannot be not found.";

    private final String itemName;

    private final String itemValue;

    public ItemNotFoundException(String itemName, String itemValue) {

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

        return new ErrorTemplate("ITEM_NOT_FOUND", TEMPLATE);
    }

}
