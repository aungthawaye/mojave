package org.mojave.core.wallet.domain.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.query.WalletQuery;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.core.wallet.domain.model.Wallet;
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
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private WalletQuery walletQuery;

    @Test
    @DisplayName("Create wallet successfully")
    public void successful() {

        final var output = this.createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(101L), Currency.USD, Wallet.DEFAULT_TAG,
                "Settlement Wallet"));
        final var wallet = this.walletQuery.getAll().getFirst();

        assertNotNull(output.walletId());
        assertEquals(output.walletId(), wallet.walletId());
        assertEquals(Currency.USD, wallet.currency());
        assertEquals("Settlement Wallet", wallet.name());
    }

    @Test
    @DisplayName("Return existing wallet when owner currency and tag already exist")
    public void duplicateWalletReturnsExisting() {

        final var firstOutput = this.createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(102L), Currency.USD, Wallet.DEFAULT_TAG,
                "Primary Wallet"));
        final var secondOutput = this.createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(102L), Currency.USD, Wallet.DEFAULT_TAG,
                "Ignored Wallet Name"));

        assertEquals(firstOutput.walletId(), secondOutput.walletId());
        assertEquals(1, this.walletQuery.getAll().size());
    }

    @Test
    @DisplayName("Rollback when wallet engine creation fails")
    public void createWalletEngineFailed() {

        this.executeSql("DROP TRIGGER IF EXISTS trg_mwe_wallet_fail_insert");
        this.executeSql("""
            CREATE TRIGGER trg_mwe_wallet_fail_insert
            BEFORE INSERT ON mwe_wallet
            FOR EACH ROW
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Forced wallet engine insert failure'
            """);

        try {

            assertThrows(
                RuntimeException.class, () -> this.createWallet(
                    this.createWalletCommand, 103L, Currency.USD, Wallet.DEFAULT_TAG,
                    "Failure Wallet"));
            assertEquals(0, this.walletQuery.getAll().size());

        } finally {

            this.executeSql("DROP TRIGGER IF EXISTS trg_mwe_wallet_fail_insert");
        }
    }

}
