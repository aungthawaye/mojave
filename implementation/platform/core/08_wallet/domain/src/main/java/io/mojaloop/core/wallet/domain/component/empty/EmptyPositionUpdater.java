package io.mojaloop.core.wallet.domain.component.empty;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;

import java.math.BigDecimal;
import java.time.Instant;

public class EmptyPositionUpdater implements PositionUpdater {

    @Override
    public PositionHistory commit(PositionUpdateId reservationId, PositionUpdateId positionUpdateId) throws CommitFailedException {

        throw new UnsupportedOperationException("EmptyPositionUpdater does not support commit operation");
    }

    @Override
    public PositionHistory decrease(TransactionId transactionId,
                                    Instant transactionAt,
                                    PositionUpdateId positionUpdateId,
                                    PositionId positionId,
                                    BigDecimal amount,
                                    String description) throws NoPositionUpdateException {

        throw new UnsupportedOperationException("EmptyPositionUpdater does not support decrease operation");
    }

    @Override
    public PositionHistory increase(TransactionId transactionId,
                                    Instant transactionAt,
                                    PositionUpdateId positionUpdateId,
                                    PositionId positionId,
                                    BigDecimal amount,
                                    String description) throws NoPositionUpdateException, LimitExceededException {

        throw new UnsupportedOperationException("EmptyPositionUpdater does not support increase operation");
    }

    @Override
    public PositionHistory reserve(TransactionId transactionId,
                                   Instant transactionAt,
                                   PositionUpdateId positionUpdateId,
                                   PositionId positionId,
                                   BigDecimal amount,
                                   String description) throws NoPositionUpdateException, LimitExceededException {

        throw new UnsupportedOperationException("EmptyPositionUpdater does not support reserve operation");
    }

    @Override
    public PositionHistory rollback(PositionUpdateId reservationId, PositionUpdateId positionUpdateId) throws RollbackFailedException {

        throw new UnsupportedOperationException("EmptyPositionUpdater does not support rollback operation");
    }

}
