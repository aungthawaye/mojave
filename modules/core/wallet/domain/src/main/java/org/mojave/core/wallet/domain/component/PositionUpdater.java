/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */

package org.mojave.core.wallet.domain.component;

import lombok.Getter;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;

import java.math.BigDecimal;
import java.time.Instant;

public interface PositionUpdater {

    PositionHistory commit(PositionUpdateId reservationId, PositionUpdateId positionUpdateId)
        throws CommitFailedException;

    PositionHistory decrease(TransactionId transactionId,
                             Instant transactionAt,
                             PositionUpdateId positionUpdateId,
                             PositionId positionId,
                             BigDecimal amount,
                             String description) throws NoPositionUpdateException;

    FulfilResult fulfil(PositionUpdateId reservationId,
                        PositionUpdateId reservationCommitId,
                        PositionUpdateId positionDecrementId,
                        PositionId payeePositionId,
                        String description) throws NoPositionFulfilmentException;

    PositionHistory increase(TransactionId transactionId,
                             Instant transactionAt,
                             PositionUpdateId positionUpdateId,
                             PositionId positionId,
                             BigDecimal amount,
                             String description)
        throws NoPositionUpdateException, LimitExceededException;

    PositionHistory reserve(TransactionId transactionId,
                            Instant transactionAt,
                            PositionUpdateId positionUpdateId,
                            PositionId positionId,
                            BigDecimal amount,
                            String description)
        throws NoPositionUpdateException, LimitExceededException;

    PositionHistory rollback(PositionUpdateId reservationId, PositionUpdateId positionUpdateId)
        throws RollbackFailedException;

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

    record FulfilResult(PositionUpdateId payerCommitmentId, PositionUpdateId payeeCommitmentId) { }

    @Getter
    class NoPositionUpdateException extends Exception {

        private final TransactionId transactionId;

        public NoPositionUpdateException(TransactionId transactionId) {

            super("No position update found for transactionId: " + transactionId);
            this.transactionId = transactionId;
        }

    }

    @Getter
    class NoPositionFulfilmentException extends Exception {

        private final PositionUpdateId reservationId;

        public NoPositionFulfilmentException(PositionUpdateId reservationId) {

            super("No position fulfilment found for reservationId: " + reservationId);
            this.reservationId = reservationId;
        }

    }

    @Getter
    class LimitExceededException extends Exception {

        private final PositionId positionId;

        private final BigDecimal amount;

        private final BigDecimal oldPosition;

        private final BigDecimal oldReserved;

        private final BigDecimal netDebitCap;

        private final TransactionId transactionId;

        public LimitExceededException(PositionId positionId,
                                      BigDecimal amount,
                                      BigDecimal oldPosition,
                                      BigDecimal oldReserved,
                                      BigDecimal netDebitCap,
                                      TransactionId transactionId) {

            super("Position limit exceeded for positionId: " + positionId + ", amount: " + amount +
                      ", oldPosition: " + oldPosition + ", oldReserved: " + oldReserved +
                      ", netDebitCap: " + netDebitCap.stripTrailingZeros().toPlainString() +
                      ", transactionId: " + transactionId);

            this.positionId = positionId;
            this.amount = amount;
            this.oldPosition = oldPosition;
            this.oldReserved = oldReserved;
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
