package io.mojaloop.core.common.datatype.enumeration.account;

import lombok.Getter;

@Getter
public enum AccountType {

    ASSET(NormalSide.DEBIT),
    LIABILITY(NormalSide.CREDIT),
    EQUITY(NormalSide.CREDIT),
    REVENUE(NormalSide.CREDIT),
    EXPENSE(NormalSide.DEBIT);

    private final NormalSide normalSide;

    AccountType(NormalSide normalSide) {

        this.normalSide = normalSide;
    }
}