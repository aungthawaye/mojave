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
import io.mojaloop.core.participant.contract.command.fsp.ChangeEndpointCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeEndpointCommandHandler implements ChangeEndpointCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeEndpointCommandHandler.class);

    private final FspRepository fspRepository;

    public ChangeEndpointCommandHandler(FspRepository fspRepository) {

        assert fspRepository != null;
        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws FspIdNotFoundException {

        LOGGER.info("Executing ChangeEndpointCommand with input: {}", input);

        var fsp = this.fspRepository.findById(input.fspId()).orElseThrow(() -> new FspIdNotFoundException(input.fspId()));

        boolean changed = fsp.changeEndpoint(input.endpointType(), input.baseUrl());

        this.fspRepository.save(fsp);

        LOGGER.info("Completed ChangeEndpointCommand with input: {} -> changed={}", input, changed);

        return new Output(changed);
    }

}
