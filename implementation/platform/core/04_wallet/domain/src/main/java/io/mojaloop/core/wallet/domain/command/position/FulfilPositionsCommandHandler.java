package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.wallet.contract.command.position.CommitReservationCommand;
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.FulfilPositionsCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FulfilPositionsCommandHandler implements FulfilPositionsCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        FulfilPositionsCommandHandler.class);

    private final CommitReservationCommand commitReservationCommand;

    private final DecreasePositionCommand decreasePositionCommand;

    public FulfilPositionsCommandHandler(final CommitReservationCommand commitReservationCommand,
                                         final DecreasePositionCommand decreasePositionCommand) {

        assert commitReservationCommand != null;
        assert decreasePositionCommand != null;

        this.commitReservationCommand = commitReservationCommand;
        this.decreasePositionCommand = decreasePositionCommand;
    }

    @Override
    public Output execute(Input input) throws
                                       FailedToCommitReservationException,
                                       PositionNotExistException,
                                       NoPositionUpdateForTransactionException {

        LOGGER.info("FulfilPositionsCommand : input: ({})", ObjectLogger.log(input));

        PositionUpdateId payerCommitId = null;
        PositionUpdateId payeeCommitId = null;

        var commitReservationOutput = this.commitReservationCommand.execute(input.reservation());
        payerCommitId = commitReservationOutput.positionUpdateId();

        var decreasePositionOutput = this.decreasePositionCommand.execute(input.decrease());
        payeeCommitId = decreasePositionOutput.positionUpdateId();

        var output = new Output(commitReservationOutput, decreasePositionOutput);

        LOGGER.info("FulfilPositionsCommand : output: ({})", ObjectLogger.log(output));

        return output;
    }

}
