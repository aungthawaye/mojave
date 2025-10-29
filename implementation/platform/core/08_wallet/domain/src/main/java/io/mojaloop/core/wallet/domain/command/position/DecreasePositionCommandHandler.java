package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.domain.component.PositionUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DecreasePositionCommandHandler implements DecreasePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecreasePositionCommandHandler.class);

    private final PositionUpdater positionUpdater;

    public DecreasePositionCommandHandler(final PositionUpdater positionUpdater) {

        assert positionUpdater != null;
        this.positionUpdater = positionUpdater;
    }

    @Override
    public Output execute(final Input input) {

        LOGGER.info("Executing DecreasePositionCommand with input: {}", input);

        final var positionUpdateId = new PositionUpdateId(Snowflake.get().nextId());

        try {
            final var history = this.positionUpdater.decrease(input.transactionId(), input.transactionAt(),
                                                              positionUpdateId, input.positionId(), input.amount(),
                                                              input.description());

            final var output = new Output(history.positionUpdateId(), history.positionId(), history.action(),
                                          history.transactionId(), history.currency(), history.amount(),
                                          history.oldPosition(), history.newPosition(), history.oldReserved(),
                                          history.newReserved(), history.netDebitCap(), history.transactionAt());

            LOGGER.info("DecreasePositionCommand executed successfully with output: {}", output);

            return output;

        } catch (final PositionUpdater.NoPositionUpdateException e) {

            LOGGER.error("No position update created for transaction: {}", input.transactionId());
            throw new RuntimeException(e);
        }
    }

}
