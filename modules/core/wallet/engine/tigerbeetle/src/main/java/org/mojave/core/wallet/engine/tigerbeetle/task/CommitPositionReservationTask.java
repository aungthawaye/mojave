package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;

public final class CommitPositionReservationTask {

    private CommitPositionReservationTask() { }

    public static WalletEngine.PositionHistory execute(final TigerBeetleWalletStore store,
                                                    final PositionUpdateId updateId,
                                                    final PositionUpdateId referenceId)
        throws WalletEngine.PositionReservationCommitFailedException {

        try {
            final var command = WalletTaskExecutor.resolution(store, WalletRecord.Action.COMMIT,
                updateId.getId(), referenceId.getId());
            return WalletTaskMapper.position(WalletTaskExecutor.execute(store, command));
        } catch (final WalletTaskExecutor.Rejected e) {
            throw new WalletEngine.PositionReservationCommitFailedException(referenceId);
        }
    }
}
