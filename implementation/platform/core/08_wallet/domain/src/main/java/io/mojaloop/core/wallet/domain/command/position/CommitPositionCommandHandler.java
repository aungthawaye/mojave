package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.core.wallet.contract.command.position.CommitPositionCommand;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CommitPositionCommandHandler implements CommitPositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitPositionCommandHandler.class);

    private final PositionUpdater positionUpdater;

    public CommitPositionCommandHandler(final PositionUpdater positionUpdater) {

        assert positionUpdater != null;
        this.positionUpdater = positionUpdater;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("Executing CommitPositionCommand with input: {}", input);

        try {

            final var history = this.positionUpdater.commit(input.reservationId(), input.positionUpdateId());

            final var output = new Output(history.positionUpdateId(),
                                          history.positionId(),
                                          history.action(),
                                          history.transactionId(),
                                          history.currency(),
                                          history.amount(),
                                          history.oldPosition(),
                                          history.newPosition(),
                                          history.oldReserved(),
                                          history.newReserved(),
                                          history.netDebitCap(),
                                          history.transactionAt());

            LOGGER.info("CommitPositionCommand executed successfully with output: {}", output);

            return output;

        } catch (final PositionUpdater.CommitFailedException e) {

            LOGGER.error("Commit failed for reservationId: {}", e.getReservationId());
            throw new RuntimeException(e);
        }
    }

}
