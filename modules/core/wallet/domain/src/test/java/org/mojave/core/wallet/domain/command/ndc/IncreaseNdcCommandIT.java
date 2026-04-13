package org.mojave.core.wallet.domain.command.ndc;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.ndc.IncreaseNdcCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.ndc.BalanceLowerThanNewNdcException;
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
@DisplayName("Increase NDC Command Integration Test")
public class IncreaseNdcCommandIT extends BaseIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private IncreaseNdcCommand increaseNdcCommand;

    @Test
    @DisplayName("Throw when balance is lower than new NDC")
    public void balanceLowerThanNewNdc() {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 502L, Currency.USD, "Increase NDC Wallet");

        this.updateWalletEngineSnapshot(
            walletId, new BigDecimal("50.00"), new BigDecimal("20.00"),
            new BigDecimal("5.00"), new BigDecimal("30.00"));

        final var exception = assertThrows(
            BalanceLowerThanNewNdcException.class,
            () -> this.increaseNdcCommand.execute(new IncreaseNdcCommand.Input(
                walletId, new BigDecimal("25.00"), new TransactionId(Snowflake.get().nextId()),
                Instant.now(), "")));

        assertEquals(walletId, exception.getWalletId());
        assertEquals(0, exception.getAmount().compareTo(new BigDecimal("25.00")));
        assertEquals(0, exception.getBalance().compareTo(new BigDecimal("50.00")));
        assertEquals(0, exception.getNewNdc().compareTo(new BigDecimal("55.00")));
        assertNotNull(exception.getTransactionId());
    }

    @Test
    @DisplayName("Increase NDC successfully")
    public void successful() throws
                             NoBalanceUpdateForTransactionException,
                             BalanceLowerThanNewNdcException,
                             WalletNotFoundException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 503L, Currency.USD, "Increase NDC Wallet");

        this.updateWalletEngineSnapshot(
            walletId, new BigDecimal("100.00"), new BigDecimal("20.00"),
            new BigDecimal("5.00"), new BigDecimal("30.00"));

        final var output = this.increaseNdcCommand.execute(
            new IncreaseNdcCommand.Input(
                walletId, new BigDecimal("15.00"), new TransactionId(Snowflake.get().nextId()),
                Instant.now(), ""));

        assertNotNull(output.ndcUpdateId());
    }

    @Test
    @DisplayName("Throw when increasing NDC for missing wallet")
    public void walletNotFound() {

        final var exception = assertThrows(
            WalletNotFoundException.class, () -> this.increaseNdcCommand.execute(
                new IncreaseNdcCommand.Input(
                    new WalletId(50101L), new BigDecimal("10.00"),
                    new TransactionId(Snowflake.get().nextId()), Instant.now(), "")));

        assertEquals(new WalletId(50101L), exception.getWalletId());
    }

}
