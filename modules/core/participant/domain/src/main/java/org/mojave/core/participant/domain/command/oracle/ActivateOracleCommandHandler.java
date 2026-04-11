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

package org.mojave.core.participant.domain.command.oracle;

import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.command.oracle.ActivateOracleCommand;
import org.mojave.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import org.mojave.core.participant.domain.repository.OracleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Primary
public class ActivateOracleCommandHandler implements ActivateOracleCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ActivateOracleCommandHandler.class);

    private final OracleRepository oracleRepository;

    public ActivateOracleCommandHandler(OracleRepository oracleRepository) {

        Objects.requireNonNull(oracleRepository);
        this.oracleRepository = oracleRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("ActivateOracleCommand : input: ({})", ObjectLogger.log(input));

        var withId = OracleRepository.Filters.withId(input.oracleId());
        var alive = OracleRepository.Filters.withTerminationStatus(TerminationStatus.ALIVE);

        var oracle = this.oracleRepository
                         .findOne(withId.and(alive))
                         .orElseThrow(() -> new OracleIdNotFoundException(input.oracleId()));

        oracle.activate();
        this.oracleRepository.save(oracle);

        var output = new Output();

        LOGGER.info("ActivateOracleCommand : output : ({})", ObjectLogger.log(output));
        return output;
    }

}
