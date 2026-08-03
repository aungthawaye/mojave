package org.mojave.core.settlement.contract.exception;

import org.mojave.component.misc.error.MojaveErrorResponse;
import org.mojave.component.misc.handy.ExceptionResolver;

public final class SettlementExceptionResolver {

    private static final String EXCEPTION_PACKAGE = "org.mojave.core.settlement.contract.exception";

    private SettlementExceptionResolver() {

    }

    public static Throwable resolve(final MojaveErrorResponse error) {

        return ExceptionResolver.resolve(EXCEPTION_PACKAGE, error);
    }

}
