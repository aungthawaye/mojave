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

import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateEndpointCommand;
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
public class DeactivateEndpointCommandIT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private DeactivateEndpointCommand deactivateEndpointCommand;

    @Test
    public void deactivateEndpoint_behaviors() throws Exception {

        assertNotNull(createFspCommand);
        assertNotNull(deactivateEndpointCommand);

        // Existing endpoint -> deactivated = true
        var created = createFspWithEndpoint("deact-ep-1", EndpointType.PARTIES, "https://p.example.com");
        var fspId = created.fspId();
        var out1 = deactivateEndpointCommand.execute(new DeactivateEndpointCommand.Input(fspId, EndpointType.PARTIES));
        assertTrue(out1.deactivated());

        // Second time still true (implementation deactivates without checking prev state)
        var out2 = deactivateEndpointCommand.execute(new DeactivateEndpointCommand.Input(fspId, EndpointType.PARTIES));
        assertTrue(out2.deactivated());

        // Missing endpoint -> deactivated = false
        var out3 = deactivateEndpointCommand.execute(new DeactivateEndpointCommand.Input(fspId, EndpointType.QUOTES));
        assertFalse(out3.deactivated());

        // Non-existent FSP -> FspIdNotFoundException
        var nonExisting = new FspId(-2222L);
        assertThrows(FspIdNotFoundException.class,
                     () -> deactivateEndpointCommand.execute(new DeactivateEndpointCommand.Input(nonExisting, EndpointType.TRANSFERS)));
    }

    private CreateFspCommand.Output createFspWithEndpoint(String code, EndpointType type, String baseUrl)
        throws CurrencyAlreadySupportedException, EndpointAlreadyConfiguredException, FspCodeAlreadyExistsException {

        var input = new CreateFspCommand.Input(new FspCode(code),
                                               "FSP-" + code,
                                               new Currency[]{Currency.USD},
                                               new CreateFspCommand.Input.Endpoint[]{
                                                   new CreateFspCommand.Input.Endpoint(type, baseUrl)
                                               });
        return createFspCommand.execute(input);
    }

}
