package org.mojave.wallet.domain.command.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.PositionUpdateId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.wallet.contract.command.CreateWalletCommand;
import org.mojave.wallet.contract.command.position.FulfilPositionsCommand;
import org.mojave.wallet.contract.exception.position.FailedToFulfilPositionsException;
import org.mojave.wallet.contract.exception.position.PositionNotExistException;
import org.mojave.wallet.contract.engine.WalletEngine;
import org.mojave.wallet.domain.BaseIT;
import org.mojave.wallet.domain.WalletDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletDomainTestConfiguration.class})
@DisplayName("Fulfil Positions Command Integration Test")
public class FulfilPositionsCommandIT extends BaseIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Autowired
    private FulfilPositionsCommand fulfilPositionsCommand;

    @Test
    @DisplayName("Throw when fulfilling positions for missing payee wallet")
    public void payeePositionNotExist() {

        final var reservationId = new PositionUpdateId(40601L);

        final var exception = assertThrows(
            PositionNotExistException.class, () -> this.fulfilPositionsCommand.execute(
                new FulfilPositionsCommand.Input(
                    reservationId, new WalletOwnerId(406L), Currency.USD,
                    "Fulfil missing payee")));

        assertEquals(new WalletOwnerId(406L), exception.getWalletOwnerId());
        assertEquals(Currency.USD, exception.getCurrency());
    }

    @Test
    @DisplayName("Fail to fulfil positions when engine fails")
    public void failed() {

        final var reservationId = new PositionUpdateId(40701L);

        this.createDefaultWallet(this.createWalletCommand, 407L, Currency.USD, "Payee Wallet");
        this.testWalletEngine.failNextFulfil(
            new WalletEngine.NoPositionFulfilmentException(reservationId));

        final var exception = assertThrows(
            FailedToFulfilPositionsException.class, () -> this.fulfilPositionsCommand.execute(
                new FulfilPositionsCommand.Input(
                    reservationId, new WalletOwnerId(407L), Currency.USD,
                    "Fulfil positions")));

        assertEquals(reservationId, exception.getReservationId());
    }

    @Test
    @DisplayName("Fulfil positions successfully")
    public void successful() throws FailedToFulfilPositionsException {

        final var reservationId = new PositionUpdateId(40801L);

        this.createDefaultWallet(this.createWalletCommand, 408L, Currency.USD, "Payee Wallet");
        this.testWalletEngine.completeNextFulfil(
            this.fulfilResult(new PositionUpdateId(40802L), new PositionUpdateId(40803L)));

        final var output = this.fulfilPositionsCommand.execute(
            new FulfilPositionsCommand.Input(
                reservationId, new WalletOwnerId(408L), Currency.USD, "Fulfil positions"));

        assertEquals(new PositionUpdateId(40802L), output.payerCommitId());
        assertEquals(new PositionUpdateId(40803L), output.payeeCommitId());
    }

}
