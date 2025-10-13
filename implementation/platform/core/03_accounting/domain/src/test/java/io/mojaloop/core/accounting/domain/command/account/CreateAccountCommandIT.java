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

package io.mojaloop.core.accounting.domain.command.account;

import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateAccountCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Test
    void should_create_account_successfully() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var chartId = chartOut.chartId();

        final var entryOut = this.createChartEntryCommand.execute(new CreateChartEntryCommand.Input(
            chartId,
            new ChartEntryCode("ASSETS"),
            "Assets",
            "Assets Desc",
            AccountType.ASSET
        ));
        final var chartEntryId = entryOut.chartEntryId();

        final var input = new CreateAccountCommand.Input(
            chartEntryId,
            new AccountOwnerId(1001L),
            Currency.USD,
            new AccountCode("ACC001"),
            "Cash Account",
            "Cash Account for tests",
            OverdraftMode.FORBID,
            BigDecimal.ZERO
        );

        // Act
        final var output = this.createAccountCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.accountId());
    }

    @Test
    void should_fail_when_chart_entry_not_found() {
        // Arrange
        final var invalidChartEntryId = new ChartEntryId(99999999L);

        final var input = new CreateAccountCommand.Input(
            invalidChartEntryId,
            new AccountOwnerId(1002L),
            Currency.USD,
            new AccountCode("ACC002"),
            "Invalid Account",
            "Should fail due to missing chart entry",
            OverdraftMode.FORBID,
            BigDecimal.ZERO
        );

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> this.createAccountCommand.execute(input));
    }

}
