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
/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.AddFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AddFspCurrencyCommandIT extends BaseDomainIT {

    @Autowired
    private AddFspCurrencyCommand addCurrency;

    @Autowired
    private CreateFspCommand createFsp;

    @Autowired
    private CreateHubCommand createHub;

    @Test
    void should_add_currency_to_fsp() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD, Currency.TZS});
        this.createHub.execute(hubInput);

        var fspOutput = this.createFsp.execute(new CreateFspCommand.Input(new FspCode("DFSP10"), "FSP Ten",
                                                                          new Currency[]{Currency.USD}, new CreateFspCommand.Input.Endpoint[]{}));

        var input = new AddFspCurrencyCommand.Input(fspOutput.fspId(), Currency.TZS);

        // Act
        var output = this.addCurrency.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.fspCurrencyId());
    }

    @Test
    void should_throw_when_currency_already_supported() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var fspOutput = this.createFsp.execute(new CreateFspCommand.Input(new FspCode("DFSP12"), "FSP Twelve",
                                                                          new Currency[]{Currency.USD}, new CreateFspCommand.Input.Endpoint[]{}));

        var input = new AddFspCurrencyCommand.Input(fspOutput.fspId(), Currency.USD);

        // Act
        this.addCurrency.execute(input);

        // Assert
        Assertions.assertThrows(FspCurrencyAlreadySupportedException.class, () -> this.addCurrency.execute(input));
    }

    @Test
    void should_throw_when_currency_not_supported_by_hub() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var fspOutput = this.createFsp.execute(new CreateFspCommand.Input(new FspCode("DFSP11"), "FSP Eleven",
                                                                          new Currency[]{Currency.USD}, new CreateFspCommand.Input.Endpoint[]{}));

        var input = new AddFspCurrencyCommand.Input(fspOutput.fspId(), Currency.TZS);

        // Act & Assert
        Assertions.assertThrows(FspCurrencyNotSupportedByHubException.class, () -> this.addCurrency.execute(input));
    }

    @Test
    void should_throw_when_fsp_not_found() throws Exception {

        // Arrange
        var hubInput = new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD});
        this.createHub.execute(hubInput);

        var input = new AddFspCurrencyCommand.Input(new FspId(999998L), Currency.USD);

        // Act & Assert
        Assertions.assertThrows(FspIdNotFoundException.class, () -> this.addCurrency.execute(input));
    }

}
