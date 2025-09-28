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
/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package io.mojaloop.core.participant.domain.command.oracle;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.participant.contract.command.oracle.TerminateOracleCommand;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.domain.repository.OracleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TerminateOracleCommandHandler implements TerminateOracleCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(TerminateOracleCommandHandler.class);

    private final OracleRepository oracleRepository;

    public TerminateOracleCommandHandler(OracleRepository oracleRepository) {

        assert oracleRepository != null;
        this.oracleRepository = oracleRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws OracleIdNotFoundException {

        LOGGER.info("Executing TerminateOracleCommand with input: {}", input);

        var oracle = this.oracleRepository
                         .findById(input.oracleId())
                         .orElseThrow(() -> new OracleIdNotFoundException(input.oracleId()));

        oracle.terminate();
        this.oracleRepository.save(oracle);

        LOGGER.info("Completed TerminateOracleCommand with input: {}", input);
        return new Output();
    }

}
