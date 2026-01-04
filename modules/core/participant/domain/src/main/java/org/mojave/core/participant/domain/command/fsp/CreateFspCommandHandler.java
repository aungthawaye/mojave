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
package org.mojave.core.participant.domain.command.fsp;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.scheme.common.datatype.identifier.participant.HubId;
import org.mojave.core.participant.contract.command.fsp.CreateFspCommand;
import org.mojave.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import org.mojave.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import org.mojave.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import org.mojave.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import org.mojave.core.participant.contract.exception.hub.HubNotFoundException;
import org.mojave.core.participant.domain.model.fsp.Fsp;
import org.mojave.core.participant.domain.repository.FspRepository;
import org.mojave.core.participant.domain.repository.HubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class CreateFspCommandHandler implements CreateFspCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFspCommandHandler.class);

    private final FspRepository fspRepository;

    private final HubRepository hubRepository;

    public CreateFspCommandHandler(FspRepository fspRepository, HubRepository hubRepository) {

        assert fspRepository != null;
        assert hubRepository != null;

        this.fspRepository = fspRepository;
        this.hubRepository = hubRepository;
    }

    @Override
    @Transactional
    @Write
    public CreateFspCommand.Output execute(CreateFspCommand.Input input) throws
                                                                         FspCurrencyAlreadySupportedException,
                                                                         FspEndpointAlreadyConfiguredException,
                                                                         FspCodeAlreadyExistsException,
                                                                         HubNotFoundException,
                                                                         FspCurrencyNotSupportedByHubException {

        LOGGER.info("CreateFspCommand : input: ({})", ObjectLogger.log(input));

        var hub = this.hubRepository.findById(new HubId()).orElseThrow(HubNotFoundException::new);

        if (this.fspRepository
                .findOne(FspRepository.Filters.withFspCode(input.fspCode()))
                .isPresent()) {
            throw new FspCodeAlreadyExistsException(input.fspCode());
        }

        var fsp = new Fsp(hub, input.fspCode(), input.name());

        for (var currency : input.currencies()) {
            fsp.addCurrency(currency);
        }

        for (var endpoint : input.endpoints()) {
            fsp.addEndpoint(endpoint.type(), endpoint.baseUrl());
        }

        this.fspRepository.save(fsp);

        var output = new Output(fsp.getId());

        LOGGER.info("CreateFspCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
