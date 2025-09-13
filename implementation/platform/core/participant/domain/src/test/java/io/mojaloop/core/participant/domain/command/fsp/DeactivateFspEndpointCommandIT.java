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
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateEndpointCommand;
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
public class DeactivateFspEndpointCommandIT {

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
        deactivateEndpointCommand.execute(new DeactivateEndpointCommand.Input(fspId, EndpointType.PARTIES));

        // Second time should be idempotent
        deactivateEndpointCommand.execute(new DeactivateEndpointCommand.Input(fspId, EndpointType.PARTIES));

        // Missing endpoint -> should not throw
        deactivateEndpointCommand.execute(new DeactivateEndpointCommand.Input(fspId, EndpointType.QUOTES));

        // Non-existent FSP -> FspIdNotFoundException
        var nonExisting = new FspId(-2222L);
        assertThrows(
            FspIdNotFoundException.class,
            () -> deactivateEndpointCommand.execute(new DeactivateEndpointCommand.Input(nonExisting, EndpointType.TRANSFERS)));
    }

    private CreateFspCommand.Output createFspWithEndpoint(String code, EndpointType type, String baseUrl)
        throws FspCurrencyAlreadySupportedException, FspEndpointAlreadyConfiguredException, FspCodeAlreadyExistsException {

        var input = new CreateFspCommand.Input(
            new FspCode(code),
            "FSP-" + code,
            new Currency[]{Currency.USD},
            new CreateFspCommand.Input.Endpoint[]{
                new CreateFspCommand.Input.Endpoint(type, baseUrl)
            });
        return createFspCommand.execute(input);
    }

}
