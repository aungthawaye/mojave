package org.mojave.mono.core.admin.api.command.wallet.position;

import org.mojave.core.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.core.wallet.contract.command.position.DecreasePositionCommand;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.command.position.IncreasePositionCommand;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.core.wallet.contract.exception.position.FailedToFulfilPositionsException;
import org.mojave.core.wallet.contract.exception.position.FailedToRollbackReservationException;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class PositionCommandController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionCommandController.class);

    private final CommitReservationCommand commitReservationCommand;

    private final DecreasePositionCommand decreasePositionCommand;

    private final FulfilPositionsCommand fulfilPositionsCommand;

    private final IncreasePositionCommand increasePositionCommand;

    private final ReservePositionCommand reservePositionCommand;

    private final RollbackReservationCommand rollbackReservationCommand;

    public PositionCommandController(final CommitReservationCommand commitReservationCommand,
                                     final DecreasePositionCommand decreasePositionCommand,
                                     final FulfilPositionsCommand fulfilPositionsCommand,
                                     final IncreasePositionCommand increasePositionCommand,
                                     final ReservePositionCommand reservePositionCommand,
                                     final RollbackReservationCommand rollbackReservationCommand) {

        Objects.requireNonNull(commitReservationCommand);
        Objects.requireNonNull(decreasePositionCommand);
        Objects.requireNonNull(fulfilPositionsCommand);
        Objects.requireNonNull(increasePositionCommand);
        Objects.requireNonNull(reservePositionCommand);
        Objects.requireNonNull(rollbackReservationCommand);

        this.commitReservationCommand = commitReservationCommand;
        this.decreasePositionCommand = decreasePositionCommand;
        this.fulfilPositionsCommand = fulfilPositionsCommand;
        this.increasePositionCommand = increasePositionCommand;
        this.reservePositionCommand = reservePositionCommand;
        this.rollbackReservationCommand = rollbackReservationCommand;
    }

    @PostMapping("/wallet/position/commit-reservation")
    public ResponseEntity<CommitReservationCommand.Output> commitReservation(
        @RequestBody final CommitReservationCommand.Input input)
        throws FailedToCommitReservationException {

        LOGGER.info("CommitReservationCommand: input ({})", input);

        final var output = this.commitReservationCommand.execute(input);

        LOGGER.info("CommitReservationCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

    @PostMapping("/wallet/position/decrease-position")
    public ResponseEntity<DecreasePositionCommand.Output> decreasePosition(
        @RequestBody final DecreasePositionCommand.Input input)
        throws NoPositionUpdateForTransactionException {

        LOGGER.info("DecreasePositionCommand: input ({})", input);

        final var output = this.decreasePositionCommand.execute(input);

        LOGGER.info("DecreasePositionCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

    @PostMapping("/wallet/position/fulfil-positions")
    public ResponseEntity<FulfilPositionsCommand.Output> fulfilPositions(
        @RequestBody final FulfilPositionsCommand.Input input)
        throws FailedToFulfilPositionsException {

        LOGGER.info("FulfilPositionsCommand: input ({})", input);

        final var output = this.fulfilPositionsCommand.execute(input);

        LOGGER.info("FulfilPositionsCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

    @PostMapping("/wallet/position/increase-position")
    public ResponseEntity<IncreasePositionCommand.Output> increasePosition(
        @RequestBody final IncreasePositionCommand.Input input) throws
                                                              NoPositionUpdateForTransactionException,
                                                              PositionLimitExceededException,
                                                              WalletNotFoundException {

        LOGGER.info("IncreasePositionCommand: input ({})", input);

        final var output = this.increasePositionCommand.execute(input);

        LOGGER.info("IncreasePositionCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

    @PostMapping("/wallet/position/reserve-position")
    public ResponseEntity<ReservePositionCommand.Output> reservePosition(
        @RequestBody final ReservePositionCommand.Input input) throws
                                                             PositionLimitExceededException,
                                                             NoPositionUpdateForTransactionException,
                                                             WalletNotFoundException {

        LOGGER.info("ReservePositionCommand: input ({})", input);

        final var output = this.reservePositionCommand.execute(input);

        LOGGER.info("ReservePositionCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

    @PostMapping("/wallet/position/rollback-reservation")
    public ResponseEntity<RollbackReservationCommand.Output> rollbackReservation(
        @RequestBody final RollbackReservationCommand.Input input)
        throws FailedToRollbackReservationException {

        LOGGER.info("RollbackReservationCommand: input ({})", input);

        final var output = this.rollbackReservationCommand.execute(input);

        LOGGER.info("RollbackReservationCommand: output ({})", output);

        return ResponseEntity.ok(output);
    }

}
