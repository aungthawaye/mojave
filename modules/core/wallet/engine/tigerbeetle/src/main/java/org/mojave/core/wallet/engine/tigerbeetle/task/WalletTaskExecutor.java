package org.mojave.core.wallet.engine.tigerbeetle.task;

import org.mojave.core.wallet.engine.tigerbeetle.store.TigerBeetleWalletStore;
import org.mojave.core.wallet.engine.tigerbeetle.store.WalletRecord;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

final class WalletTaskExecutor {

    private static final int MAX_ATTEMPTS = 128;

    private WalletTaskExecutor() { }

    static WalletRecord.Event execute(final TigerBeetleWalletStore store,
                                      final WalletRecord.Command command) {

        for (var attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            final var previous = store.event(command.action().group(), command.id());
            if (previous != null) {
                if (!previous.command().matches(command)) {
                    throw new Rejected(Reason.DUPLICATE, previous.before());
                }
                return previous;
            }
            final var state = store.state(command.wallet());
            if (state == null) {
                throw new Rejected(Reason.NOT_FOUND, null);
            }
            if (store.exists(TigerBeetleWalletStore.businessId(command)) ||
                command.reference() != 0 && store.exists(TigerBeetleWalletStore.resolutionId(
                    command.action().group(), command.reference()))) {
                // Another caller may have committed this exact request since the initial lookup.
                final var completed = store.event(command.action().group(), command.id());
                if (completed != null && completed.command().matches(command)) {
                    return completed;
                }
                throw new Rejected(Reason.DUPLICATE, state);
            }
            final var event = new WalletRecord.Event(command, state, apply(state, command));
            if (store.append(List.of(event))) {
                return event;
            }
        }
        throw new IllegalStateException("Wallet contention retry limit exceeded; retry the same update ID");
    }

    static void fulfil(final TigerBeetleWalletStore store, final long reservation,
                       final long commitment, final long decrement, final long payee,
                       final String description) {

        if (commitment == decrement) {
            throw new Rejected(Reason.DUPLICATE, null);
        }
        final var original = reference(store, 2, reservation, WalletRecord.Action.RESERVE);
        final var source = original.command();
        final var payerCommand = new WalletRecord.Command(WalletRecord.Action.COMMIT, commitment,
            source.wallet(), source.transaction(), source.at(), source.amount(), reservation,
            description, payee, decrement);
        final var payeeCommand = new WalletRecord.Command(WalletRecord.Action.DECREASE_POSITION,
            decrement, payee, source.transaction(), source.at(), source.amount(), 0, description,
            source.wallet(), commitment);

        for (var attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            final var payerPrevious = store.event(2, commitment);
            final var payeePrevious = store.event(2, decrement);
            if (payerPrevious != null || payeePrevious != null) {
                // The lookups can straddle a concurrent commit; re-read a missing half.
                final var payer = payerPrevious != null ? payerPrevious : store.event(2, commitment);
                final var receiver = payeePrevious != null ? payeePrevious : store.event(2, decrement);
                if (payer != null && receiver != null && payer.command().matches(payerCommand) &&
                    receiver.command().matches(payeeCommand)) {
                    return;
                }
                throw new Rejected(Reason.DUPLICATE, null);
            }
            final var payerBefore = store.state(source.wallet());
            final var receiverBefore = source.wallet() == payee ? payerBefore : store.state(payee);
            if (payerBefore == null || receiverBefore == null || payerBefore.currency() != receiverBefore.currency()) {
                throw new Rejected(Reason.NOT_FOUND, null);
            }
            if (store.exists(TigerBeetleWalletStore.resolutionId(2, reservation)) ||
                store.exists(TigerBeetleWalletStore.businessId(payerCommand)) ||
                store.exists(TigerBeetleWalletStore.businessId(payeeCommand))) {
                final var completed = store.event(2, commitment);
                if (completed != null && completed.command().matches(payerCommand)) {
                    continue;
                }
                throw new Rejected(Reason.DUPLICATE, payerBefore);
            }
            final var payerAfter = apply(payerBefore, payerCommand);
            final var payeeBefore = source.wallet() == payee ? payerAfter : receiverBefore;
            final var payeeAfter = apply(payeeBefore, payeeCommand);
            if (store.append(List.of(new WalletRecord.Event(payerCommand, payerBefore, payerAfter),
                new WalletRecord.Event(payeeCommand, payeeBefore, payeeAfter)))) {
                return;
            }
        }
        throw new IllegalStateException("Wallet fulfilment contention retry limit exceeded");
    }

