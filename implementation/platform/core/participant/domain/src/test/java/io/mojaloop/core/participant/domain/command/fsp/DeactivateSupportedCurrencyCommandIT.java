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

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateSupportedCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.EndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.FspCodeAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class DeactivateSupportedCurrencyCommandIT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private DeactivateSupportedCurrencyCommand deactivateSupportedCurrencyCommand;

    @Test
    public void deactivateCurrency_true_then_false_and_fspNotFound() throws Exception {

        assertNotNull(createFspCommand);
        assertNotNull(deactivateSupportedCurrencyCommand);

        var created = createFspWithCurrencies("deact-cur-1", Currency.USD, Currency.MMK);
        var fspId = created.fspId();

        var out1 = deactivateSupportedCurrencyCommand.execute(new DeactivateSupportedCurrencyCommand.Input(fspId, Currency.MMK));
        assertTrue(out1.deactivated());

        var out2 = deactivateSupportedCurrencyCommand.execute(new DeactivateSupportedCurrencyCommand.Input(fspId, Currency.MMK));
        assertFalse(out2.deactivated());

        var nonExisting = new FspId(-3333L);
        assertThrows(FspIdNotFoundException.class, () -> deactivateSupportedCurrencyCommand.execute(
            new DeactivateSupportedCurrencyCommand.Input(nonExisting, Currency.USD)));
    }

    private CreateFspCommand.Output createFspWithCurrencies(String code, Currency... currencies)
        throws CurrencyAlreadySupportedException, EndpointAlreadyConfiguredException, FspCodeAlreadyExistsException {

        var input = new CreateFspCommand.Input(new FspCode(code),
                                               "FSP-" + code,
                                               currencies,
                                               new CreateFspCommand.Input.Endpoint[]{});
        return createFspCommand.execute(input);
    }

}
