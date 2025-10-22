/*-
 * ================================================================================
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
 * ================================================================================
 */

package io.mojaloop.core.participant.domain.command.hub;

import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.participant.contract.command.hub.AddHubCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.hub.HubCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AddHubCurrencyCommandIT extends BaseDomainIT {

    @Autowired
    private CreateHubCommand createHub;

    @Autowired
    private AddHubCurrencyCommand addCurrency;

    @Test
    void should_add_currency_to_existing_hub() throws Exception {

        // Arrange
        var created = this.createHub.execute(new CreateHubCommand.Input("Hub-1", new Currency[]{}));
        var input = new AddHubCurrencyCommand.Input(created.hubId(), Currency.TZS);

        // Act
        var output = this.addCurrency.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.hubCurrencyId());
    }

    @Test
    void should_throw_when_hub_not_found() {

        // Arrange
        var input = new AddHubCurrencyCommand.Input(new HubId(), Currency.TZS);

        // Act & Assert
        Assertions.assertThrows(HubNotFoundException.class, () -> this.addCurrency.execute(input));
    }

    @Test
    void should_throw_when_currency_already_supported() throws Exception {

        // Arrange
        var created = this.createHub.execute(new CreateHubCommand.Input("Hub-1", new Currency[]{Currency.USD}));
        var input = new AddHubCurrencyCommand.Input(created.hubId(), Currency.USD);

        // Act
        this.addCurrency.execute(input);

        // Assert
        Assertions.assertThrows(HubCurrencyAlreadySupportedException.class, () -> this.addCurrency.execute(input));
    }
}
