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
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.definition.RemoveFlowDefinitionPostingCommand;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.contract.exception.definition.PostingDefinitionNotFoundException;
import io.mojaloop.core.accounting.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.accounting.ReceiveIn;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.core.common.datatype.identifier.accounting.PostingDefinitionId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RemoveFlowDefinitionPostingCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private RemoveFlowDefinitionPostingCommand removeFlowDefinitionPostingCommand;

    @Test
    void should_remove_posting_definition_successfully() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));

        final var debitEntry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc",
                AccountType.ASSET));
        final var creditEntry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("LIAB"), "Liabilities", "Liab Desc",
                AccountType.LIABILITY));

        // Mature entries
        this.createAccountCommand.execute(new CreateAccountCommand.Input(debitEntry.chartEntryId(), new AccountOwnerId(3401L),
            Currency.USD, new AccountCode("ACC_D"), "Debit Acc", "Test", OverdraftMode.FORBID, BigDecimal.ZERO));
        this.createAccountCommand.execute(new CreateAccountCommand.Input(creditEntry.chartEntryId(), new AccountOwnerId(3401L),
            Currency.USD, new AccountCode("ACC_C"), "Credit Acc", "Test", OverdraftMode.FORBID, BigDecimal.ZERO));

        final var postings = List.of(
            new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, debitEntry.chartEntryId().getId(), "DEPOSIT_INTO_FSP",
                "LIQUIDITY_AMOUNT", Side.DEBIT, "Debit Assets"),
            new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, creditEntry.chartEntryId().getId(), "DEPOSIT_INTO_FSP",
                "LIQUIDITY_AMOUNT", Side.CREDIT, "Credit Liabilities")
        );

        final var created = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "Flow F", "Desc", postings));

        final var toRemovePostingId = created.postingDefinitionIds().get(0);
        final var input = new RemoveFlowDefinitionPostingCommand.Input(created.flowDefinitionId(), toRemovePostingId);

        // Act
        final var output = this.removeFlowDefinitionPostingCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertNotNull(output.flowDefinitionId());
    }

    @Test
    void should_fail_when_flow_definition_not_found() {
        // Arrange
        final var invalidFlowId = new FlowDefinitionId(99999999L);
        final var invalidPostingId = new PostingDefinitionId(11111111L);
        final var input = new RemoveFlowDefinitionPostingCommand.Input(invalidFlowId, invalidPostingId);

        // Act & Assert
        assertThrows(FlowDefinitionNotFoundException.class, () -> this.removeFlowDefinitionPostingCommand.execute(input));
    }

    @Test
    void should_fail_when_posting_definition_not_found() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var entry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc",
                AccountType.ASSET));
        this.createAccountCommand.execute(new CreateAccountCommand.Input(entry.chartEntryId(), new AccountOwnerId(3402L),
            Currency.USD, new AccountCode("ACC_ASSET"), "Asset Acc", "Test", OverdraftMode.FORBID, BigDecimal.ZERO));

        final var created = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "Flow G", "Desc",
                List.of(new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, entry.chartEntryId().getId(), "DEPOSIT_INTO_FSP",
                    "LIQUIDITY_AMOUNT", Side.DEBIT, "Debit Assets"))));

        final var input = new RemoveFlowDefinitionPostingCommand.Input(created.flowDefinitionId(), new PostingDefinitionId(123456789L));

        // Act & Assert
        assertThrows(PostingDefinitionNotFoundException.class, () -> this.removeFlowDefinitionPostingCommand.execute(input));
    }
}
