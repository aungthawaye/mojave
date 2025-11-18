/*-
 * ================================================================================
 * Mojave
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
 * Mojave
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

package io.mojaloop.core.participant.domain.command.hub;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.participant.contract.command.hub.AddHubCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.domain.repository.HubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class AddHubCurrencyCommandHandler implements AddHubCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddHubCurrencyCommandHandler.class);

    private final HubRepository hubRepository;

    public AddHubCurrencyCommandHandler(HubRepository hubRepository) {

        assert hubRepository != null;
        this.hubRepository = hubRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing AddHubCurrencyCommand with input: {}", input);

        var hub = this.hubRepository.findById(input.hubId()).orElseThrow(HubNotFoundException::new);

        var supportedCurrency = hub.addCurrency(input.currency());

        this.hubRepository.save(hub);

        LOGGER.info("Completed AddHubCurrencyCommand with input: {} -> supportedCurrencyId={}", input, supportedCurrency.getId());

        return new Output(supportedCurrency.getId());
    }

}
