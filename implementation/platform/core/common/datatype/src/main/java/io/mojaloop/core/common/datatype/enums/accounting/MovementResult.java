package io.mojaloop.core.common.datatype.enums.accounting;

public enum MovementResult {
    PENDING,
    SUCCESS,
    INSUFFICIENT_BALANCE,
    OVERDRAFT_EXCEEDED,
    REVERSED
}
