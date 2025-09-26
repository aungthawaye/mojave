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

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.core.participant.contract.command.oracle.TerminateOracleCommand;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TerminateOracleCommandIT extends BaseDomainIT {

    @Autowired
    private CreateOracleCommand createOracle;

    @Autowired
    private TerminateOracleCommand terminateOracle;

    @Test
    void should_terminate_oracle() throws Exception {

        // Arrange
        var created = this.createOracle.execute(new CreateOracleCommand.Input(PartyIdType.BUSINESS, "Business Oracle", "http://oracle.local/business"));
        var input = new TerminateOracleCommand.Input(created.oracleId());

        // Act
        var output = this.terminateOracle.execute(input);

        // Assert
        Assertions.assertNotNull(output);
    }

    @Test
    void should_throw_when_oracle_not_found_on_termination() {

        // Arrange
        var input = new TerminateOracleCommand.Input(new OracleId(999997L));

        // Act & Assert
        Assertions.assertThrows(OracleIdNotFoundException.class, () -> this.terminateOracle.execute(input));
    }
}
