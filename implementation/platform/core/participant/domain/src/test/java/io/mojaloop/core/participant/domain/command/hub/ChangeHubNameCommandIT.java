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

import io.mojaloop.core.participant.contract.command.hub.ChangeHubNameCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.hub.HubCountLimitReachedException;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.core.participant.domain.repository.HubRepository;
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
public class ChangeHubNameCommandIT {

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private ChangeHubNameCommand changeHubNameCommand;

    @Autowired
    private HubRepository hubRepository;

    @Test
    public void changeName_success_persists() throws HubCountLimitReachedException, HubNotFoundException, FspCurrencyAlreadySupportedException {

        var created = createHubCommand.execute(new CreateHubCommand.Input("Old Name", new Currency[]{Currency.USD}));
        var hubId = created.hubId();

        changeHubNameCommand.execute(new ChangeHubNameCommand.Input(hubId, "New Name"));

        var saved = hubRepository.findById(hubId);
        assertTrue(saved.isPresent());
        assertEquals("New Name", saved.get().convert().name());
    }

}
