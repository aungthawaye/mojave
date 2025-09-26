/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateEndpointCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DeactivateEndpointCommandIT extends BaseDomainIT {

    @Autowired
    private CreateHubCommand createHub;

    @Autowired
    private CreateFspCommand createFsp;

    @Autowired
    private DeactivateEndpointCommand deactivateEndpoint;

    @Test
    void should_deactivate_existing_endpoint() throws Exception {

        // Arrange
        var createHubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(createHubInput);

        var endpoints = new CreateFspCommand.Input.Endpoint[]{
            new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://fsp.example/parties")
        };

        var createFspInput = new CreateFspCommand.Input(new FspCode("DFSP800"), "FSP-800",
            new Currency[]{Currency.USD}, endpoints);
        var created = this.createFsp.execute(createFspInput);

        var input = new DeactivateEndpointCommand.Input(created.fspId(), EndpointType.PARTIES);

        // Act
        var output = this.deactivateEndpoint.execute(input);

        // Assert
        Assertions.assertNotNull(output);
    }

    @Test
    void should_throw_when_fsp_not_found() {

        // Arrange
        var input = new DeactivateEndpointCommand.Input(new FspId(222_222L), EndpointType.PARTIES);

        // Act & Assert
        Assertions.assertThrows(FspIdNotFoundException.class, () -> this.deactivateEndpoint.execute(input));
    }
}
