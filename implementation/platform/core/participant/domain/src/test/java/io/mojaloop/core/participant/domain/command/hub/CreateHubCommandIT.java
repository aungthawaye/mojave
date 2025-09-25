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

import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.hub.HubCountLimitReachedException;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.core.participant.domain.repository.HubRepository;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateHubCommandIT {

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private HubRepository hubRepository;

    @Test
    public void createHub_success_persistsAndReturnsId() throws FspCurrencyAlreadySupportedException, HubCountLimitReachedException {

        assertNotNull(createHubCommand);

        CreateHubCommand.Input input = new CreateHubCommand.Input("The Hub", new Currency[]{Currency.USD, Currency.MMK});

        var output = this.createHubCommand.execute(input);

        assertNotNull(output);
        assertNotNull(output.hubId());
        var saved = hubRepository.findById(output.hubId());
        assertTrue(saved.isPresent());
        assertEquals("The Hub", saved.get().convert().name());
        assertEquals(2, saved.get().getCurrencies().size());
    }

    @Test
    public void creatingSecondHub_throwsHubLimitReachedException() throws FspCurrencyAlreadySupportedException, HubCountLimitReachedException {
        // create first hub
        this.createHubCommand.execute(new CreateHubCommand.Input("Hub A", new Currency[]{Currency.USD}));
        // attempt second
        assertThrows(HubCountLimitReachedException.class, () -> this.createHubCommand.execute(new CreateHubCommand.Input("Hub B", new Currency[]{Currency.MMK})));
    }

    @Test
    public void duplicateCurrencyInInput_throwsCurrencyAlreadySupportedException() {

        CreateHubCommand.Input bad = new CreateHubCommand.Input("Dup currency", new Currency[]{Currency.USD, Currency.USD});
        assertThrows(FspCurrencyAlreadySupportedException.class, () -> this.createHubCommand.execute(bad));
    }

}
