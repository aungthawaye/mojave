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

package io.mojaloop.core.participant.admin.client.api.fsp;

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantAdminClientException;
import io.mojaloop.core.participant.contract.command.fsp.AddFspCurrencyCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class DeactivateSupportedCurrencyIT {

    @Autowired
    private CreateFsp createFsp;

    @Autowired
    private AddSupportedCurrency addSupportedCurrency;

    @Autowired
    private DeactivateSupportedCurrency deactivateSupportedCurrency;

    @Test
    public void test_successfully_deactivate_supported_currency() throws ParticipantAdminClientException {

        var output = this.createFsp.execute(
            new CreateFspCommand.Input(new FspCode("fsp-deactivate-currency"), "FSP Deactivate Currency", new Currency[]{Currency.USD},
                                       new CreateFspCommand.Input.Endpoint[]{
                                           new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://localhost:7080")
                                       }));

        var fspId = new FspId(output.fspId().getId());
        this.addSupportedCurrency.execute(new AddFspCurrencyCommand.Input(fspId, Currency.EUR));

        this.deactivateSupportedCurrency.execute(new DeactivateFspCurrencyCommand.Input(fspId, Currency.EUR));
    }

}
