package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReservePositionCommandHandler implements ReservePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservePositionCommandHandler.class);

    private final PositionUpdater positionUpdater;

    public ReservePositionCommandHandler(final PositionUpdater positionUpdater) {

        assert positionUpdater != null;
        this.positionUpdater = positionUpdater;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("Executing ReservePositionCommand with input: {}", input);

        final var positionUpdateId = new PositionUpdateId(Snowflake.get().nextId());

        try {

            final var history = this.positionUpdater.reserve(input.transactionId(), input.transactionAt(),
                                                             positionUpdateId, input.positionId(), input.amount(),
                                                             input.description());

            final var output = new Output(history.positionUpdateId(), history.positionId(), history.action(),
                                          history.transactionId(), history.currency(), history.amount(),
                                          history.oldPosition(), history.newPosition(), history.oldReserved(),
                                          history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("ReservePositionCommand executed successfully with output: {}", output);

            return output;

        } catch (final PositionUpdater.NoPositionUpdateException e) {

            LOGGER.error("No position update created for transaction: {}", input.transactionId());
            throw new RuntimeException(e);

        } catch (final PositionUpdater.LimitExceededException e) {

            LOGGER.error("Position reservation exceeds limit for positionId: {} amount: {}", e.getPositionId(),
                         e.getAmount());
            throw new RuntimeException(e);
        }
    }

}
