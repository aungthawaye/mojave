package org.mojave.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.wallet.contract.exception.position.PositionNotExistException;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.domain.BaseIT;
import org.mojave.wallet.domain.WalletDomainTestConfiguration;
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
                             PositionNotExistException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 402L, Currency.USD, "Commit Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("50.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"));

        final var reservation = this.reservePositionCommand.execute(
            new ReservePositionCommand.Input(
                new WalletOwnerId(402L), Currency.USD, new BigDecimal("10.00"),
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
