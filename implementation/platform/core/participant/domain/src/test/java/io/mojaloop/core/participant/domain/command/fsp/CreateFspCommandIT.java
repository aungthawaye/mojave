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
package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.common.component.flyway.FlywayMigration;
import io.mojaloop.common.component.vault.Vault;
import io.mojaloop.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.common.datatype.type.fspiop.FspCode;
import io.mojaloop.common.fspiop.model.core.Currency;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.EndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.FspAlreadyExistsException;
import io.mojaloop.core.participant.domain.LocalVaultSettings;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.core.participant.domain.ParticipantDomainSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateFspCommandIT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @BeforeAll
    public static void setup() {

        var vault = new Vault(LocalVaultSettings.VAULT_ADDR, LocalVaultSettings.VAULT_TOKEN, LocalVaultSettings.ENGINE_PATH);

        FlywayMigration.migrate(vault.get(ParticipantDomainSettings.VaultPaths.FLYWAY_PATH, FlywayMigration.Settings.class));
    }

    @Test
    public void test() throws EndpointAlreadyConfiguredException, CurrencyAlreadySupportedException, FspAlreadyExistsException {

        assert createFspCommand != null;

        var input = new CreateFspCommand.Input(new FspCode("fsp2"),
                                               "FSP 2",
                                               new Currency[]{Currency.USD, Currency.MMK, Currency.MYR},
                                               new CreateFspCommand.Input.Endpoint[]{
                                                   new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "https://www.fspexample.com"),
                                                   new CreateFspCommand.Input.Endpoint(EndpointType.QUOTES, "https://www.fspexample.com"),
                                                   new CreateFspCommand.Input.Endpoint(EndpointType.TRANSFERS,
                                                                                       "https://www.fspexample.com")});

        var output = this.createFspCommand.execute(input);
    }

}
