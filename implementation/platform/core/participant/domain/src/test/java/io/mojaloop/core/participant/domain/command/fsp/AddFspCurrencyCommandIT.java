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

import io.mojaloop.core.common.datatype.identifier.participant.FspCurrencyId;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.AddFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class AddFspCurrencyCommandIT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private AddFspCurrencyCommand addFspCurrencyCommand;

    @Test
    public void addCurrency_success_duplicate_and_fspNotFound() throws Exception {

        assertNotNull(createFspCommand);
        assertNotNull(addFspCurrencyCommand);

        var created = createSampleFsp("add-cur-1");
        var fspId = created.fspId();

        var out1 = addFspCurrencyCommand.execute(new AddFspCurrencyCommand.Input(fspId, Currency.MMK));
        assertNotNull(out1);
        FspCurrencyId scId = out1.fspCurrencyId();
        assertNotNull(scId);

        assertThrows(
            FspCurrencyAlreadySupportedException.class,
            () -> addFspCurrencyCommand.execute(new AddFspCurrencyCommand.Input(fspId, Currency.MMK)));

        var nonExisting = new FspId(-4321L);
        assertThrows(FspIdNotFoundException.class, () -> addFspCurrencyCommand.execute(new AddFspCurrencyCommand.Input(nonExisting, Currency.MYR)));
    }

    private CreateFspCommand.Output createSampleFsp(String code)
        throws FspCurrencyAlreadySupportedException, FspEndpointAlreadyConfiguredException, FspCodeAlreadyExistsException {

        var input = new CreateFspCommand.Input(new FspCode(code), "FSP-" + code, new Currency[]{Currency.USD}, new CreateFspCommand.Input.Endpoint[]{});
        return this.createFspCommand.execute(input);
    }

}
