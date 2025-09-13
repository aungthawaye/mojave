package io.mojaloop.core.common.datatype.exception.account;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class ChartEntryCodeValueTooLargeException extends InputException {

    private static final String TEMPLATE = "The value of Chart Entry code is too large. Must not exceed " + StringSizeConstraints.MAX_CODE_LENGTH + " characters.";

    public ChartEntryCodeValueTooLargeException() {

        super(new ErrorTemplate("CHART_ENTRY_CODE_VALUE_TOO_LARGE", TEMPLATE));
    }

}
