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

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.AddEndpointCommand;
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
public class AddFspEndpointCommandIT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private AddEndpointCommand addEndpointCommand;

    @Test
    public void addEndpoint_fsp_not_found_throws() {

        assertNotNull(addEndpointCommand);
        var nonExistingFspId = new FspId(-12345L);
        var input = new AddEndpointCommand.Input(nonExistingFspId, EndpointType.TRANSFERS, "https://t.example.com");
        assertThrows(FspIdNotFoundException.class, () -> addEndpointCommand.execute(input));
    }

    @Test
    public void addEndpoint_success_and_duplicate_throws() throws Exception {

        assertNotNull(createFspCommand);
        assertNotNull(addEndpointCommand);

        var created = createSampleFsp("add-ep-1");
        var fspId = created.fspId();

        // First add should succeed
        var input1 = new AddEndpointCommand.Input(fspId, EndpointType.QUOTES, "https://quotes.example.com");
        var output1 = addEndpointCommand.execute(input1);
        assertNotNull(output1);

        // Second add of the same type should throw EndpointAlreadyConfiguredException
        var input2 = new AddEndpointCommand.Input(fspId, EndpointType.QUOTES, "https://quotes.other.com");
        assertThrows(FspEndpointAlreadyConfiguredException.class, () -> addEndpointCommand.execute(input2));
    }

    private CreateFspCommand.Output createSampleFsp(String code)
        throws FspCurrencyAlreadySupportedException, FspEndpointAlreadyConfiguredException, FspCodeAlreadyExistsException {

        var input = new CreateFspCommand.Input(
            new FspCode(code),
            "FSP-" + code,
            new Currency[]{Currency.USD},
            new CreateFspCommand.Input.Endpoint[]{});
        return this.createFspCommand.execute(input);
    }

}
