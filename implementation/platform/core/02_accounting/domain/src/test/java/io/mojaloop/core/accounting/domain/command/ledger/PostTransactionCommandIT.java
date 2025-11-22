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

package io.mojaloop.core.accounting.domain.command.ledger;

import io.mojaloop.core.accounting.contract.command.account.CreateAccountCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartCommand;
import io.mojaloop.core.accounting.contract.command.chart.CreateChartEntryCommand;
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.command.ledger.PostTransactionCommand;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotConfiguredException;
import io.mojaloop.core.accounting.contract.exception.ledger.RequiredAmountNameNotFoundInTransactionException;
import io.mojaloop.core.accounting.contract.exception.ledger.RequiredParticipantNotFoundInTransactionException;
import io.mojaloop.core.accounting.domain.command.BaseDomainIT;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.enums.accounting.OverdraftMode;
import io.mojaloop.core.common.datatype.enums.accounting.ReceiveIn;
import io.mojaloop.core.common.datatype.enums.accounting.Side;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PostTransactionCommandIT extends BaseDomainIT {

    @Autowired
    private CreateChartCommand createChartCommand;

    @Autowired
    private CreateChartEntryCommand createChartEntryCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private PostTransactionCommand postTransactionCommand;

    @Test
    void should_fail_when_flow_definition_not_configured() {
        // Arrange
        final var txId = new TransactionId(7000000000004L);
        final var txAt = Instant.parse("2025-01-01T10:30:00Z");
        final var participants = Map.of("DEPOSIT_INTO_FSP", new AccountOwnerId(99999L));
        final var amounts = Map.of("LIQUIDITY_AMOUNT", new BigDecimal("1"));
        final var input = new PostTransactionCommand.Input(TransactionType.FUND_IN, Currency.EUR, txId, txAt, participants, amounts);

        // Act & Assert (no flow definition created for EUR FUND_IN)
        assertThrows(FlowDefinitionNotConfiguredException.class, () -> this.postTransactionCommand.execute(input));
    }

    @Test
    void should_fail_when_required_amount_missing() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var assetsEntry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc", AccountType.ASSET));
        final var ownerId = new AccountOwnerId(91003L);
        this.createAccountCommand.execute(
            new CreateAccountCommand.Input(assetsEntry.chartEntryId(), ownerId, Currency.USD, new AccountCode("ACC_ASSET_Y"), "Asset Acc", "Test", OverdraftMode.FORBID,
                BigDecimal.ZERO));

        final var postings = List.of(
            new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, assetsEntry.chartEntryId().getId(), "DEPOSIT_INTO_FSP", "LIQUIDITY_AMOUNT", Side.DEBIT,
                "Debit Assets"));
        this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "FundIn Missing A", "Desc", postings));

        final var txId = new TransactionId(7000000000003L);
        final var txAt = Instant.parse("2025-01-01T10:20:00Z");
        final var participants = Map.of("DEPOSIT_INTO_FSP", ownerId);
        final var amounts = Map.<String, BigDecimal>of(); // Missing required amount name
        final var input = new PostTransactionCommand.Input(TransactionType.FUND_IN, Currency.USD, txId, txAt, participants, amounts);

        // Act & Assert
        assertThrows(RequiredAmountNameNotFoundInTransactionException.class, () -> this.postTransactionCommand.execute(input));
    }

    @Test
    void should_fail_when_required_participant_missing() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));
        final var assetsEntry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc", AccountType.ASSET));
        final var ownerId = new AccountOwnerId(91002L);
        this.createAccountCommand.execute(
            new CreateAccountCommand.Input(assetsEntry.chartEntryId(), ownerId, Currency.USD, new AccountCode("ACC_ASSET_X"), "Asset Acc", "Test", OverdraftMode.FORBID,
                BigDecimal.ZERO));

        final var postings = List.of(
            new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, assetsEntry.chartEntryId().getId(), "DEPOSIT_INTO_FSP", "LIQUIDITY_AMOUNT", Side.DEBIT,
                "Debit Assets"));
        this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "FundIn Missing P", "Desc", postings));

        final var txId = new TransactionId(7000000000002L);
        final var txAt = Instant.parse("2025-01-01T10:10:00Z");
        final var participants = Map.<String, AccountOwnerId>of(); // Missing required participant
        final var amounts = Map.of("LIQUIDITY_AMOUNT", new BigDecimal("50"));
        final var input = new PostTransactionCommand.Input(TransactionType.FUND_IN, Currency.USD, txId, txAt, participants, amounts);

        // Act & Assert
        assertThrows(RequiredParticipantNotFoundInTransactionException.class, () -> this.postTransactionCommand.execute(input));
    }

    @Test
    void should_post_fund_in_successfully() throws Exception {
        // Arrange
        final var chartOut = this.createChartCommand.execute(new CreateChartCommand.Input("Main Chart"));

        final var assetsEntry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("ASSETS"), "Assets", "Assets Desc", AccountType.ASSET));
        final var liabilitiesEntry = this.createChartEntryCommand.execute(
            new CreateChartEntryCommand.Input(chartOut.chartId(), new ChartEntryCode("LIAB"), "Liabilities", "Liabilities Desc", AccountType.LIABILITY));

        final var ownerId = new AccountOwnerId(91001L);

        // Mature entries with accounts for the participant owner
        this.createAccountCommand.execute(
            new CreateAccountCommand.Input(assetsEntry.chartEntryId(), ownerId, Currency.USD, new AccountCode("ACC_ASSET_FI"), "Asset Acc", "Test", OverdraftMode.FORBID,
                BigDecimal.ZERO));
        this.createAccountCommand.execute(
            new CreateAccountCommand.Input(liabilitiesEntry.chartEntryId(), ownerId, Currency.USD, new AccountCode("ACC_LIAB_FI"), "Liab Acc", "Test", OverdraftMode.FORBID,
                BigDecimal.ZERO));

        final var postings = List.of(
            new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, assetsEntry.chartEntryId().getId(), "DEPOSIT_INTO_FSP", "LIQUIDITY_AMOUNT", Side.DEBIT,
                "Debit Assets"),
            new CreateFlowDefinitionCommand.Input.Posting(ReceiveIn.CHART_ENTRY, liabilitiesEntry.chartEntryId().getId(), "DEPOSIT_INTO_FSP", "LIQUIDITY_AMOUNT", Side.CREDIT,
                "Credit Liabilities"));

        this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(TransactionType.FUND_IN, Currency.USD, "FundIn (USD)", "Desc", postings));

        final var txId = new TransactionId(7000000000001L);
        final var txAt = Instant.parse("2025-01-01T10:00:00Z");
        final var participants = Map.of("DEPOSIT_INTO_FSP", ownerId);
        final var amounts = Map.of("LIQUIDITY_AMOUNT", new BigDecimal("123.45"));

        final var input = new PostTransactionCommand.Input(TransactionType.FUND_IN, Currency.USD, txId, txAt, participants, amounts);

        // Act
        final var output = this.postTransactionCommand.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(txId, output.transactionId());
        assertEquals(txAt, output.transactionAt());
        assertEquals(TransactionType.FUND_IN, output.transactionType());
        assertNotNull(output.flowDefinitionId());
        assertNotNull(output.movements());
        assertEquals(2, output.movements().size());

        // Verify movement sides and amounts
        final var sides = output.movements().stream().map(PostTransactionCommand.Output.Movement::side).toList();
        assertTrue(sides.contains(Side.DEBIT));
        assertTrue(sides.contains(Side.CREDIT));
        output.movements().forEach(m -> {
            assertEquals(Currency.USD, m.currency());
            assertEquals(0, m.amount().compareTo(new BigDecimal("123.45")));
            assertNotNull(m.ledgerMovementId());
            assertNotNull(m.accountId());
            assertNotNull(m.chartEntryId());
            assertNotNull(m.createdAt());
        });
    }

}
