package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.contract.engine.WalletEngine;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;
import org.mojave.scheme.rule.enums.wallet.BalanceAction;
import org.mojave.scheme.rule.enums.wallet.PositionAction;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.BalanceUpdateId;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

final class WalletTaskMapper {

    private WalletTaskMapper() { }

    static WalletEngine.BalanceHistory balance(final WalletRecord.Event event) {

        final var command = event.command();
        return new WalletEngine.BalanceHistory(new BalanceUpdateId(command.id()),
            new WalletId(command.wallet()), BalanceAction.valueOf(command.action().name()),
            new TransactionId(command.transaction()), event.before().currency(), command.amount(),
            event.before().balance(), event.after().balance(), command.at(),
            command.reference() == 0 ? null : new BalanceUpdateId(command.reference()));
    }

    static WalletEngine.PositionHistory position(final WalletRecord.Event event) {

        final var command = event.command();
        final var action = switch (command.action()) {
            case INCREASE_POSITION -> PositionAction.INCREASE;
            case DECREASE_POSITION -> PositionAction.DECREASE;
            default -> PositionAction.valueOf(command.action().name());
        };
        return new WalletEngine.PositionHistory(new PositionUpdateId(command.id()),
            new WalletId(command.wallet()), action, new TransactionId(command.transaction()),
            event.before().currency(), command.amount(), event.before().position(),
            event.after().position(), event.before().reserved(), event.after().reserved(),
            event.before().ndc(), command.at());
    }

    static WalletEngine.NdcHistory ndc(final WalletRecord.Event event) {

        final var command = event.command();
        return new WalletEngine.NdcHistory(new NdcUpdateId(command.id()), new WalletId(command.wallet()),
            command.action() == WalletRecord.Action.INCREASE_NDC ? PositionAction.INCREASE : PositionAction.DECREASE,
            new TransactionId(command.transaction()), event.before().currency(), command.amount(),
            event.before().ndc(), event.after().ndc(), command.at());
    }
}
