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
import org.mojave.core.participant.contract.command.fsp.RemoveFspGroupItemCommand;
import org.mojave.core.participant.contract.exception.fsp.FspGroupIdNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.FspGroupItemIdNotFoundException;
import org.mojave.core.participant.domain.repository.FspGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Primary
public class RemoveFspGroupItemCommandHandler implements RemoveFspGroupItemCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RemoveFspGroupItemCommandHandler.class);

    private final FspGroupRepository fspGroupRepository;

    public RemoveFspGroupItemCommandHandler(final FspGroupRepository fspGroupRepository) {

        Objects.requireNonNull(fspGroupRepository);

        this.fspGroupRepository = fspGroupRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("RemoveFspGroupItemCommand : input: ({})", ObjectLogger.log(input));

        final var group = this.fspGroupRepository
                              .findById(input.fspGroupId())
                              .orElseThrow(
                                  () -> new FspGroupIdNotFoundException(input.fspGroupId()));

        final var removed = group.removeItem(input.fspGroupItemId());

        if (!removed) {
            throw new FspGroupItemIdNotFoundException(input.fspGroupItemId());
        }

        this.fspGroupRepository.save(group);

        final var output = new Output(input.fspGroupItemId());

        LOGGER.info("RemoveFspGroupItemCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
