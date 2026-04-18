package org.mojave.core.wallet.domain.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.constant.WalletDefaultTag;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.BalanceIdNotFoundException;
import org.mojave.core.wallet.contract.query.WalletQuery;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletDomainTestConfiguration.class})
@DisplayName("Wallet Queries Integration Test")
public class WalletQueryIT extends BaseIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private WalletQuery walletQuery;

    @Test
    @DisplayName("Throw when getting wallet by missing id")
    public void getByIdNotFound() {

        assertThrows(
            BalanceIdNotFoundException.class,
            () -> this.walletQuery.get(new WalletId(Long.MAX_VALUE)));
    }

    @Test
    @DisplayName("Get wallet by id owner currency tag")
    public void getByIdOwnerCurrencyTagSuccessful() {

        final var anyWalletId = this.createWallet(
            this.createWalletCommand, 201L, Currency.USD, WalletDefaultTag.DEFAULT_TAG,
            "Default Wallet");

        final var p2pWalletId = this.createWallet(
            this.createWalletCommand, 201L, Currency.USD,
            "P2P_TRANSFER", "P2P Wallet");

        final var byId = this.walletQuery.get(new WalletId(anyWalletId.getId()));
        final var byOwner = this.walletQuery.get(new WalletOwnerId(201L));
        final var byOwnerCurrency = this.walletQuery.get(new WalletOwnerId(201L), Currency.USD);
        final var byOwnerCurrencyTag = this.walletQuery.get(
            new WalletOwnerId(201L), Currency.USD, "P2P_TRANSFER");
        final var all = this.walletQuery.getAll();

        assertEquals(anyWalletId, byId.walletId());
        assertEquals(2, byOwner.size());
        assertEquals(2, byOwnerCurrency.size());
        assertEquals(p2pWalletId, byOwnerCurrencyTag.walletId());
        assertEquals(2, all.size());

        assertTrue(byOwner.stream().anyMatch(w -> w.walletId().equals(anyWalletId)));
        assertTrue(byOwner.stream().anyMatch(w -> w.walletId().equals(p2pWalletId)));
        assertTrue(byOwnerCurrency.stream().anyMatch(w -> w.walletId().equals(anyWalletId)));
        assertTrue(byOwnerCurrency.stream().anyMatch(w -> w.walletId().equals(p2pWalletId)));
    }

    @Test
    @DisplayName("Throw when getting wallet by missing owner currency and tag")
    public void getByOwnerCurrencyTagNotFound() {

        assertThrows(
            WalletNotFoundException.class,
            () -> this.walletQuery.get(new WalletOwnerId(999L), Currency.MMK, "P2P_TRANSFER"));
    }

}
