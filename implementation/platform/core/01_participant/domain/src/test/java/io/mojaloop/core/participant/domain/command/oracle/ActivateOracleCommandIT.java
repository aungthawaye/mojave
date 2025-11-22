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

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.command.oracle.ActivateOracleCommand;
import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.domain.command.BaseDomainIT;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ActivateOracleCommandIT extends BaseDomainIT {

    @Autowired
    private CreateOracleCommand createOracle;

    @Autowired
    private ActivateOracleCommand activateOracle;

    @Test
    void should_activate_oracle() throws Exception {

        // Arrange
        var created = this.createOracle.execute(new CreateOracleCommand.Input(PartyIdType.MSISDN, "MSISDN Oracle", "http://oracle.local/msisdn"));
        var input = new ActivateOracleCommand.Input(created.oracleId());

        // Act
        var output = this.activateOracle.execute(input);

        // Assert
        Assertions.assertNotNull(output);
    }

    @Test
    void should_throw_when_oracle_not_found_on_activation() {

        // Arrange
        var input = new ActivateOracleCommand.Input(new OracleId(999999L));

        // Act & Assert
        Assertions.assertThrows(OracleIdNotFoundException.class, () -> this.activateOracle.execute(input));
    }

}
