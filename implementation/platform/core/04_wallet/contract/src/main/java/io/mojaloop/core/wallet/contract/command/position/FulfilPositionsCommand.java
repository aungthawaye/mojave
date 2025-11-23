package io.mojaloop.core.wallet.contract.command.position;

import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionNotExistException;

public interface FulfilPositionsCommand {

    Output execute(Input input) throws
                                FailedToCommitReservationException,
                                PositionNotExistException,
                                NoPositionUpdateForTransactionException;

    record Input(CommitReservationCommand.Input reservation,
                 DecreasePositionCommand.Input decrease) { }

    record Output(CommitReservationCommand.Output reservation,
                  DecreasePositionCommand.Output decrease) { }

}
