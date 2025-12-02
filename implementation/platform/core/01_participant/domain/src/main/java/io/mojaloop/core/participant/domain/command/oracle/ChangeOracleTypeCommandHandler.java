/*-
 * ================================================================================
 * Mojave
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
/*-
 * ==============================================================================
 * Mojave
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
 * ==============================================================================
 */

package io.mojaloop.core.participant.domain.command.oracle;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.participant.contract.command.oracle.ChangeOracleTypeCommand;
import io.mojaloop.core.participant.contract.exception.oracle.OracleAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.domain.repository.OracleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeOracleTypeCommandHandler implements ChangeOracleTypeCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeOracleTypeCommandHandler.class);

    private final OracleRepository oracleRepository;

    public ChangeOracleTypeCommandHandler(OracleRepository oracleRepository) {

        assert oracleRepository != null;
        this.oracleRepository = oracleRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("ChangeOracleTypeCommand : input: ({})", ObjectLogger.log(input));

        var oracle = this.oracleRepository
                         .findById(input.oracleId())
                         .orElseThrow(() -> new OracleIdNotFoundException(input.oracleId()));

        var newType = input.type();
        if (oracle.getType() != null && oracle.getType().equals(newType)) {
            var output = new Output();
            LOGGER.info("ChangeOracleTypeCommand : output : ({})", ObjectLogger.log(output));
            return output;
        }

        // Check for conflict: any other oracle already holds this type
        var existing = this.oracleRepository.findOne(OracleRepository.Filters
                                                         .withType(newType)
                                                         .and(Specification.not(
                                                             OracleRepository.Filters.withId(
                                                                 oracle.getId()))));

        if (existing.isPresent()) {
            throw new OracleAlreadyExistsException(newType);
        }

        oracle.type(newType);
        this.oracleRepository.save(oracle);

        var output = new Output();
        LOGGER.info("ChangeOracleTypeCommand : output : ({})", ObjectLogger.log(output));
        return output;
    }

}
