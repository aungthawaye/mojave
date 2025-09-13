package io.mojaloop.core.common.datatype.exception.account;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class ChartEntryCodeEmptyValueException extends InputException {

    private static final String TEMPLATE = "Value is required to create Chart Entry code.";

    public ChartEntryCodeEmptyValueException() {

        super(new ErrorTemplate("CHART_ENTRY_CODE_EMPTY_VALUE", TEMPLATE));
    }

}
