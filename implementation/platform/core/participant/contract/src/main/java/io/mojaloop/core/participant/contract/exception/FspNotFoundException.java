package io.mojaloop.core.participant.contract.exception;

import io.mojaloop.common.component.exception.domain.ItemNotFoundException;

public class FspNotFoundException extends ItemNotFoundException {

    public FspNotFoundException(String itemName, String itemValue) {

        super(itemName, itemValue);
    }

}
