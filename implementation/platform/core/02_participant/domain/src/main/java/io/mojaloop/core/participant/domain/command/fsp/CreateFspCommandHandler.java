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

package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.domain.model.fsp.Fsp;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import io.mojaloop.core.participant.domain.repository.HubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
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

        LOGGER.info("Executing CreateFspCommand with input: {}", input);

        var hub = this.hubRepository.findById(new HubId()).orElseThrow(HubNotFoundException::new);

        if (this.fspRepository.findOne(FspRepository.Filters.withFspCode(input.fspCode())).isPresent()) {
            LOGGER.info("FSP with FSP Code {} already exists", input.fspCode().value());
            throw new FspCodeAlreadyExistsException(input.fspCode());
        }

        var fsp = new Fsp(hub, input.fspCode(), input.name());
        LOGGER.info("Created FSP: {}", fsp);

        for (var currency : input.supportedCurrencies()) {
            LOGGER.info("Adding supported currency: {}", currency);
            fsp.addCurrency(currency);
            LOGGER.info("Added supported currency: {}", currency);
        }

        for (var endpoint : input.endpoints()) {
            LOGGER.info("Adding endpoint: {}", endpoint);
            fsp.addEndpoint(endpoint.type(), endpoint.baseUrl());
            LOGGER.info("Added endpoint: {}", endpoint);
        }

        this.fspRepository.save(fsp);

        LOGGER.info("Completed CreateFspCommand with input: {}", input);

        return new Output(fsp.getId());
    }

}