    static WalletRecord.Command resolution(final TigerBeetleWalletStore store,
                                            final WalletRecord.Action action, final long id,
                                            final long reference) {

        final var original = reference(store, action.group(), reference,
            action == WalletRecord.Action.REVERSE_WITHDRAW ? WalletRecord.Action.WITHDRAW : WalletRecord.Action.RESERVE);
        final var source = original.command();
        return new WalletRecord.Command(action, id, source.wallet(), source.transaction(),
            action == WalletRecord.Action.REVERSE_WITHDRAW ? Instant.now() : source.at(),
            source.amount(), reference, source.description(), 0, 0);
    }

    private static WalletRecord.Event reference(final TigerBeetleWalletStore store, final int group,
                                                final long id, final WalletRecord.Action expected) {

        final var original = store.event(group, id);
        if (original == null || original.command().action() != expected) {
            throw new Rejected(Reason.NOT_FOUND, null);
        }
        return original;
    }

    private static WalletRecord.State apply(final WalletRecord.State state,
                                            final WalletRecord.Command command) {

        final var amount = command.amount();
        var balance = state.balance();
        var position = state.position();
        var reserved = state.reserved();
        var ndc = state.ndc();
        switch (command.action()) {
            case DEPOSIT, REVERSE_WITHDRAW -> balance = balance.add(amount);
            case WITHDRAW -> {
                if (balance.compareTo(amount) < 0) {
                    throw new Rejected(Reason.INSUFFICIENT_BALANCE, state);
                }
                balance = balance.subtract(amount);
            }
            case INCREASE_POSITION, RESERVE -> {
                if (position.add(reserved).add(amount).compareTo(ndc) > 0) {
                    throw new Rejected(Reason.POSITION_LIMIT, state);
                }
                if (command.action() == WalletRecord.Action.RESERVE) {
                    reserved = reserved.add(amount);
                } else {
                    position = position.add(amount);
                }
            }
            case DECREASE_POSITION -> position = position.subtract(amount);
            case COMMIT, ROLLBACK -> {
                if (reserved.compareTo(amount) < 0) {
                    throw new Rejected(Reason.NOT_FOUND, state);
                }
                reserved = reserved.subtract(amount);
                if (command.action() == WalletRecord.Action.COMMIT) {
                    position = position.add(amount);
                }
            }
            case INCREASE_NDC -> {
                ndc = ndc.add(amount);
                if (balance.compareTo(ndc) < 0) {
                    throw new Rejected(Reason.NDC_EXCEEDS_BALANCE, state);
                }
            }
            case DECREASE_NDC -> {
                ndc = ndc.subtract(amount).max(BigDecimal.ZERO);
                if (position.add(reserved).compareTo(ndc) > 0) {
                    throw new Rejected(Reason.NDC_BELOW_POSITION, state);
                }
            }
        }
        return state.next(balance, position, reserved, ndc);
    }

    enum Reason {
        NOT_FOUND, DUPLICATE, INSUFFICIENT_BALANCE, POSITION_LIMIT,
        NDC_EXCEEDS_BALANCE, NDC_BELOW_POSITION
    }

    static final class Rejected extends RuntimeException {

        private final Reason reason;

        private final WalletRecord.State state;

        Rejected(final Reason reason, final WalletRecord.State state) {

            super("Wallet operation rejected: " + reason);
            this.reason = reason;
            this.state = state;
        }

        Reason reason() {

            return reason;
        }

        WalletRecord.State state() {

            return state;
        }
    }
}
