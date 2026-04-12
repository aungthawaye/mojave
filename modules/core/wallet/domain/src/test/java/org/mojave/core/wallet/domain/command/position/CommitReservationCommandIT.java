package org.mojave.core.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.wallet.PositionAction;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.PositionUpdateId;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletDomainTestConfiguration.class})
@DisplayName("Commit Reservation Command Integration Test")
public class CommitReservationCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-11T11:20:35Z");

    @Autowired
    private CommitReservationCommand commitReservationCommand;

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private ReservePositionCommand reservePositionCommand;

    @Test
    @DisplayName("Fail to commit reservation when engine cannot commit")
    public void failed() {

        final var reservationId = new PositionUpdateId(40101L);

        final var exception = assertThrows(
            FailedToCommitReservationException.class,
            () -> this.commitReservationCommand.execute(
                new CommitReservationCommand.Input(reservationId, "Commit reservation")));

        assertEquals(reservationId, exception.getReservationId());
    }

    @Test
    @DisplayName("Commit reservation successfully")
    public void successful() throws
                             FailedToCommitReservationException,
                             NoPositionUpdateForTransactionException,
                             PositionLimitExceededException,
                             WalletNotFoundException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 402L, Currency.USD, "Commit Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("50.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"));

        final var reservation = this.reservePositionCommand.execute(
            new ReservePositionCommand.Input(
                new WalletOwnerId(402L), Currency.USD, "P2P_TRANSFER", new BigDecimal("10.00"),
                new TransactionId(40201L), TRANSACTION_AT, "Reserve before commit"));

        final var output = this.commitReservationCommand.execute(
            new CommitReservationCommand.Input(
                reservation.positionUpdateId(), "Commit reservation"));

        assertNotNull(output.positionUpdateId());
        assertEquals(new WalletId(walletId.getId()), output.walletId());
        assertEquals(PositionAction.COMMIT, output.action());
        assertEquals(0, output.newPosition().compareTo(new BigDecimal("60.00")));
        assertEquals(0, output.newReserved().compareTo(BigDecimal.ZERO));
    }

}
