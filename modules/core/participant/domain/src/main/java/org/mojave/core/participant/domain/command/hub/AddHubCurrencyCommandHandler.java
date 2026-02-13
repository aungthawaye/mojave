/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.core.participant.domain.command.hub;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.command.hub.AddHubCurrencyCommand;
import org.mojave.core.participant.contract.exception.hub.HubNotFoundException;
import org.mojave.core.participant.domain.repository.HubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Primary
public class AddHubCurrencyCommandHandler implements AddHubCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        AddHubCurrencyCommandHandler.class);

    private final HubRepository hubRepository;

    public AddHubCurrencyCommandHandler(HubRepository hubRepository) {

        Objects.requireNonNull(hubRepository);
        this.hubRepository = hubRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("AddHubCurrencyCommand : input: ({})", ObjectLogger.log(input));

        var hub = this.hubRepository.findById(input.hubId()).orElseThrow(HubNotFoundException::new);

        var supportedCurrency = hub.addCurrency(input.currency());

        this.hubRepository.save(hub);

        var output = new Output(supportedCurrency.getId());

        LOGGER.info("AddHubCurrencyCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
