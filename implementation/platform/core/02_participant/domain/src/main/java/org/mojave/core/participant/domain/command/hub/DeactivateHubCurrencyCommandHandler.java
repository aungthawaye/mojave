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
package org.mojave.core.participant.domain.command.hub;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.command.hub.DeactivateHubCurrencyCommand;
import org.mojave.core.participant.contract.exception.hub.HubNotFoundException;
import org.mojave.core.participant.domain.repository.FspRepository;
import org.mojave.core.participant.domain.repository.HubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class DeactivateHubCurrencyCommandHandler implements DeactivateHubCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DeactivateHubCurrencyCommandHandler.class);

    private final HubRepository hubRepository;

    private final FspRepository fspRepository;

    public DeactivateHubCurrencyCommandHandler(HubRepository hubRepository,
                                               FspRepository fspRepository) {

        assert hubRepository != null;
        assert fspRepository != null;

        this.hubRepository = hubRepository;
        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("DeactivateHubCurrencyCommand : input: ({})", ObjectLogger.log(input));

        var hub = this.hubRepository.findById(input.hubId()).orElseThrow(HubNotFoundException::new);

        var optHubCurrency = hub.deactivate(input.currency());

        var fsps = this.fspRepository.findAll();

        for (var fsp : fsps) {
            fsp.deactivate(input.currency());

            this.fspRepository.save(fsp);
        }

        this.hubRepository.save(hub);

        var output = optHubCurrency
                         .map(currency -> new Output(currency.getId(), !currency.isActive()))
                         .orElse(new Output(null, false));

        LOGGER.info("DeactivateHubCurrencyCommand : output: ({})", ObjectLogger.log(output));

        return output;
    }

}
