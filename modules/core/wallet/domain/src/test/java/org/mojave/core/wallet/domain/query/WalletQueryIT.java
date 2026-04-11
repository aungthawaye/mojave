package org.mojave.core.wallet.domain.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.scheme.rule.wallet.WalletPurpose;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.BalanceIdNotFoundException;
import org.mojave.core.wallet.contract.query.WalletQuery;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.core.wallet.domain.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @DisplayName("Get wallet by id owner currency purpose")
    public void getByIdOwnerCurrencyPurposeSuccessful() {

        final var anyWalletId = this.createWallet(
            this.createWalletCommand, 201L, Currency.USD, Wallet.DEFAULT_PURPOSE,
            "Default Wallet");

        final var p2pWalletId = this.createWallet(
            this.createWalletCommand, 201L, Currency.USD, WalletPurpose.P2P,
            "P2P Wallet");

        final var byId = this.walletQuery.get(new WalletId(anyWalletId.getId()));
        final var byOwner = this.walletQuery.get(new WalletOwnerId(201L));
        final var byOwnerCurrency = this.walletQuery.get(new WalletOwnerId(201L), Currency.USD);
        final var byOwnerCurrencyPurpose = this.walletQuery.get(
            new WalletOwnerId(201L), Currency.USD, WalletPurpose.P2P);
        final var all = this.walletQuery.getAll();

        assertEquals(anyWalletId, byId.walletId());
        assertEquals(2, byOwner.size());
        assertEquals(2, byOwnerCurrency.size());
        assertEquals(p2pWalletId, byOwnerCurrencyPurpose.walletId());
        assertEquals(2, all.size());

        assertTrue(byOwner.stream().anyMatch(w -> w.walletId().equals(anyWalletId)));
        assertTrue(byOwner.stream().anyMatch(w -> w.walletId().equals(p2pWalletId)));
        assertTrue(byOwnerCurrency.stream().anyMatch(w -> w.walletId().equals(anyWalletId)));
        assertTrue(byOwnerCurrency.stream().anyMatch(w -> w.walletId().equals(p2pWalletId)));
    }

    @Test
    @DisplayName("Throw when getting wallet by missing owner currency and purpose")
    public void getByOwnerCurrencyPurposeNotFound() {

        assertThrows(
            WalletNotFoundException.class,
            () -> this.walletQuery.get(new WalletOwnerId(999L), Currency.MMK, WalletPurpose.P2P));
    }

}
