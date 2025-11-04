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

import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.core.participant.contract.exception.oracle.OracleAlreadyExistsException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CreateOracleCommandIT extends BaseDomainIT {

    @Autowired
    private CreateOracleCommand createOracle;

    @Test
    void should_create_oracle() throws Exception {

        // Arrange
        var input = new CreateOracleCommand.Input(PartyIdType.MSISDN, "MSISDN Oracle", "http://oracle.local/msisdn");

        // Act
        var output = this.createOracle.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        Assertions.assertNotNull(output.oracleId());
    }

    @Test
    void should_throw_when_oracle_type_already_exists() throws Exception {

        // Arrange
        var input1 = new CreateOracleCommand.Input(PartyIdType.EMAIL, "Email Oracle A", "http://oracle.local/email-a");
        var input2 = new CreateOracleCommand.Input(PartyIdType.EMAIL, "Email Oracle B", "http://oracle.local/email-b");

        // Act
        this.createOracle.execute(input1);

        // Assert
        Assertions.assertThrows(OracleAlreadyExistsException.class, () -> this.createOracle.execute(input2));
    }

}
