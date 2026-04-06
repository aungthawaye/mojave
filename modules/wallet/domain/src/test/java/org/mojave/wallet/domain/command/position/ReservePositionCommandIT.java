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
import org.mojave.wallet.contract.command.position.ReservePositionCommand;
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
@DisplayName("Reserve Position Command Integration Test")
public class ReservePositionCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-11T11:20:35Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private ReservePositionCommand reservePositionCommand;

    @Test
    @DisplayName("Throw when reserving missing position")
    public void positionNotExist() {

        final var exception = assertThrows(
            PositionNotExistException.class, () -> this.reservePositionCommand.execute(
                new ReservePositionCommand.Input(
                    new WalletOwnerId(413L), Currency.USD, new BigDecimal("2.00"),
                    new TransactionId(41301L), TRANSACTION_AT, "Reserve missing position")));

        assertEquals(new WalletOwnerId(413L), exception.getWalletOwnerId());
        assertEquals(Currency.USD, exception.getCurrency());
    }

    @Test
    @DisplayName("Throw when engine returns no position update")
    public void noPositionUpdate() {

        this.createDefaultWallet(this.createWalletCommand, 414L, Currency.USD, "Reserve Wallet");

        final var transactionId = new TransactionId(41401L);

        this.testWalletEngine.failNextReservePosition(
            new WalletEngine.NoPositionUpdateException(transactionId));

        final var exception = assertThrows(
            NoPositionUpdateForTransactionException.class,
            () -> this.reservePositionCommand.execute(
                new ReservePositionCommand.Input(
                    new WalletOwnerId(414L), Currency.USD, new BigDecimal("2.00"),
                    transactionId, TRANSACTION_AT, "Reserve without update")));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Throw when reserve exceeds limit")
    public void positionLimitExceeded() {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 415L, Currency.USD, "Reserve Limit Wallet");
        final var transactionId = new TransactionId(41501L);

        this.testWalletEngine.failNextReservePosition(
            new WalletEngine.PositionLimitExceededException(
                walletId, new BigDecimal("6.00"), new BigDecimal("12.00"),
                new BigDecimal("3.00"), new BigDecimal("15.00"), transactionId));

        final var exception = assertThrows(
            PositionLimitExceededException.class, () -> this.reservePositionCommand.execute(
                new ReservePositionCommand.Input(
                    new WalletOwnerId(415L), Currency.USD, new BigDecimal("6.00"),
                    transactionId, TRANSACTION_AT, "Reserve too much")));

        assertEquals(new PositionId(walletId.getId()), exception.getPositionId());
        assertEquals(new BigDecimal("6.00"), exception.getAmount());
        assertEquals(new BigDecimal("12.00"), exception.getPosition());
        assertEquals(new BigDecimal("3.00"), exception.getReserved());
        assertEquals(new BigDecimal("15.00"), exception.getNetDebitCap());
    }

    @Test
    @DisplayName("Reserve position successfully")
    public void successful() throws
                             PositionLimitExceededException,
                             NoPositionUpdateForTransactionException,
                             PositionNotExistException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 416L, Currency.USD, "Reserve Wallet");
        final var transactionId = new TransactionId(41601L);
        final var history = this.positionHistory(
            new PositionUpdateId(41602L), walletId, PositionAction.RESERVE, transactionId,
            Currency.USD, new BigDecimal("2.50"), new BigDecimal("14.00"),
            new BigDecimal("14.00"), new BigDecimal("1.00"), new BigDecimal("3.50"),
            new BigDecimal("20.00"), TRANSACTION_AT);

        this.testWalletEngine.completeNextReservePosition(history);

        final var output = this.reservePositionCommand.execute(
            new ReservePositionCommand.Input(
                new WalletOwnerId(416L), Currency.USD, new BigDecimal("2.50"),
                transactionId, TRANSACTION_AT, "Reserve position"));

        assertEquals(history.positionUpdateId(), output.positionUpdateId());
        assertEquals(new PositionId(walletId.getId()), output.positionId());
        assertEquals(PositionAction.RESERVE, output.action());
        assertEquals(new BigDecimal("3.50"), output.newReserved());
    }

}
