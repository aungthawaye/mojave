package org.mojave.core.wallet.domain.command.ndc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.ndc.DecreaseNdcCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.ndc.PositionReservedExceedsNdcException;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletDomainTestConfiguration.class})
@DisplayName("Decrease NDC Command Integration Test")
public class DecreaseNdcCommandIT extends BaseIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private DecreaseNdcCommand decreaseNdcCommand;

    @Test
    @DisplayName("Throw when position plus reserved exceeds new NDC")
    public void positionReservedExceedsNdc() {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 505L, Currency.USD, "Decrease NDC Wallet");

        this.updateWalletEngineSnapshot(
            walletId, new BigDecimal("100.00"), new BigDecimal("35.00"), new BigDecimal("10.00"),
            new BigDecimal("60.00"));

        final var exception = assertThrows(
            PositionReservedExceedsNdcException.class,
            () -> this.decreaseNdcCommand.execute(new DecreaseNdcCommand.Input(
                walletId, new BigDecimal("20.00"), new TransactionId(Snowflake.get().nextId()),
                Instant.now(), "")));

        assertEquals(walletId, exception.getWalletId());
        assertEquals(0, exception.getAmount().compareTo(new BigDecimal("20.00")));
        assertEquals(0, exception.getPosition().compareTo(new BigDecimal("35.00")));
        assertEquals(0, exception.getReserved().compareTo(new BigDecimal("10.00")));
        assertEquals(0, exception.getNewNdc().compareTo(new BigDecimal("40.00")));
        assertNotNull(exception.getTransactionId());
    }

    @Test
    @DisplayName("Decrease NDC successfully")
    public void successful() throws
                             NoBalanceUpdateForTransactionException,
                             PositionReservedExceedsNdcException,
                             WalletNotFoundException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 506L, Currency.USD, "Decrease NDC Wallet");

        this.updateWalletEngineSnapshot(
            walletId, new BigDecimal("100.00"), new BigDecimal("20.00"),
            new BigDecimal("5.00"), new BigDecimal("60.00"));

        final var output = this.decreaseNdcCommand.execute(
            new DecreaseNdcCommand.Input(
                walletId, new BigDecimal("10.00"), new TransactionId(Snowflake.get().nextId()),
                Instant.now(), "Decrease NDC for test"));

        assertNotNull(output.ndcUpdateId());

        final var snapshot = this.loadNdcUpdateSnapshot(output.ndcUpdateId());

        assertEquals("DECREASE", snapshot.action());
        assertEquals(0, snapshot.amount().compareTo(new BigDecimal("10.00")));
        assertEquals(0, snapshot.oldNdc().compareTo(new BigDecimal("60.00")));
        assertEquals(0, snapshot.newNdc().compareTo(new BigDecimal("50.00")));
        assertEquals("Decrease NDC for test", snapshot.description());
    }

    @Test
    @DisplayName("Throw when decreasing NDC for missing wallet")
    public void walletNotFound() {

        final var exception = assertThrows(
            WalletNotFoundException.class, () -> this.decreaseNdcCommand.execute(
                new DecreaseNdcCommand.Input(
                    new WalletId(50401L), new BigDecimal("10.00"),
                    new TransactionId(Snowflake.get().nextId()), Instant.now(), "")));

        assertEquals(new WalletId(50401L), exception.getWalletId());
    }

}
