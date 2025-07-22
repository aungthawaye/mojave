package io.mojaloop.core.participant.contract.exception;

import io.mojaloop.common.component.exception.domain.ItemAlreadyExistsException;

public class FspAlreadyExistsException extends ItemAlreadyExistsException {

    public FspAlreadyExistsException(String itemName, String itemValue) {

        super(itemName, itemValue);
    }

}
