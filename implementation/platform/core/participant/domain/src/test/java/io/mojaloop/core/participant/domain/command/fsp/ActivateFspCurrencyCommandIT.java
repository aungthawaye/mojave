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
import io.mojaloop.core.participant.contract.command.fsp.ActivateFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.fsp.CannotActivateFspCurrencyException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
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
public class ActivateFspCurrencyCommandIT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private ActivateFspCurrencyCommand activateFspCurrencyCommand;

    @Autowired
    private io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCommand deactivateFspCommand;

    @Test
    public void activateCurrency_behaviors() throws Exception {

        assertNotNull(createFspCommand);
        assertNotNull(activateFspCurrencyCommand);

        // Existing supported currency + active FSP -> activated = true
        var created = createFspWithCurrencies("act-cur-1", Currency.USD, Currency.KES);
        var fspId = created.fspId();
        activateFspCurrencyCommand.execute(new ActivateFspCurrencyCommand.Input(fspId, Currency.KES));

        // Missing supported currency -> should not throw, but no flag returned
        activateFspCurrencyCommand.execute(new ActivateFspCurrencyCommand.Input(fspId, Currency.MMK));

        // Non-existent FSP -> FspIdNotFoundException
        var nonExisting = new FspId(-654L);
        assertThrows(
            FspIdNotFoundException.class,
            () -> activateFspCurrencyCommand.execute(new ActivateFspCurrencyCommand.Input(nonExisting, Currency.USD)));
    }

    @Test
    public void activateCurrency_inactiveFsp_throws() throws Exception {

        assertNotNull(deactivateFspCommand);
        var created = createFspWithCurrencies("act-cur-2", Currency.USD);
        var fspId = created.fspId();
        deactivateFspCommand.execute(new io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCommand.Input(fspId));
        assertThrows(
            CannotActivateFspCurrencyException.class,
            () -> activateFspCurrencyCommand.execute(new ActivateFspCurrencyCommand.Input(fspId, Currency.USD)));
    }

    private CreateFspCommand.Output createFspWithCurrencies(String code, Currency... currencies)
        throws FspCurrencyAlreadySupportedException, FspEndpointAlreadyConfiguredException, FspCodeAlreadyExistsException {

        var input = new CreateFspCommand.Input(
            new FspCode(code),
            "FSP-" + code,
            currencies,
            new CreateFspCommand.Input.Endpoint[]{});
        return createFspCommand.execute(input);
    }

}
