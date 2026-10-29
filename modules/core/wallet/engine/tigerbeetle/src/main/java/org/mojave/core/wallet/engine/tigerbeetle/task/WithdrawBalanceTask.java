package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

import java.math.BigDecimal;
import java.time.Instant;

public final class WithdrawBalanceTask {

    private WithdrawBalanceTask() { }

    public static WalletEngine.BalanceHistory execute(final TigerBeetleWalletStore store,
                                                    final BalanceUpdateId updateId,
                                                    final TransactionId transactionId,
                                                    final Instant transactionAt,
                                                    final WalletId walletId,
                                                    final BigDecimal amount,
                                                    final String description)
        throws WalletEngine.NoBalanceUpdateException, WalletEngine.InsufficientBalanceException {

        final var command = new WalletRecord.Command(WalletRecord.Action.WITHDRAW,
            updateId.getId(), walletId.getId(), transactionId.getId(), transactionAt, amount,
            0, description, 0, 0);

        try {
            return WalletTaskMapper.balance(WalletTaskExecutor.execute(store, command));
        } catch (final WalletTaskExecutor.Rejected e) {
            if (e.reason() == WalletTaskExecutor.Reason.INSUFFICIENT_BALANCE) {
                throw new WalletEngine.InsufficientBalanceException(transactionId, walletId, amount, e.state().balance());
            }
            throw new WalletEngine.NoBalanceUpdateException(transactionId);
        }
    }
}
