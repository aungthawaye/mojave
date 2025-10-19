package io.mojaloop.core.common.datatype.enums.trasaction;

public final class FundTransferReserveDimension {

    public enum Participants {
        PAYER_FSP,
        PAYEE_FSP
    }

    public enum Amounts {
        TRANSFER_AMOUNT,
        PAYEE_FSP_FEE,
        PAYEE_FSP_COMMISSION
    }

}
