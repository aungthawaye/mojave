package org.mojave.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.command.position.DecreasePositionCommand;
import org.mojave.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.wallet.contract.exception.position.PositionNotExistException;
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
@DisplayName("Decrease Position Command Integration Test")
public class DecreasePositionCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-11T11:20:35Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private DecreasePositionCommand decreasePositionCommand;

    @Test
    @DisplayName("Throw when decreasing missing position")
    public void positionNotExist() {

        final var exception = assertThrows(
            PositionNotExistException.class, () -> this.decreasePositionCommand.execute(
                new DecreasePositionCommand.Input(
                    new WalletOwnerId(403L), Currency.USD, new BigDecimal("3.00"),
                    new TransactionId(40301L), TRANSACTION_AT, "Decrease missing position")));

        assertEquals(new WalletOwnerId(403L), exception.getWalletOwnerId());
        assertEquals(Currency.USD, exception.getCurrency());
    }

    @Test
    @DisplayName("Throw when engine returns no position update")
    public void noPositionUpdate() {

        this.createDefaultWallet(this.createWalletCommand, 404L, Currency.USD, "Decrease Wallet");

        final var transactionId = new TransactionId(40401L);

        this.testWalletEngine.failNextDecreasePosition(
            new WalletEngine.NoPositionUpdateException(transactionId));

        final var exception = assertThrows(
            NoPositionUpdateForTransactionException.class,
            () -> this.decreasePositionCommand.execute(
                new DecreasePositionCommand.Input(
                    new WalletOwnerId(404L), Currency.USD, new BigDecimal("4.00"),
                    transactionId, TRANSACTION_AT, "Decrease without update")));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Decrease position successfully")
    public void successful() throws NoPositionUpdateForTransactionException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 405L, Currency.USD, "Decrease Wallet");
        final var transactionId = new TransactionId(40501L);
        final var history = this.positionHistory(
            new PositionUpdateId(40502L), walletId, PositionAction.DECREASE, transactionId,
            Currency.USD, new BigDecimal("4.00"), new BigDecimal("20.00"),
            new BigDecimal("16.00"), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("50.00"),
            TRANSACTION_AT);

        this.testWalletEngine.completeNextDecreasePosition(history);

        final var output = this.decreasePositionCommand.execute(
            new DecreasePositionCommand.Input(
                new WalletOwnerId(405L), Currency.USD, new BigDecimal("4.00"),
                transactionId, TRANSACTION_AT, "Decrease position"));

        assertEquals(history.positionUpdateId(), output.positionUpdateId());
        assertEquals(new PositionId(walletId.getId()), output.positionId());
        assertEquals(PositionAction.DECREASE, output.action());
        assertEquals(new BigDecimal("16.00"), output.newPosition());
    }

}
