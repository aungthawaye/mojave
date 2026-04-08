package org.mojave.wallet.domain.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.exception.balance.BalanceIdNotFoundException;
import org.mojave.wallet.contract.exception.position.PositionIdNotFoundException;
import org.mojave.wallet.contract.query.BalanceQuery;
import org.mojave.wallet.contract.query.PositionQuery;
import org.mojave.wallet.domain.BaseIT;
import org.mojave.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.wallet.domain.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletDomainTestConfiguration.class})
@DisplayName("Wallet Queries Integration Test")
public class WalletQueryIT extends BaseIT {

    @Autowired
    private BalanceQuery balanceQuery;

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private PositionQuery positionQuery;

    @Test
    @DisplayName("Throw when getting balance by missing id")
    public void balanceGetByIdNotFound() {

        assertThrows(
            BalanceIdNotFoundException.class,
            () -> this.balanceQuery.get(new WalletId(Long.MAX_VALUE)));
    }

    @Test
    @DisplayName("Get balance by id owner currency and all wallets")
    public void balanceGetSuccessful() {

        final var defaultWalletId = this.createWallet(
            this.createWalletCommand, 201L, Currency.USD, Wallet.DEFAULT_SCENARIO,
            "Balance Default Wallet");

        this.createWallet(
            this.createWalletCommand, 201L, Currency.USD, "RESERVATION",
            "Balance Reservation Wallet");

        final var byId = this.balanceQuery.get(new WalletId(defaultWalletId.getId()));
        final var byOwner = this.balanceQuery.get(new WalletOwnerId(201L), Currency.USD);
        final var all = this.balanceQuery.getAll();

        assertEquals(defaultWalletId, byId.walletId());
        assertEquals(1, byOwner.size());
        assertEquals(defaultWalletId, byOwner.getFirst().walletId());
        assertEquals(1, all.size());
        assertEquals(defaultWalletId, all.getFirst().walletId());
    }

    @Test
    @DisplayName("Throw when getting position by missing id")
    public void positionGetByIdNotFound() {

        assertThrows(
            PositionIdNotFoundException.class,
            () -> this.positionQuery.get(new WalletId(Long.MAX_VALUE)));
    }

    @Test
    @DisplayName("Get position by id owner currency and all wallets")
    public void positionGetSuccessful() {

        final var defaultWalletId = this.createWallet(
            this.createWalletCommand, 202L, Currency.MMK, Wallet.DEFAULT_SCENARIO,
            "Position Default Wallet");

        this.createWallet(
            this.createWalletCommand, 202L, Currency.MMK, "LIMIT",
            "Position Limit Wallet");

        final var byId = this.positionQuery.get(new WalletId(defaultWalletId.getId()));
        final var byOwner = this.positionQuery.get(new WalletOwnerId(202L), Currency.MMK);
        final var all = this.positionQuery.getAll();

        assertEquals(defaultWalletId, byId.walletId());
        assertEquals(1, byOwner.size());
        assertEquals(defaultWalletId, byOwner.getFirst().walletId());
        assertEquals(1, all.size());
        assertEquals(defaultWalletId, all.getFirst().walletId());
    }

}
