/*-
 * =============================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =============================================================================
 */

package io.mojaloop.core.wallet.domain.command.wallet;

import io.mojaloop.core.wallet.contract.command.wallet.CreateWalletCommand;
import io.mojaloop.core.wallet.contract.exception.wallet.WalletAlreadyExistsException;
import io.mojaloop.core.wallet.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateWalletCommandIT extends BaseDomainIT {

    @Autowired
    private CreateWalletCommand createWalletCommand;

    @Test
    void should_create_wallet_successfully() {
        // Arrange
        final var input = new CreateWalletCommand.Input(new WalletOwnerId(81001L), Currency.USD, "Main Wallet");

        // Act
        final var output = this.createWalletCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.walletId());
    }

    @Test
    void should_fail_when_wallet_already_exists_for_owner_and_currency() {
        // Arrange
        final var ownerId = new WalletOwnerId(81002L);
        final var currency = Currency.USD;
        this.createWalletCommand.execute(new CreateWalletCommand.Input(ownerId, currency, "Wallet A"));
        final var input = new CreateWalletCommand.Input(ownerId, currency, "Wallet B");

        // Act & Assert
        assertThrows(WalletAlreadyExistsException.class, () -> this.createWalletCommand.execute(input));
    }
}
