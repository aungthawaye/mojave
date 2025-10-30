package io.mojaloop.core.wallet.domain.component;

import io.mojaloop.core.common.datatype.enums.wallet.PositionAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.fspiop.spec.core.Currency;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

public interface PositionUpdater {

    PositionHistory commit(PositionUpdateId reservationId, PositionUpdateId positionUpdateId) throws CommitFailedException;

    PositionHistory decrease(TransactionId transactionId,
                             Instant transactionAt,
                             PositionUpdateId positionUpdateId,
                             PositionId positionId,
                             BigDecimal amount,
                             String description) throws NoPositionUpdateException;

    PositionHistory increase(TransactionId transactionId,
                             Instant transactionAt,
                             PositionUpdateId positionUpdateId,
                             PositionId positionId,
                             BigDecimal amount,
                             String description) throws NoPositionUpdateException, LimitExceededException;

    PositionHistory reserve(TransactionId transactionId,
                            Instant transactionAt,
                            PositionUpdateId positionUpdateId,
                            PositionId positionId,
                            BigDecimal amount,
                            String description) throws NoPositionUpdateException, LimitExceededException;

    PositionHistory rollback(PositionUpdateId reservationId, PositionUpdateId positionUpdateId) throws RollbackFailedException;

    record PositionHistory(PositionUpdateId positionUpdateId,
                           PositionId positionId,
                           PositionAction action,
                           TransactionId transactionId,
                           Currency currency,
                           BigDecimal amount,
                           BigDecimal oldPosition,
                           BigDecimal newPosition,
                           BigDecimal oldReserved,
                           BigDecimal newReserved,
                           BigDecimal netDebitCap,
                           Instant transactionAt) { }

    @Getter
    class NoPositionUpdateException extends Exception {

        private final TransactionId transactionId;

        public NoPositionUpdateException(TransactionId transactionId) {

            super("No position update found for transactionId: " + transactionId);
            this.transactionId = transactionId;
        }

    }

    @Getter
    class LimitExceededException extends Exception {

        private final PositionId positionId;

        private final BigDecimal amount;

        private final BigDecimal oldPosition;

        private final BigDecimal netDebitCap;

        private final TransactionId transactionId;

        public LimitExceededException(PositionId positionId, BigDecimal amount, BigDecimal oldPosition, BigDecimal netDebitCap, TransactionId transactionId) {

            super("Position limit exceeded for positionId: " + positionId + ", amount: " + amount + ", oldPosition: " + oldPosition + ", netDebitCap: " +
                      netDebitCap.stripTrailingZeros().toPlainString() + ", transactionId: " + transactionId);

            this.positionId = positionId;
            this.amount = amount;
            this.oldPosition = oldPosition;
            this.netDebitCap = netDebitCap;
            this.transactionId = transactionId;
        }

    }

    @Getter
    class CommitFailedException extends Exception {

        private final PositionUpdateId reservationId;

        public CommitFailedException(PositionUpdateId reservationId) {

            super("Commit failed for reservationId: " + reservationId);
            this.reservationId = reservationId;
        }

    }

    @Getter
    class RollbackFailedException extends Exception {

        private final PositionUpdateId reservationId;

        public RollbackFailedException(PositionUpdateId reservationId) {

            super("Rollback failed for reservationId: " + reservationId);
            this.reservationId = reservationId;
        }

    }

}
