package org.mojave.wallet.domain.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.query.BalanceQuery;
import org.mojave.wallet.domain.BaseIT;
import org.mojave.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.wallet.domain.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletDomainTestConfiguration.class})
@DisplayName("Create Wallet Command Integration Test")
public class CreateWalletCommandIT extends BaseIT {

    @Autowired
    private BalanceQuery balanceQuery;

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Test
    @DisplayName("Create wallet successfully")
    public void successful() {

        final var output = this.createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(101L), Currency.USD, Wallet.DEFAULT_SCENARIO,
                "Settlement Wallet"));
        final var wallet = this.balanceQuery.getAll().getFirst();

        assertNotNull(output.walletId());
        assertEquals(output.walletId(), wallet.walletId());
        assertEquals(Currency.USD, wallet.currency());
        assertEquals("Settlement Wallet", wallet.name());
    }

    @Test
    @DisplayName("Return existing wallet when owner currency and scenario already exist")
    public void duplicateWalletReturnsExisting() {

        final var firstOutput = this.createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(102L), Currency.USD, Wallet.DEFAULT_SCENARIO,
                "Primary Wallet"));
        final var secondOutput = this.createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(102L), Currency.USD, Wallet.DEFAULT_SCENARIO,
                "Ignored Wallet Name"));

        assertEquals(firstOutput.walletId(), secondOutput.walletId());
        assertEquals(1, this.balanceQuery.getAll().size());
        assertEquals(1, this.testWalletEngine.createdWalletCount());
    }

    @Test
    @DisplayName("Rollback when wallet engine creation fails")
    public void createWalletEngineFailed() {

        this.testWalletEngine.failNextCreateWallet();

        assertThrows(
            RuntimeException.class, () -> this.createWallet(
                this.createWalletCommand, 103L, Currency.USD, Wallet.DEFAULT_SCENARIO,
                "Failure Wallet"));
        assertEquals(0, this.balanceQuery.getAll().size());
    }

}
