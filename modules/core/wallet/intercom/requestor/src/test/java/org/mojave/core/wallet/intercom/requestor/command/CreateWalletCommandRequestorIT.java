package org.mojave.core.wallet.intercom.requestor.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.scheme.rule.wallet.WalletPurpose;
import org.mojave.core.wallet.contract.command.CreateWalletCommand;
import org.mojave.core.wallet.intercom.requestor.WalletIntercomRequestorTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        WalletIntercomRequestorTestConfiguration.class})
@DisplayName("Create Wallet Command Requestor Integration Test")
public class CreateWalletCommandRequestorIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Test
    @DisplayName("Create wallet through requestor successfully")
    public void successful() {

        final var output = this.createWalletCommand.execute(
            new CreateWalletCommand.Input(
                new WalletOwnerId(900001L),
                Currency.USD,
                WalletPurpose.ANY,
                "Requestor Wallet"));

        assertNotNull(output.walletId());
    }

}
