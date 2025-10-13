package io.mojaloop.core.wallet.domain.model;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet {

    protected WalletId id;

    protected WalletOwnerId walletOwnerId;

    protected String currency;

    protected String name;

    protected BigDecimal balance;

    protected BigDecimal reserved;

    public BalanceHistory commit(WithdrawReservation withdrawReservation, BalanceUpdater balanceUpdater) {

        return null;
    }

    public BalanceHistory deposit(BigDecimal amount, BalanceUpdater balanceUpdater) {

        return null;
    }

    public WithdrawReservation reserve(BigDecimal amount, BalanceUpdater balanceUpdater) {

        return null;
    }

    public BalanceHistory rollback(WithdrawReservation withdrawReservation, BalanceUpdater balanceUpdater) {

        return null;
    }

    public BalanceHistory withdraw(BigDecimal amount, BalanceUpdater balanceUpdater) {

        return null;
    }

}
