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

import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.command.fsp.LeaveFspGroupCommand;
import org.mojave.core.participant.contract.exception.fsp.FspGroupIdNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.TerminatedFspIdException;
import org.mojave.core.participant.domain.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Primary
public class LeaveFspGroupCommandHandler implements LeaveFspGroupCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeaveFspGroupCommandHandler.class);

    private final FspRepository fspRepository;

    public LeaveFspGroupCommandHandler(final FspRepository fspRepository) {

        Objects.requireNonNull(fspRepository);

        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("LeaveFspGroupCommand : input: ({})", ObjectLogger.log(input));

        final var fsp = this.fspRepository
                            .findById(input.fspId())
                            .orElseThrow(() -> new FspGroupIdNotFoundException(input.fspGroupId()));

        if (fsp.getTerminationStatus() == TerminationStatus.TERMINATED) {
            throw new TerminatedFspIdException(input.fspId());
        }

        fsp.leave();

        final var output = new Output();

        LOGGER.info("LeaveFspGroupCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
