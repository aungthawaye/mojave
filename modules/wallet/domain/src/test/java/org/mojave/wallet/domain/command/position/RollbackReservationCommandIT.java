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
import org.mojave.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.wallet.contract.exception.position.FailedToRollbackReservationException;
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
@DisplayName("Rollback Reservation Command Integration Test")
public class RollbackReservationCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-11T11:20:35Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private RollbackReservationCommand rollbackReservationCommand;

    @Test
    @DisplayName("Fail to rollback reservation when engine cannot rollback")
    public void failed() {

        final var reservationId = new PositionUpdateId(41701L);

        this.testWalletEngine.failNextRollbackPositionReservation(
            new WalletEngine.PositionReservationRollbackFailedException(reservationId));

        final var exception = assertThrows(
            FailedToRollbackReservationException.class,
            () -> this.rollbackReservationCommand.execute(
                new RollbackReservationCommand.Input(reservationId, "Rollback reservation")));

        assertEquals(reservationId, exception.getReservationId());
    }

    @Test
    @DisplayName("Rollback reservation successfully")
    public void successful() throws FailedToRollbackReservationException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 418L, Currency.USD, "Rollback Wallet");
        final var reservationId = new PositionUpdateId(41801L);
        final var history = this.positionHistory(
            new PositionUpdateId(41802L), walletId, PositionAction.ROLLBACK,
            new TransactionId(41803L), Currency.USD, new BigDecimal("3.00"),
            new BigDecimal("10.00"), new BigDecimal("10.00"), new BigDecimal("3.00"),
            BigDecimal.ZERO, new BigDecimal("25.00"), TRANSACTION_AT);

        this.testWalletEngine.completeNextRollbackPositionReservation(history);

        final var output = this.rollbackReservationCommand.execute(
            new RollbackReservationCommand.Input(reservationId, "Rollback reservation"));

        assertEquals(history.positionUpdateId(), output.positionUpdateId());
        assertEquals(new PositionId(walletId.getId()), output.positionId());
        assertEquals(PositionAction.ROLLBACK, output.action());
        assertEquals(BigDecimal.ZERO, output.newReserved());
    }

}
