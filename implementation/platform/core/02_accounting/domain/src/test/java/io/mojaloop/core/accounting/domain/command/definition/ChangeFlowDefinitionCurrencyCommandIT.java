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
import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionWithCurrencyExistsException;
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

public class ChangeFlowDefinitionCurrencyCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private ChangeFlowDefinitionCurrencyCommand changeFlowDefinitionCurrencyCommand;

    @Test
    void should_change_currency_successfully() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var entry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc", AccountType.ASSET));
        this.createAccountCommand.execute(
            new CreateAccountCommand.Input(
                entry.chartEntryId(), new AccountOwnerId(3501L), Currency.USD, new AccountCode("ACC_ASSET"), "Asset Acc", "Test", OverdraftMode.FORBID, BigDecimal.ZERO));

        final var created = this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
            TransactionType.FUND_IN, Currency.USD, "Flow H", "Desc", List.of(
            new CreateFlowDefinitionCommand.Input.Posting(
                ReceiveIn.CHART_ENTRY, entry.chartEntryId().getId(), "DEPOSIT_INTO_FSP", "LIQUIDITY_AMOUNT", Side.DEBIT, "Debit Assets"))));

        final var input = new ChangeFlowDefinitionCurrencyCommand.Input(created.flowDefinitionId(), Currency.EUR);

        // Act
        final var output = this.changeFlowDefinitionCurrencyCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.flowDefinitionId());
    }

    @Test
    void should_fail_when_target_currency_conflicts() throws Exception {
        // Arrange
        // Create flow A in USD
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var entry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc", AccountType.ASSET));
        this.createAccountCommand.execute(
            new CreateAccountCommand.Input(
                entry.chartEntryId(), new AccountOwnerId(3502L), Currency.USD, new AccountCode("ACC_ASSET"), "Asset Acc", "Test", OverdraftMode.FORBID, BigDecimal.ZERO));

        final var flowA = this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
            TransactionType.FUND_IN, Currency.USD, "Flow I", "Desc", List.of(
            new CreateFlowDefinitionCommand.Input.Posting(
                ReceiveIn.CHART_ENTRY, entry.chartEntryId().getId(), "DEPOSIT_INTO_FSP", "LIQUIDITY_AMOUNT", Side.DEBIT, "Debit Assets"))));

        // Create flow B in EUR
        final var flowB = this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
            TransactionType.FUND_IN, Currency.EUR, "Flow J", "Desc", List.of(
            new CreateFlowDefinitionCommand.Input.Posting(
                ReceiveIn.CHART_ENTRY, entry.chartEntryId().getId(), "DEPOSIT_INTO_FSP", "LIQUIDITY_AMOUNT", Side.DEBIT, "Debit Assets"))));

        // Try to change flow A currency to EUR which is already used by flow B
        final var input = new ChangeFlowDefinitionCurrencyCommand.Input(flowA.flowDefinitionId(), Currency.EUR);

        // Act & Assert
        assertThrows(FlowDefinitionWithCurrencyExistsException.class, () -> this.changeFlowDefinitionCurrencyCommand.execute(input));
    }

}
