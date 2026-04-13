package org.mojave.core.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.scheme.rule.enums.wallet.PositionAction;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.position.IncreasePositionCommand;
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
            WalletNotFoundException.class, () -> this.increasePositionCommand.execute(
                new IncreasePositionCommand.Input(
                    new WalletId(40901L),
                    new BigDecimal("9.00"),
                    new TransactionId(40901L), TRANSACTION_AT, "Increase missing position")));

        assertEquals(new WalletId(40901L), exception.getWalletId());
    }

    @Test
    @DisplayName("Throw when engine returns no position update")
    public void noPositionUpdate() throws
                                   NoPositionUpdateForTransactionException,
                                   PositionLimitExceededException,
                                   WalletNotFoundException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 410L, Currency.USD, "Increase Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("5.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"));

        final var transactionId = new TransactionId(41001L);
        final var input = new IncreasePositionCommand.Input(
            walletId,
            new BigDecimal("9.00"),
            transactionId, TRANSACTION_AT, "Increase without update");

        this.increasePositionCommand.execute(input);

        final var exception = assertThrows(
            NoPositionUpdateForTransactionException.class, () -> this.increasePositionCommand.execute(
                input));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Throw when increase exceeds limit")
    public void positionLimitExceeded() {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 411L, Currency.USD, "Limit Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("90.00"), new BigDecimal("5.00"),
            new BigDecimal("100.00"));
        final var transactionId = new TransactionId(41101L);

        final var exception = assertThrows(
            PositionLimitExceededException.class, () -> this.increasePositionCommand.execute(
                new IncreasePositionCommand.Input(
                    walletId,
                    new BigDecimal("20.00"),
                    transactionId, TRANSACTION_AT, "Increase too much")));

        assertEquals(new WalletId(walletId.getId()), exception.getWalletId());
        assertEquals(0, exception.getAmount().compareTo(new BigDecimal("20.00")));
        assertEquals(0, exception.getPosition().compareTo(new BigDecimal("90.00")));
        assertEquals(0, exception.getReserved().compareTo(new BigDecimal("5.00")));
        assertEquals(0, exception.getNetDebitCap().compareTo(new BigDecimal("100.00")));
        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Increase position successfully")
    public void successful() throws
                             NoPositionUpdateForTransactionException,
                             PositionLimitExceededException,
                             WalletNotFoundException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 412L, Currency.USD, "Increase Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("5.00"), BigDecimal.ZERO,
            new BigDecimal("100.00"));
        final var transactionId = new TransactionId(41201L);

        final var output = this.increasePositionCommand.execute(
            new IncreasePositionCommand.Input(
                walletId,
                new BigDecimal("11.00"),
                transactionId, TRANSACTION_AT, "Increase position"));

        assertNotNull(output.positionUpdateId());
        assertEquals(new WalletId(walletId.getId()), output.walletId());
        assertEquals(PositionAction.INCREASE, output.action());
        assertEquals(0, output.oldPosition().compareTo(new BigDecimal("5.00")));
        assertEquals(0, output.newPosition().compareTo(new BigDecimal("16.00")));
    }

}
