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

package io.mojaloop.core.participant.admin.client.api.oracle;

import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantAdminClientException;
import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class GetAllOraclesIT {

    @Autowired
    private CreateOracle createOracle;

    @Autowired
    private GetAllOracles getAllOracles;

    @Test
    public void test_successfully_get_all_oracles() throws ParticipantAdminClientException {

        this.createOracle.execute(new CreateOracleCommand.Input(PartyIdType.ACCOUNT_ID, "Oracle List 1", "http://localhost:7090"));
        this.createOracle.execute(new CreateOracleCommand.Input(PartyIdType.EMAIL, "Oracle List 2", "http://localhost:7090"));
        this.getAllOracles.execute();
    }

}
