/*-
 * ============================================================================
 * Mojaloop OSS
 * ----------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * ----------------------------------------------------------------------------
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
 * ============================================================================
 */

package io.mojaloop.core.participant.domain.command.oracle;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.core.participant.contract.command.oracle.DeactivateOracleCommand;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class DeactivateOracleCommandIT {

    @Autowired
    private CreateOracleCommand createOracleCommand;

    @Autowired
    private DeactivateOracleCommand deactivateOracleCommand;

    @Test
    public void deactivate_succeeds_and_invalidId_throws() throws Exception {
        assertNotNull(createOracleCommand);
        assertNotNull(deactivateOracleCommand);

        var created = createOracleCommand.execute(new CreateOracleCommand.Input(
            PartyIdType.EMAIL, "Oracle-Email", "https://oracle-email.example.com"));

        // Deactivate should not throw
        deactivateOracleCommand.execute(new DeactivateOracleCommand.Input(created.oracleId()));

        // Non-existent
        var nonExisting = new OracleId(-1010L);
        assertThrows(OracleIdNotFoundException.class,
            () -> deactivateOracleCommand.execute(new DeactivateOracleCommand.Input(nonExisting)));
    }
}
