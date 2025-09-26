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
import io.mojaloop.core.participant.contract.command.hub.DeactivateHubCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.domain.model.hub.HubCurrency;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import io.mojaloop.core.participant.domain.repository.HubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateHubCurrencyCommandHandler implements DeactivateHubCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateHubCurrencyCommandHandler.class);

    private final HubRepository hubRepository;

    private final FspRepository fspRepository;

    public DeactivateHubCurrencyCommandHandler(HubRepository hubRepository, FspRepository fspRepository) {

        assert hubRepository != null;
        assert fspRepository != null;

        this.hubRepository = hubRepository;
        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws HubNotFoundException {

        LOGGER.info("Executing DeactivateHubCurrencyCommand with input: {}", input);

        var hub = this.hubRepository
                      .findById(input.hubId())
                      .orElseThrow(HubNotFoundException::new);

        var optHubCurrency = hub.deactivate(input.currency());

        var fsps = this.fspRepository.findAll();

        for (var fsp : fsps) {

            LOGGER.info("Deactivating currency of FSP: fspId : [{}], currency : [{}]", fsp.getId(), input.currency());

            fsp.deactivate(input.currency());

            this.fspRepository.save(fsp);
        }

        this.hubRepository.save(hub);

        LOGGER.info("Completed DeactivateHubCurrencyCommand with input: {} -> deactivated={} ", input, optHubCurrency);

        if (optHubCurrency.isPresent()) {
            return new Output(optHubCurrency.map(HubCurrency::getId).orElse(null), optHubCurrency.get().isActive());
        }

        return new Output(null, false);
    }

}
