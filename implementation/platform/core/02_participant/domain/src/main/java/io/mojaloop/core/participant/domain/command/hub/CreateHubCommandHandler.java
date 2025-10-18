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

package io.mojaloop.core.participant.domain.command.hub;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.core.participant.contract.exception.hub.HubCountLimitReachedException;
import io.mojaloop.core.participant.contract.exception.hub.HubCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.domain.model.hub.Hub;
import io.mojaloop.core.participant.domain.repository.HubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateHubCommandHandler implements CreateHubCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateHubCommandHandler.class);

    private final HubRepository hubRepository;

    public CreateHubCommandHandler(HubRepository hubRepository) {

        assert hubRepository != null;

        this.hubRepository = hubRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing CreateHubCommand with input: {}", input);

        long existing = this.hubRepository.count();

        if (existing >= 1) {

            LOGGER.info("Hub limit reached. existing={} -> throwing HubLimitReachedException", existing);
            throw new HubCountLimitReachedException();
        }

        var hub = new Hub(input.name());

        for (var currency : input.currencies()) {

            LOGGER.info("Adding supported currency: {}", currency);
            hub.addCurrency(currency);
            LOGGER.info("Added supported currency: {}", currency);
        }

        this.hubRepository.save(hub);

        LOGGER.info("Completed CreateHubCommand with input: {}", input);

        return new Output(hub.getId());
    }

}
