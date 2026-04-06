package org.mojave.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.command.position.CommitReservationCommand;
import org.mojave.wallet.contract.exception.position.FailedToCommitReservationException;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.mojave.wallet.domain.BaseIT;
import org.mojave.wallet.domain.WalletDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    @DisplayName("Fail to commit reservation when engine cannot commit")
    public void failed() {

        final var reservationId = new PositionUpdateId(40101L);

        this.testWalletEngine.failNextCommitPositionReservation(
            new WalletEngine.PositionReservationCommitFailedException(reservationId));

        final var exception = assertThrows(
            FailedToCommitReservationException.class,
            () -> this.commitReservationCommand.execute(
                new CommitReservationCommand.Input(reservationId, "Commit reservation")));

        assertEquals(reservationId, exception.getReservationId());
    }

    @Test
    @DisplayName("Commit reservation successfully")
    public void successful() throws FailedToCommitReservationException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 402L, Currency.USD, "Commit Wallet");
        final var reservationId = new PositionUpdateId(40201L);
        final var history = this.positionHistory(
            new PositionUpdateId(40202L), walletId, PositionAction.COMMIT,
            new TransactionId(40203L), Currency.USD, new BigDecimal("10.00"),
            new BigDecimal("50.00"), new BigDecimal("50.00"), new BigDecimal("10.00"),
            BigDecimal.ZERO, new BigDecimal("100.00"), TRANSACTION_AT);

        this.testWalletEngine.completeNextCommitPositionReservation(history);

        final var output = this.commitReservationCommand.execute(
            new CommitReservationCommand.Input(reservationId, "Commit reservation"));

        assertEquals(history.positionUpdateId(), output.positionUpdateId());
        assertEquals(new PositionId(walletId.getId()), output.positionId());
        assertEquals(PositionAction.COMMIT, output.action());
        assertEquals(BigDecimal.ZERO, output.newReserved());
    }

}
