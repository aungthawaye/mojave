package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

public final class FulfilPositionsTask {

    private FulfilPositionsTask() { }

    public static WalletEngine.PositionFulfilmentResult execute(final TigerBeetleWalletStore store,
                                                                final PositionUpdateId reservationId,
                                                                final PositionUpdateId commitmentId,
                                                                final PositionUpdateId decrementId,
                                                                final WalletId payeeWalletId,
                                                                final String description)
        throws WalletEngine.NoPositionFulfilmentException {

        try {
            WalletTaskExecutor.fulfil(store, reservationId.getId(), commitmentId.getId(),
                decrementId.getId(), payeeWalletId.getId(), description);
            return new WalletEngine.PositionFulfilmentResult(commitmentId, decrementId);
        } catch (final WalletTaskExecutor.Rejected e) {
            throw new WalletEngine.NoPositionFulfilmentException(reservationId);
        }
    }
}
