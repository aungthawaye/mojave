package org.mojave.core.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.scheme.rule.enums.wallet.PositionAction;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
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
            WalletNotFoundException.class, () -> this.reservePositionCommand.execute(
                new ReservePositionCommand.Input(
                    new WalletId(41301L),
                    new BigDecimal("2.00"),
                    new TransactionId(41301L), TRANSACTION_AT, "Reserve missing position")));

        assertEquals(new WalletId(41301L), exception.getWalletId());
    }

    @Test
    @DisplayName("Throw when engine returns no position update")
    public void noPositionUpdate() throws
                                   NoPositionUpdateForTransactionException,
                                   PositionLimitExceededException,
                                   WalletNotFoundException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 414L, Currency.USD, "Reserve Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("14.00"), new BigDecimal("1.00"),
            new BigDecimal("20.00"));

        final var transactionId = new TransactionId(41401L);
        final var input = new ReservePositionCommand.Input(
            walletId,
            new BigDecimal("2.00"),
            transactionId, TRANSACTION_AT, "Reserve without update");

        this.reservePositionCommand.execute(input);

        final var exception = assertThrows(
            NoPositionUpdateForTransactionException.class,
            () -> this.reservePositionCommand.execute(input));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Throw when reserve exceeds limit")
    public void positionLimitExceeded() {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 415L, Currency.USD, "Reserve Limit Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("12.00"), new BigDecimal("3.00"),
            new BigDecimal("15.00"));
        final var transactionId = new TransactionId(41501L);

        final var exception = assertThrows(
            PositionLimitExceededException.class, () -> this.reservePositionCommand.execute(
                new ReservePositionCommand.Input(
                    walletId,
                    new BigDecimal("6.00"),
                    transactionId, TRANSACTION_AT, "Reserve too much")));

        assertEquals(new WalletId(walletId.getId()), exception.getWalletId());
        assertEquals(0, exception.getAmount().compareTo(new BigDecimal("6.00")));
        assertEquals(0, exception.getPosition().compareTo(new BigDecimal("12.00")));
        assertEquals(0, exception.getReserved().compareTo(new BigDecimal("3.00")));
        assertEquals(0, exception.getNetDebitCap().compareTo(new BigDecimal("15.00")));
    }

    @Test
    @DisplayName("Reserve position successfully")
    public void successful() throws
                             PositionLimitExceededException,
                             NoPositionUpdateForTransactionException,
                             WalletNotFoundException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 416L, Currency.USD, "Reserve Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("14.00"), new BigDecimal("1.00"),
            new BigDecimal("20.00"));
        final var transactionId = new TransactionId(41601L);

        final var output = this.reservePositionCommand.execute(
            new ReservePositionCommand.Input(
                walletId,
                new BigDecimal("2.50"),
                transactionId, TRANSACTION_AT, "Reserve position"));

        assertNotNull(output.positionUpdateId());
        assertEquals(new WalletId(walletId.getId()), output.walletId());
        assertEquals(PositionAction.RESERVE, output.action());
        assertEquals(0, output.newReserved().compareTo(new BigDecimal("3.50")));
    }

}
