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
import org.mojave.core.participant.contract.command.fsp.CreateFspGroupCommand;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameAlreadyExistsException;
import org.mojave.core.participant.domain.model.fsp.FspGroup;
import org.mojave.core.participant.domain.repository.FspGroupRepository;
import org.mojave.core.participant.domain.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Primary
public class CreateFspGroupCommandHandler implements CreateFspGroupCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateFspGroupCommandHandler.class);

    private final FspGroupRepository fspGroupRepository;

    private final FspRepository fspRepository;

    public CreateFspGroupCommandHandler(final FspGroupRepository fspGroupRepository,
                                        final FspRepository fspRepository) {

        Objects.requireNonNull(fspGroupRepository);
        Objects.requireNonNull(fspRepository);

        this.fspGroupRepository = fspGroupRepository;
        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("CreateFspGroupCommand : input: ({})", ObjectLogger.log(input));

        if (this.fspGroupRepository
                .findOne(FspGroupRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {
            throw new FspGroupNameAlreadyExistsException(input.name());
        }

        final var group = new FspGroup(input.name());

        for (final var fspId : input.fspIds()) {

            final var withId = FspRepository.Filters.withId(fspId);
            final var alive = FspRepository.Filters.withTerminationStatus(TerminationStatus.ALIVE);

            if (this.fspRepository.findOne(withId.and(alive)).isEmpty()) {
                throw new FspIdNotFoundException(fspId);
            }

            group.addItem(fspId);
        }

        this.fspGroupRepository.save(group);

        final var output = new Output(group.getId());

        LOGGER.info("CreateFspGroupCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
