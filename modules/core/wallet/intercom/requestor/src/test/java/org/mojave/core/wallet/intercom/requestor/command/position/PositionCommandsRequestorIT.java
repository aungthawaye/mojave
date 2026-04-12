package org.mojave.core.wallet.intercom.requestor.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.core.wallet.contract.command.position.DecreasePositionCommand;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.command.position.IncreasePositionCommand;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.core.wallet.contract.exception.position.FailedToRollbackReservationException;
import org.mojave.core.wallet.intercom.requestor.WalletIntercomRequestorTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletIntercomRequestorTestConfiguration.class})
@DisplayName("Position Commands Requestor Integration Test")
public class PositionCommandsRequestorIT {

    @Autowired
    private CommitReservationCommand commitReservationCommand;

    @Autowired
    private DecreasePositionCommand decreasePositionCommand;

    @Autowired
    private FulfilPositionsCommand fulfilPositionsCommand;

    @Autowired
    private IncreasePositionCommand increasePositionCommand;

    @Autowired
    private ReservePositionCommand reservePositionCommand;

    @Autowired
    private RollbackReservationCommand rollbackReservationCommand;

    @Test
    @DisplayName("Execute position commands through requestor")
    public void successful() {

        final var transactionAt = Instant.now();

        assertThrows(
            WalletNotFoundException.class,
            () -> this.increasePositionCommand.execute(
                new IncreasePositionCommand.Input(
                    new WalletOwnerId(900201L),
                    Currency.USD,
                    "P2P_TRANSFER",
                    new BigDecimal("10.00"),
                    new TransactionId(90020101L),
                    transactionAt,
                    "requestor-increase-missing-position")));

        assertThrows(
            WalletNotFoundException.class,
            () -> this.decreasePositionCommand.execute(
                new DecreasePositionCommand.Input(
                    new WalletOwnerId(900202L),
                    Currency.USD,
                    "P2P_TRANSFER",
                    new BigDecimal("8.00"),
                    new TransactionId(90020201L),
                    transactionAt,
                    "requestor-decrease-missing-position")));

        assertThrows(
            WalletNotFoundException.class,
            () -> this.reservePositionCommand.execute(
                new ReservePositionCommand.Input(
                    new WalletOwnerId(900203L),
                    Currency.USD,
                    "P2P_TRANSFER",
                    new BigDecimal("4.00"),
                    new TransactionId(90020301L),
                    transactionAt,
                    "requestor-reserve-missing-position")));

        assertThrows(
            WalletNotFoundException.class,
            () -> this.fulfilPositionsCommand.execute(
                new FulfilPositionsCommand.Input(
                    new PositionUpdateId(90020401L),
                    new WalletOwnerId(900204L),
                    Currency.USD,
                    "P2P_TRANSFER",
                    "requestor-fulfil-missing-payee-position")));

        assertThrows(
            FailedToCommitReservationException.class,
            () -> this.commitReservationCommand.execute(
                new CommitReservationCommand.Input(
                    new PositionUpdateId(90020501L),
                    "requestor-commit-missing-reservation")));

        assertThrows(
            FailedToRollbackReservationException.class,
            () -> this.rollbackReservationCommand.execute(
                new RollbackReservationCommand.Input(
                    new PositionUpdateId(90020601L),
                    "requestor-rollback-missing-reservation")));
    }

}
