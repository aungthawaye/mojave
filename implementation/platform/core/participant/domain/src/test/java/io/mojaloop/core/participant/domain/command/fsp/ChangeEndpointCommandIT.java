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
import io.mojaloop.core.participant.contract.command.fsp.ChangeEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.EndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.FspCodeAlreadyExistsException;
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
public class ChangeEndpointCommandIT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private ChangeEndpointCommand changeEndpointCommand;

    @Test
    public void changeEndpoint_success_changed_and_noop_when_same_or_missing() throws Exception {

        assertNotNull(createFspCommand);
        assertNotNull(changeEndpointCommand);

        var created = createFspWithEndpoint("chg-ep-1", EndpointType.TRANSFERS, "https://a.example.com");
        var fspId = created.fspId();

        var out1 = changeEndpointCommand.execute(new ChangeEndpointCommand.Input(fspId, EndpointType.TRANSFERS, "https://b.example.com"));
        assertTrue(out1.changed());

        var out2 = changeEndpointCommand.execute(new ChangeEndpointCommand.Input(fspId, EndpointType.TRANSFERS, "https://b.example.com"));
        assertFalse(out2.changed());

        var nonExisting = new FspId(-1L);
        var out3 = changeEndpointCommand.execute(
            new ChangeEndpointCommand.Input(nonExisting, EndpointType.TRANSFERS, "https://c.example.com"));
        assertFalse(out3.changed());
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
