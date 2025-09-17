package io.mojaloop.core.common.datatype.enums.account;

import lombok.Getter;

@Getter
public enum AccountType {

    ASSET(Side.DEBIT),
    LIABILITY(Side.CREDIT),
    EQUITY(Side.CREDIT),
    REVENUE(Side.CREDIT),
    EXPENSE(Side.DEBIT);

    private final Side side;

    AccountType(Side side) {

        this.side = side;
    }
}