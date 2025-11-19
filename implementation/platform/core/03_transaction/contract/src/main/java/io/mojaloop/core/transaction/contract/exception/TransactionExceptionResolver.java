package io.mojaloop.core.transaction.contract.exception;

import io.mojaloop.component.misc.error.RestErrorResponse;

public class TransactionExceptionResolver {

    public static Throwable resolve(final RestErrorResponse error) {

        final var code = error.code();
        final var extras = error.extras();

        return switch (code) {
            case TransactionIdNotFoundException.CODE -> TransactionIdNotFoundException.from(extras);

            default -> throw new RuntimeException("Unknown exception code: " + code);
        };
    }
}
