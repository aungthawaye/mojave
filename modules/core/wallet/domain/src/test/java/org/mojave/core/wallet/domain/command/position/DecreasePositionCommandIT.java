package org.mojave.core.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.PositionAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.position.DecreasePositionCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.domain.BaseIT;
import org.mojave.core.wallet.domain.WalletDomainTestConfiguration;
import org.mojave.scheme.rule.wallet.WalletPurpose;
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
@DisplayName("Decrease Position Command Integration Test")
public class DecreasePositionCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-11T11:20:35Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private DecreasePositionCommand decreasePositionCommand;

    @Test
    @DisplayName("Throw when decreasing missing position")
    public void positionNotExist() {

        final var exception = assertThrows(
            WalletNotFoundException.class, () -> this.decreasePositionCommand.execute(
                new DecreasePositionCommand.Input(
                    new WalletOwnerId(403L), Currency.USD, WalletPurpose.ANY, new BigDecimal("3.00"),
                    new TransactionId(40301L), TRANSACTION_AT, "Decrease missing position")));

        assertEquals(new WalletOwnerId(403L), exception.getWalletOwnerId());
        assertEquals(Currency.USD, exception.getCurrency());
        assertEquals(WalletPurpose.ANY, exception.getPurpose());
    }

    @Test
    @DisplayName("Throw when engine returns no position update")
    public void noPositionUpdate() throws NoPositionUpdateForTransactionException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 404L, Currency.USD, "Decrease Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("20.00"), BigDecimal.ZERO,
            new BigDecimal("50.00"));

        final var transactionId = new TransactionId(40401L);
        final var input = new DecreasePositionCommand.Input(
            new WalletOwnerId(404L), Currency.USD, WalletPurpose.ANY, new BigDecimal("4.00"),
            transactionId, TRANSACTION_AT, "Decrease without update");

        this.decreasePositionCommand.execute(input);

        final var exception = assertThrows(
            NoPositionUpdateForTransactionException.class,
            () -> this.decreasePositionCommand.execute(input));

        assertEquals(transactionId, exception.getTransactionId());
    }

    @Test
    @DisplayName("Decrease position successfully")
    public void successful() throws NoPositionUpdateForTransactionException {

        final var walletId = this.createDefaultWallet(
            this.createWalletCommand, 405L, Currency.USD, "Decrease Wallet");
        this.updateWalletEngineSnapshot(
            walletId, BigDecimal.ZERO, new BigDecimal("20.00"), BigDecimal.ZERO,
            new BigDecimal("50.00"));
        final var transactionId = new TransactionId(40501L);

        final var output = this.decreasePositionCommand.execute(
            new DecreasePositionCommand.Input(
                new WalletOwnerId(405L), Currency.USD, WalletPurpose.ANY, new BigDecimal("4.00"),
                transactionId, TRANSACTION_AT, "Decrease position"));

        assertNotNull(output.positionUpdateId());
        assertEquals(new WalletId(walletId.getId()), output.walletId());
        assertEquals(PositionAction.DECREASE, output.action());
        assertEquals(0, output.oldPosition().compareTo(new BigDecimal("20.00")));
        assertEquals(0, output.newPosition().compareTo(new BigDecimal("16.00")));
    }

}
