/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CreateFspCommandIT extends BaseDomainIT {

    @Autowired
    private CreateFspCommand createFsp;

    @Autowired
    private CreateHubCommand createHub;

    @Test
    void should_create_fsp_with_supported_currencies_and_endpoints() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD, Currency.TZS});
        this.createHub.execute(hubInput);

        var endpoints = new CreateFspCommand.Input.Endpoint[]{
            new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://fsp.example/parties"),
            new CreateFspCommand.Input.Endpoint(EndpointType.TRANSFERS, "http://fsp.example/transfers")
        };

        var input = new CreateFspCommand.Input(new FspCode("DFSP1"), "FSP One",
            new Currency[]{Currency.USD, Currency.TZS}, endpoints);

        // Act
        var output = this.createFsp.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.fspId());
    }

    @Test
    void should_throw_when_hub_not_found() {

        // Arrange
        var endpoints = new CreateFspCommand.Input.Endpoint[]{
            new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://fsp.example/parties")
        };

        var input = new CreateFspCommand.Input(new FspCode("DFSP2"), "FSP Two",
            new Currency[]{Currency.USD}, endpoints);

        // Act & Assert
        Assertions.assertThrows(HubNotFoundException.class, () -> this.createFsp.execute(input));
    }

    @Test
    void should_throw_when_fsp_code_already_exists() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var endpoints = new CreateFspCommand.Input.Endpoint[]{
            new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://fsp.example/parties")
        };

        var input1 = new CreateFspCommand.Input(new FspCode("DFSP3"), "FSP Three",
            new Currency[]{Currency.USD}, endpoints);

        var input2 = new CreateFspCommand.Input(new FspCode("DFSP3"), "FSP Three Duplicate",
            new Currency[]{Currency.USD}, endpoints);

        // Act
        this.createFsp.execute(input1);

        // Assert
        Assertions.assertThrows(FspCodeAlreadyExistsException.class, () -> this.createFsp.execute(input2));
    }

    @Test
    void should_throw_when_currency_not_supported_by_hub() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var endpoints = new CreateFspCommand.Input.Endpoint[]{
            new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://fsp.example/parties")
        };

        var input = new CreateFspCommand.Input(new FspCode("DFSP4"), "FSP Four",
            new Currency[]{Currency.TZS}, endpoints);

        // Act & Assert
        Assertions.assertThrows(FspCurrencyNotSupportedByHubException.class, () -> this.createFsp.execute(input));
    }

    @Test
    void should_throw_when_duplicate_endpoint_type_in_input() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var endpoints = new CreateFspCommand.Input.Endpoint[]{
            new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://fsp.example/parties1"),
            new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://fsp.example/parties2")
        };

        var input = new CreateFspCommand.Input(new FspCode("DFSP5"), "FSP Five",
            new Currency[]{Currency.USD}, endpoints);

        // Act & Assert
        Assertions.assertThrows(FspEndpointAlreadyConfiguredException.class, () -> this.createFsp.execute(input));
    }
}
