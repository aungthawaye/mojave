package org.mojave.core.wallet.engine.tigerbeetle.store;

import org.mojave.scheme.rule.enums.Currency;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

public final class WalletRecord {

    public static final int SCALE = 4;

    private static final BigInteger MAX_UNITS = BigInteger.TEN.pow(34).subtract(BigInteger.ONE);

    private WalletRecord() { }

    public static BigDecimal amount(final BigDecimal value) {

        Objects.requireNonNull(value, "amount");
        final var normalized = value.setScale(SCALE, RoundingMode.UNNECESSARY);
        if (normalized.unscaledValue().abs().compareTo(MAX_UNITS) > 0) {
            throw new IllegalArgumentException("Amount exceeds decimal(34, 4)");
        }
        return normalized;
    }

    public enum Action {
        DEPOSIT(1, 1), WITHDRAW(1, 2), REVERSE_WITHDRAW(1, 3),
        INCREASE_POSITION(2, 4), DECREASE_POSITION(2, 5), RESERVE(2, 6), COMMIT(2, 7), ROLLBACK(2, 8),
        INCREASE_NDC(3, 9), DECREASE_NDC(3, 10);

        private final int group;

        private final int code;

        Action(final int group, final int code) {

            this.group = group;
            this.code = code;
        }

        public int code() {

            return code;
        }

        public int group() {

            return group;
        }
    }

    public record State(long wallet, Currency currency, int scale, long version,
                        BigDecimal balance, BigDecimal position, BigDecimal reserved,
                        BigDecimal ndc) {

        public State {

            balance = amount(balance);
            position = amount(position);
            reserved = amount(reserved);
            ndc = amount(ndc);
            if (balance.signum() < 0 || reserved.signum() < 0 || ndc.signum() < 0) {
                throw new IllegalArgumentException("Negative balance, reservation, or NDC");
            }
        }

        public State next(final BigDecimal balance, final BigDecimal position,
                          final BigDecimal reserved, final BigDecimal ndc) {

            return new State(wallet, currency, scale, Math.incrementExact(version),
                balance, position, reserved, ndc);
        }
    }

    public record Command(Action action, long id, long wallet, long transaction, Instant at,
                          BigDecimal amount, long reference, String description,
                          long peer, long peerUpdate) {

        public Command {

            Objects.requireNonNull(action, "action");
            Objects.requireNonNull(at, "transactionAt");
            amount = WalletRecord.amount(amount);
            if (id <= 0 || wallet <= 0 || transaction <= 0 || amount.signum() < 0) {
                throw new IllegalArgumentException("IDs must be positive and amount nonnegative");
            }
            if (description != null && description.codePointCount(0, description.length()) > 255) {
                throw new IllegalArgumentException("Description exceeds 255 characters");
            }
        }

        public boolean matches(final Command other) {

            return action == other.action && id == other.id && wallet == other.wallet &&
                transaction == other.transaction &&
                (action == Action.REVERSE_WITHDRAW || at.equals(other.at)) &&
                amount.equals(other.amount) && reference == other.reference &&
                Objects.equals(description, other.description) && peer == other.peer &&
                peerUpdate == other.peerUpdate;
        }
    }

    public record Event(Command command, State before, State after) { }
}
