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
import io.mojaloop.core.participant.contract.command.hub.ChangeHubNameCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ChangeHubNameCommandIT extends BaseDomainIT {

    @Autowired
    private CreateHubCommand createHub;

    @Autowired
    private ChangeHubNameCommand changeName;

    @Test
    void should_change_hub_name() throws Exception {

        // Arrange
        var created = this.createHub.execute(new CreateHubCommand.Input("Hub-1", new Currency[]{}));
        var input = new ChangeHubNameCommand.Input(created.hubId(), "Hub-Updated");

        // Act
        var output = this.changeName.execute(input);

        // Assert
        Assertions.assertNotNull(output);
    }

    @Test
    void should_throw_when_hub_not_found() {

        // Arrange
        var input = new ChangeHubNameCommand.Input(new HubId(), "Hub-Updated");

        // Act & Assert
        Assertions.assertThrows(HubNotFoundException.class, () -> this.changeName.execute(input));
    }

}
