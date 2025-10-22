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

import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.hub.HubCountLimitReachedException;
import io.mojaloop.core.participant.contract.exception.hub.HubCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CreateHubCommandIT extends BaseDomainIT {

    @Autowired
    private CreateHubCommand handler;

    @Test
    void should_create_hub_with_supported_currencies() throws Exception {

        // Arrange
        var input = new CreateHubCommand.Input("Hub-1", new Currency[]{Currency.USD, Currency.TZS});

        // Act
        var output = this.handler.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.hubId());
    }

    @Test
    void should_throw_when_hub_already_exists() throws Exception {

        // Arrange
        var input1 = new CreateHubCommand.Input("Hub-1", new Currency[]{Currency.USD});
        var input2 = new CreateHubCommand.Input("Hub-2", new Currency[]{Currency.TZS});

        // Act
        this.handler.execute(input1);

        // Assert
        Assertions.assertThrows(HubCountLimitReachedException.class, () -> this.handler.execute(input2));
    }

    @Test
    void should_allow_empty_supported_currencies() throws Exception {

        // Arrange
        var input = new CreateHubCommand.Input("Hub-Empty", new Currency[]{});

        // Act
        var output = this.handler.execute(input);

        // Assert
        Assertions.assertNotNull(output.hubId());
    }
}
