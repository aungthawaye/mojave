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

package io.mojaloop.core.participant.domain.command.hub;

import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.participant.contract.command.hub.ActivateHubCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.AddHubCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.data.HubData;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.hub.HubCountLimitReachedException;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.contract.query.HubQuery;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class ActivateHubCurrencyCommandIT {

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private AddHubCurrencyCommand addHubCurrencyCommand;

    @Autowired
    private ActivateHubCurrencyCommand activateHubCurrencyCommand;

    @Autowired
    private HubQuery hubQuery;

    @Test
    public void activate_flow_succeeds() throws HubCountLimitReachedException, FspCurrencyAlreadySupportedException, HubNotFoundException {

        var created = createHubCommand.execute(new CreateHubCommand.Input("Hub", new Currency[]{Currency.USD}));
        addHubCurrencyCommand.execute(new AddHubCurrencyCommand.Input(created.hubId(), Currency.MMK));

        var actOut = activateHubCurrencyCommand.execute(new ActivateHubCurrencyCommand.Input(created.hubId(), Currency.MMK));
        assertTrue(actOut.activated());

        HubData hubData = hubQuery.get();
        var mmk = java.util.Arrays.stream(hubData.currencies()).filter(c -> c.currency() == Currency.MMK).findFirst().orElseThrow();
        assertEquals(ActivationStatus.ACTIVE, mmk.activationStatus());
    }

}
