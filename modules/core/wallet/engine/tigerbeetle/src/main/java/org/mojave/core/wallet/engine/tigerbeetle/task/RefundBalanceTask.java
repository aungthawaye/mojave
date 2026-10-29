package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;

public final class RefundBalanceTask {

    private RefundBalanceTask() { }

    public static WalletEngine.BalanceHistory execute(final TigerBeetleWalletStore store,
                                                    final BalanceUpdateId updateId,
                                                    final BalanceUpdateId referenceId)
        throws WalletEngine.BalanceReversalFailedException {

        try {
            final var command = WalletTaskExecutor.resolution(store, WalletRecord.Action.REVERSE_WITHDRAW,
                updateId.getId(), referenceId.getId());
            return WalletTaskMapper.balance(WalletTaskExecutor.execute(store, command));
        } catch (final WalletTaskExecutor.Rejected e) {
            throw new WalletEngine.BalanceReversalFailedException(referenceId);
        }
    }
}
