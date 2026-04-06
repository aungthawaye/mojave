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
import org.mojave.wallet.contract.command.position.IncreasePositionCommand;
import org.mojave.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.wallet.contract.exception.position.PositionLimitExceededException;
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
@DisplayName("Increase Position Command Integration Test")
public class IncreasePositionCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-11T11:20:35Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private IncreasePositionCommand increasePositionCommand;

    @Test
    @DisplayName("Throw when increasing missing position")
    public void positionNotExist() {

        final var exception = assertThrows(
            PositionNotExistException.class, () -> this.increasePositionCommand.execute(
                new IncreasePositionCommand.Input(
                    new WalletOwnerId(409L), Currency.USD, new BigDecimal("9.00"),
                    new TransactionId(40901L), TRANSACTION_AT, "Increase missing position")));

        assertEquals(new WalletOwnerId(409L), exception.getWalletOwnerId());
        assertEquals(Currency.USD, exception.getCurrency());
    }

    @Test
    @DisplayName("Throw when engine returns no position update")
    public void noPositionUpdate() {

        this.createDefaultWallet(this.createWalletCommand, 410L, Currency.USD, "Increase Wallet");

        final var transactionId = new TransactionId(41001L);

        this.testWalletEngine.failNextIncreasePosition(
            new WalletEngine.NoPositionUpdateException(transactionId));

        final var exception = assertThrows(
            NoPositionUpdateForTransactionException.class,
            () -> this.increasePositionCommand.execute(
                new IncreasePositionCommand.Input(
                    new WalletOwnerId(410L), Currency.USD, new BigDecimal("9.00"),
                    transactionId, TRANSACTION_AT, "Increase without update")));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Throw when increase exceeds limit")
    public void positionLimitExceeded() {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 411L, Currency.USD, "Limit Wallet");
        final var transactionId = new TransactionId(41101L);

        this.testWalletEngine.failNextIncreasePosition(
            new WalletEngine.PositionLimitExceededException(
                walletId, new BigDecimal("20.00"), new BigDecimal("90.00"),
                new BigDecimal("5.00"), new BigDecimal("100.00"), transactionId));

        final var exception = assertThrows(
            PositionLimitExceededException.class, () -> this.increasePositionCommand.execute(
                new IncreasePositionCommand.Input(
                    new WalletOwnerId(411L), Currency.USD, new BigDecimal("20.00"),
                    transactionId, TRANSACTION_AT, "Increase too much")));

        assertEquals(new PositionId(walletId.getId()), exception.getPositionId());
        assertEquals(new BigDecimal("20.00"), exception.getAmount());
        assertEquals(new BigDecimal("90.00"), exception.getPosition());
        assertEquals(new BigDecimal("5.00"), exception.getReserved());
        assertEquals(new BigDecimal("100.00"), exception.getNetDebitCap());
        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Increase position successfully")
    public void successful() throws
                             NoPositionUpdateForTransactionException,
                             PositionLimitExceededException,
                             PositionNotExistException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 412L, Currency.USD, "Increase Wallet");
        final var transactionId = new TransactionId(41201L);
        final var history = this.positionHistory(
            new PositionUpdateId(41202L), walletId, PositionAction.INCREASE, transactionId,
            Currency.USD, new BigDecimal("11.00"), new BigDecimal("5.00"),
            new BigDecimal("16.00"), BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"),
            TRANSACTION_AT);

        this.testWalletEngine.completeNextIncreasePosition(history);

        final var output = this.increasePositionCommand.execute(
            new IncreasePositionCommand.Input(
                new WalletOwnerId(412L), Currency.USD, new BigDecimal("11.00"),
                transactionId, TRANSACTION_AT, "Increase position"));

        assertEquals(history.positionUpdateId(), output.positionUpdateId());
        assertEquals(new PositionId(walletId.getId()), output.positionId());
        assertEquals(PositionAction.INCREASE, output.action());
        assertEquals(new BigDecimal("16.00"), output.newPosition());
    }

}
