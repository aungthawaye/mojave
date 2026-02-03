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

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.command.oracle.CreateOracleCommand;
import org.mojave.core.participant.contract.exception.oracle.OracleAlreadyExistsException;
import org.mojave.core.participant.domain.model.oracle.Oracle;
import org.mojave.core.participant.domain.repository.OracleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class CreateOracleCommandHandler implements CreateOracleCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOracleCommandHandler.class);

    private final OracleRepository oracleRepository;

    public CreateOracleCommandHandler(OracleRepository oracleRepository) {

        Objects.requireNonNull(oracleRepository);
        this.oracleRepository = oracleRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("CreateOracleCommand : input: ({})", ObjectLogger.log(input));

        // Check if an Oracle already exists for the given PartyIdType
        if (this.oracleRepository
                .findOne(OracleRepository.Filters.withType(input.type()))
                .isPresent()) {
            throw new OracleAlreadyExistsException(input.type());
        }

        var oracle = new Oracle(input.type(), input.name(), input.baseUrl());
        this.oracleRepository.save(oracle);

        var output = new Output(oracle.getId());
        LOGGER.info("CreateOracleCommand : output : ({})", ObjectLogger.log(output));
        return output;
    }

}
