package io.mojaloop.core.participant.contract.exception.oracle;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class OracleNameRequiredException extends InputException {

    private static final String TEMPLATE = "Oracle Name is required.";

    public OracleNameRequiredException() {

        super(new ErrorTemplate("ORACLE_NAME_REQUIRED", TEMPLATE));
    }

}
