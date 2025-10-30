package io.mojaloop.core.wallet.domain.component.empty;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;

import java.math.BigDecimal;
import java.time.Instant;

public class EmptyBalanceUpdater implements BalanceUpdater {

    @Override
    public BalanceHistory deposit(TransactionId transactionId, Instant transactionAt, BalanceUpdateId balanceUpdateId, WalletId walletId, BigDecimal amount, String description) {

        throw new UnsupportedOperationException("Balance update operations are not supported in the empty implementation");
    }

    @Override
    public BalanceHistory reverse(BalanceUpdateId reversedId, BalanceUpdateId balanceUpdateId) {

        throw new UnsupportedOperationException("Balance reversal operations are not supported in the empty implementation");
    }

    @Override
    public BalanceHistory withdraw(TransactionId transactionId, Instant transactionAt, BalanceUpdateId balanceUpdateId, WalletId walletId, BigDecimal amount, String description) {

        throw new UnsupportedOperationException("Balance withdrawal operations are not supported in the empty implementation");
    }

}
