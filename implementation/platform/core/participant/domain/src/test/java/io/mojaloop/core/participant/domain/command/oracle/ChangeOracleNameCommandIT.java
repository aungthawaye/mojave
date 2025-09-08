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
import io.mojaloop.core.participant.contract.command.oracle.ChangeOracleNameCommand;
import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.core.participant.contract.exception.OracleIdNotFoundException;
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
public class ChangeOracleNameCommandIT {

    @Autowired
    private CreateOracleCommand createOracleCommand;

    @Autowired
    private ChangeOracleNameCommand changeOracleNameCommand;

    @Test
    public void changeName_and_notFound() throws Exception {

        assertNotNull(createOracleCommand);
        assertNotNull(changeOracleNameCommand);

        var created = createOracleCommand.execute(new CreateOracleCommand.Input(
            PartyIdType.ACCOUNT_ID, "Oracle-Account", "https://oracle-account.example.com"));

        changeOracleNameCommand.execute(new ChangeOracleNameCommand.Input(created.oracleId(), "New Name"));

        var nonExisting = new OracleId(-2020L);
        assertThrows(OracleIdNotFoundException.class,
            () -> changeOracleNameCommand.execute(new ChangeOracleNameCommand.Input(nonExisting, "X")));
    }
}
