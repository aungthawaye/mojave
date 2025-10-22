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

package io.mojaloop.core.accounting.domain.command.definition;

import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import io.mojaloop.core.accounting.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.accounting.ReceiveIn;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChangeFlowDefinitionPropertiesCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private ChangeFlowDefinitionPropertiesCommand changeFlowDefinitionPropertiesCommand;

    @Test
    void should_change_properties_successfully() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var entry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc",
                AccountType.ASSET));
        this.createAccountCommand.execute(new CreateAccountCommand.Input(entry.chartEntryId(), new AccountOwnerId(3601L),
            Currency.USD, new AccountCode("ACC_ASSET"), "Asset Acc", "Test", OverdraftMode.FORBID, BigDecimal.ZERO));

        final var created = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "Flow K", "Original Desc",
                List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, entry.chartEntryId().getId(), "DEPOSIT_INTO_FSP",
                    "LIQUIDITY_AMOUNT", Side.DEBIT, "Debit Assets"))));

        final var input = new ChangeFlowDefinitionPropertiesCommand.Input(created.flowDefinitionId(), "Flow K Updated",
            "Updated Description");

        // Act
        final var output = this.changeFlowDefinitionPropertiesCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.flowDefinitionId());
    }

    @Test
    void should_fail_when_name_taken() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var entry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc",
                AccountType.ASSET));
        this.createAccountCommand.execute(new CreateAccountCommand.Input(entry.chartEntryId(), new AccountOwnerId(3602L),
            Currency.USD, new AccountCode("ACC_ASSET"), "Asset Acc", "Test", OverdraftMode.FORBID, BigDecimal.ZERO));

        final var flow1 = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "Flow L", "Desc",
                List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, entry.chartEntryId().getId(), "DEPOSIT_INTO_FSP",
                    "LIQUIDITY_AMOUNT", Side.DEBIT, "Debit Assets"))));

        final var flow2 = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(TransactionType.FUND_OUT, Currency.EUR, "Flow M", "Desc",
                List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, entry.chartEntryId().getId(), "WITHDRAW_FROM_FSP",
                    "LIQUIDITY_AMOUNT", Side.DEBIT, "Debit Assets"))));

        final var input = new ChangeFlowDefinitionPropertiesCommand.Input(flow2.flowDefinitionId(), "Flow L", "New Desc");

        // Act & Assert
        assertThrows(FlowDefinitionNameTakenException.class, () -> this.changeFlowDefinitionPropertiesCommand.execute(input));
    }
}
