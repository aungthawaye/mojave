package org.mojave.core.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.position.FailedToFulfilPositionsException;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
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
@DisplayName("Fulfil Positions Command Integration Test")
public class FulfilPositionsCommandIT extends BaseIT {

    private static final Instant TRANSACTION_AT = Instant.parse("2026-01-11T11:20:35Z");

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private FulfilPositionsCommand fulfilPositionsCommand;

    @Autowired
    private ReservePositionCommand reservePositionCommand;

    @Test
    @DisplayName("Throw when fulfilling positions for missing payee wallet")
    public void payeePositionNotExist() {

        final var reservationId = new PositionUpdateId(40601L);

        final var exception = assertThrows(
            WalletNotFoundException.class, () -> this.fulfilPositionsCommand.execute(
                new FulfilPositionsCommand.Input(
                    reservationId, new WalletOwnerId(406L), Currency.USD, WalletPurpose.ANY,
                    "Fulfil missing payee")));

        assertEquals(new WalletOwnerId(406L), exception.getWalletOwnerId());
        assertEquals(Currency.USD, exception.getCurrency());
        assertEquals(WalletPurpose.ANY, exception.getPurpose());
    }

    @Test
    @DisplayName("Fail to fulfil positions when engine fails")
    public void failed() {

        final var reservationId = new PositionUpdateId(40701L);

        this.createDefaultWallet(this.createWalletCommand, 407L, Currency.USD, "Payee Wallet");

        final var exception = assertThrows(
            FailedToFulfilPositionsException.class, () -> this.fulfilPositionsCommand.execute(
                new FulfilPositionsCommand.Input(
                    reservationId, new WalletOwnerId(407L), Currency.USD, WalletPurpose.ANY,
                    "Fulfil positions")));

        assertEquals(reservationId, exception.getReservationId());
    }

    @Test
    @DisplayName("Fulfil positions successfully")
    public void successful() throws
                             FailedToFulfilPositionsException,
                             NoPositionUpdateForTransactionException,
                             PositionLimitExceededException,
                             WalletNotFoundException {

        final var payerWalletId = this.createDefaultWallet(
            this.createWalletCommand, 408L, Currency.USD, "Payer Wallet");
        final var payeeWalletId = this.createDefaultWallet(
            this.createWalletCommand, 409L, Currency.USD, "Payee Wallet");

        this.updateWalletEngineSnapshot(
            payerWalletId, BigDecimal.ZERO, new BigDecimal("10.00"), BigDecimal.ZERO,
            new BigDecimal("30.00"));
        this.updateWalletEngineSnapshot(
            payeeWalletId, BigDecimal.ZERO, new BigDecimal("50.00"), BigDecimal.ZERO,
            new BigDecimal("80.00"));

        final var reservation = this.reservePositionCommand.execute(
            new ReservePositionCommand.Input(
                new WalletOwnerId(408L), Currency.USD, WalletPurpose.ANY, new BigDecimal("4.00"),
                new TransactionId(40801L), TRANSACTION_AT, "Reserve for fulfilment"));

        final var output = this.fulfilPositionsCommand.execute(
            new FulfilPositionsCommand.Input(
                reservation.positionUpdateId(), new WalletOwnerId(409L), Currency.USD,
                WalletPurpose.ANY,
                "Fulfil positions"));

        assertNotNull(output.payerCommitId());
        assertNotNull(output.payeeCommitId());
    }

}
