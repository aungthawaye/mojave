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

package io.mojaloop.core.accounting.domain.command.account;

import io.mojaloop.core.accounting.contract.command.account.ActivateAccountCommand;
import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.accounting.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ActivateAccountCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private ActivateAccountCommand activateAccountCommand;

    @Test
    void should_activate_account_successfully() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var entryOut = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc", AccountType.ASSET));

        final var createInput = new CreateAccountCommand.Input(entryOut.chartEntryId(), new AccountOwnerId(2001L), Currency.USD, new AccountCode("ACC003"), "Account", "Desc",
            OverdraftMode.FORBID, BigDecimal.ZERO);
        final var accountOut = this.createAccountCommand.execute(createInput);

        final var input = new ActivateAccountCommand.Input(accountOut.accountId());

        // Act
        final var output = this.activateAccountCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.accountId());
    }

    @Test
    void should_fail_when_account_id_not_found() {
        // Arrange
        final var input = new ActivateAccountCommand.Input(new AccountId(999999L));

        // Act & Assert
        assertThrows(AccountIdNotFoundException.class, () -> this.activateAccountCommand.execute(input));
    }

}
