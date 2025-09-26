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

import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.ActivateFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ActivateFspCurrencyCommandIT extends BaseDomainIT {

    @Autowired
    private ActivateFspCurrencyCommand activateCurrency;

    @Autowired
    private CreateFspCommand createFsp;

    @Autowired
    private CreateHubCommand createHub;

    @Test
    void should_activate_fsp_currency() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var fspOutput = this.createFsp.execute(new CreateFspCommand.Input(new FspCode("DFSP20"), "FSP Twenty",
            new Currency[]{Currency.USD}, new CreateFspCommand.Input.Endpoint[]{}));

        var input = new ActivateFspCurrencyCommand.Input(fspOutput.fspId(), Currency.USD);

        // Act & Assert (should not throw)
        this.activateCurrency.execute(input);
    }

    @Test
    void should_throw_when_fsp_not_found_on_activation() {

        // Arrange
        var input = new ActivateFspCurrencyCommand.Input(null, Currency.USD);

        // Act & Assert
        Assertions.assertThrows(FspIdNotFoundException.class, () -> this.activateCurrency.execute(input));
    }

    @Test
    void should_throw_when_currency_not_supported_by_hub_on_activation() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var fspOutput = this.createFsp.execute(new CreateFspCommand.Input(new FspCode("DFSP21"), "FSP Twenty One",
            new Currency[]{Currency.USD}, new CreateFspCommand.Input.Endpoint[]{}));

        var input = new ActivateFspCurrencyCommand.Input(fspOutput.fspId(), Currency.TZS);

        // Act & Assert
        Assertions.assertThrows(FspCurrencyNotSupportedByHubException.class, () -> this.activateCurrency.execute(input));
    }

    @Test
    void should_throw_when_hub_not_found_on_activation() {

        // Arrange
        var input = new ActivateFspCurrencyCommand.Input(new io.mojaloop.core.common.datatype.identifier.participant.FspId(999999L), Currency.USD);

        // Act & Assert
        Assertions.assertThrows(HubNotFoundException.class, () -> this.activateCurrency.execute(input));
    }
}
