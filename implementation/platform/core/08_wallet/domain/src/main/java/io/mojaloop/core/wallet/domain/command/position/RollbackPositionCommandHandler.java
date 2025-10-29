package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.core.wallet.contract.command.position.RollbackPositionCommand;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RollbackPositionCommandHandler implements RollbackPositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RollbackPositionCommandHandler.class);

    private final PositionUpdater positionUpdater;

    public RollbackPositionCommandHandler(final PositionUpdater positionUpdater) {

        assert positionUpdater != null;
        this.positionUpdater = positionUpdater;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("Executing RollbackPositionCommand with input: {}", input);

        try {

            final var history = this.positionUpdater.rollback(input.reservationId(), input.positionUpdateId());

            final var output = new Output(history.positionUpdateId(), history.positionId(), history.action(),
                                          history.transactionId(), history.currency(), history.amount(),
                                          history.oldPosition(), history.newPosition(), history.oldReserved(),
                                          history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("RollbackPositionCommand executed successfully with output: {}", output);

            return output;

        } catch (final PositionUpdater.RollbackFailedException e) {

            LOGGER.error("Rollback failed for reservationId: {}", e.getReservationId());
            throw new RuntimeException(e);
        }
    }

}
