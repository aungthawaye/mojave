package org.mojave.core.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.command.position.RollbackReservationCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.FailedToRollbackReservationException;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.scheme.rule.wallet.WalletPurpose;
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
@DisplayName("Rollback Reservation Command Integration Test")
public class RollbackReservationCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-11T11:20:35Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private RollbackReservationCommand rollbackReservationCommand;

    @Autowired
    private ReservePositionCommand reservePositionCommand;

    @Test
    @DisplayName("Fail to rollback reservation when engine cannot rollback")
    public void failed() {

        final var reservationId = new PositionUpdateId(41701L);

        final var exception = assertThrows(
            FailedToRollbackReservationException.class,
            () -> this.rollbackReservationCommand.execute(
                new RollbackReservationCommand.Input(reservationId, "Rollback reservation")));

        assertEquals(reservationId, exception.getReservationId());
    }

    @Test
    @DisplayName("Rollback reservation successfully")
    public void successful() throws
                             FailedToRollbackReservationException,
                             NoPositionUpdateForTransactionException,
                             PositionLimitExceededException,
                             WalletNotFoundException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 418L, Currency.USD, "Rollback Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("10.00"), BigDecimal.ZERO,
            new BigDecimal("25.00"));

        final var reservation = this.reservePositionCommand.execute(
            new ReservePositionCommand.Input(
                new WalletOwnerId(418L), Currency.USD, WalletPurpose.ANY, new BigDecimal("3.00"),
                new TransactionId(41801L), TRANSACTION_AT, "Reserve before rollback"));

        final var output = this.rollbackReservationCommand.execute(
            new RollbackReservationCommand.Input(
                reservation.positionUpdateId(), "Rollback reservation"));

        assertNotNull(output.positionUpdateId());
        assertEquals(new WalletId(walletId.getId()), output.walletId());
        assertEquals(PositionAction.ROLLBACK, output.action());
        assertEquals(0, output.newPosition().compareTo(new BigDecimal("10.00")));
        assertEquals(0, output.newReserved().compareTo(BigDecimal.ZERO));
    }

}
