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

import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

class DeactivateFspCurrencyCommandIT extends BaseDomainIT {

    @Autowired
    private DeactivateFspCurrencyCommand deactivateCurrency;

    @Autowired
    private CreateFspCommand createFsp;

    @Autowired
    private CreateHubCommand createHub;

    @Test
    void should_deactivate_fsp_currency() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var fspOutput = this.createFsp.execute(new CreateFspCommand.Input(new FspCode("DFSP30"), "FSP Thirty",
            new Currency[]{Currency.USD}, new CreateFspCommand.Input.Endpoint[]{}));

        var input = new DeactivateFspCurrencyCommand.Input(fspOutput.fspId(), Currency.USD);

        // Act & Assert (should not throw)
        this.deactivateCurrency.execute(input);
    }

    @Test
    void should_throw_when_fsp_not_found_on_deactivation() {

        // Arrange
        var input = new DeactivateFspCurrencyCommand.Input(new io.mojaloop.core.common.datatype.identifier.participant.FspId(999997L), Currency.USD);

        // Act & Assert
        Assertions.assertThrows(FspIdNotFoundException.class, () -> this.deactivateCurrency.execute(input));
    }
}
