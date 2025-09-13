package io.mojaloop.core.participant.contract.exception.oracle;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;

public class OracleBaseUrlRequiredException extends InputException {

    private static final String TEMPLATE = "Base URL of Oracle is required.";

    public OracleBaseUrlRequiredException() {

        super(new ErrorTemplate("ORACLE_BASE_URL_REQUIRED", TEMPLATE));
    }

}
