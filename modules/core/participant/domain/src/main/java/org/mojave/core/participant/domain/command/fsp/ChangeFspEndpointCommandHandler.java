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
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.core.participant.contract.command.fsp.ChangeFspEndpointCommand;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.domain.model.fsp.FspEndpoint;
import org.mojave.core.participant.domain.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
@Primary
public class ChangeFspEndpointCommandHandler implements ChangeFspEndpointCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeFspEndpointCommandHandler.class);

    private final FspRepository fspRepository;

    public ChangeFspEndpointCommandHandler(FspRepository fspRepository) {

        Objects.requireNonNull(fspRepository);
        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("ChangeFspEndpointCommand : input: ({})", ObjectLogger.log(input));

        var withId = FspRepository.Filters.withId(input.fspId());
        var alive = FspRepository.Filters.withTerminationStatus(TerminationStatus.ALIVE);

        var fsp = this.fspRepository
                      .findOne(withId.and(alive))
                      .orElseThrow(() -> new FspIdNotFoundException(input.fspId()));

        var optFspEndpoint = fsp.changeEndpoint(input.type(), input.baseUrl());

        this.fspRepository.save(fsp);

        var output = new Output(
            optFspEndpoint.map(FspEndpoint::getId).orElse(null), optFspEndpoint.isPresent());

        LOGGER.info("ChangeFspEndpointCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
