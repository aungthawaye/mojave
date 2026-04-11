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
import org.mojave.core.participant.contract.command.fsp.JoinFspGroupCommand;
import org.mojave.core.participant.contract.exception.fsp.FspGroupIdNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.TerminatedFspIdException;
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
public class JoinFspGroupCommandHandler implements JoinFspGroupCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(JoinFspGroupCommandHandler.class);

    private final FspGroupRepository fspGroupRepository;

    private final FspRepository fspRepository;

    public JoinFspGroupCommandHandler(final FspGroupRepository fspGroupRepository,
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

        LOGGER.info("JoinFspGroupCommand : input: ({})", ObjectLogger.log(input));

        final var fsp = this.fspRepository
                            .findById(input.fspId())
                            .orElseThrow(() -> new FspIdNotFoundException(input.fspId()));

        if (fsp.getTerminationStatus() == TerminationStatus.TERMINATED) {
            throw new TerminatedFspIdException(input.fspId());
        }

        final var group = this.fspGroupRepository
                              .findById(input.fspGroupId())
                              .orElseThrow(
                                  () -> new FspGroupIdNotFoundException(input.fspGroupId()));

        fsp.join(group);

        this.fspRepository.save(fsp);

        final var output = new Output();

        LOGGER.info("JoinFspGroupCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}
