package org.mojave.accounting.domain.command.definition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.accounting.contract.exception.definition.CoaEntryConflictInDefinitionException;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionAlreadyConfiguredException;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import org.mojave.accounting.contract.exception.definition.ImmatureCoaEntryException;
import org.mojave.accounting.contract.exception.definition.InvalidAmountNameForTransactionTypeException;
import org.mojave.accounting.contract.exception.definition.InvalidParticipantForTransactionTypeException;
import org.mojave.accounting.contract.exception.definition.RequireParticipantForCoaEntryException;
import org.mojave.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.accounting.domain.BaseIT;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.scheme.rule.type.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Create Flow Definition Command Integration Test")
public class CreateFlowDefinitionCommandIT extends BaseIT {

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private FlowDefinitionQuery flowDefinitionQuery;

    @Test
    @DisplayName("Throw when flow definition is already configured")
    public void flowDefinitionAlreadyConfigured() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow Duplicate CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "FLOW_DUP_SETTLE_01",
            "Flow Dup Settle 01", AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand, coaEntryId, 302L, Currency.BYN, "FLOW_DUP_ACC_01",
            "Flow Dup Account 01");

        this.createFlowDefinition(
            this.createFlowDefinitionCommand, TransactionType.FUND_TRANSFER, Currency.BYN,
            "fund-transfer-usd-duplicate", coaEntryId, "PAYER_FSP", "TRANSFER_AMOUNT", Side.DEBIT);

        assertThrows(
            FlowDefinitionAlreadyConfiguredException.class, () -> this.createFlowDefinition(
                this.createFlowDefinitionCommand, TransactionType.FUND_TRANSFER, Currency.BYN,
                "fund-transfer-usd-duplicate-2", coaEntryId, "PAYEE_FSP", "PAYEE_FSP_FEE",
                Side.CREDIT));
    }

    @Test
    @DisplayName("Throw when flow definition name is already taken")
    public void flowDefinitionNameTaken() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow Name Taken CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "FLOW_NAME_TAKEN_01",
            "Flow Name Taken 01",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand,
            coaEntryId,
            305L,
            Currency.USD,
            "FLOW_NAME_TAKEN_ACC_01",
            "Flow Name Taken Account 01");

        this.createFlowDefinition(
            this.createFlowDefinitionCommand,
            TransactionType.FUND_TRANSFER,
            Currency.USD,
            "same-flow-name",
            coaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            FlowDefinitionNameTakenException.class,
            () -> this.createFlowDefinition(
                this.createFlowDefinitionCommand,
                TransactionType.FUND_TRANSFER,
                Currency.EUR,
                "same-flow-name",
                coaEntryId,
                "PAYER_FSP",
                "TRANSFER_AMOUNT",
                Side.DEBIT));
    }

    @Test
    @DisplayName("Throw when amount name is invalid for transaction type")
    public void invalidAmountNameForTransactionType() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow Amount CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "FLOW_AMOUNT_SETTLE_01",
            "Flow Amount Settle 01", AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand, coaEntryId, 304L, Currency.GBP, "FLOW_AMT_ACC_01",
            "Flow Amount Account 01");

        assertThrows(
            InvalidAmountNameForTransactionTypeException.class,
            () -> this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
                TransactionType.FUND_TRANSFER, Currency.GBP, "flow-invalid-amount",
                "flow-invalid-amount description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowLine(
                    1, "PAYER_FSP", coaEntryId,
                    "INVALID_AMOUNT", Side.DEBIT, "invalid amount")))));
    }

    @Test
    @DisplayName("Throw when participant is invalid for transaction type")
    public void invalidParticipantForTransactionType() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow Participant CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "HUB", "FLOW_HUB_SETTLE_01",
            "Flow Hub Settle 01", AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand, coaEntryId, 303L, Currency.EUR, "FLOW_PART_ACC_01",
            "Flow Participant Account 01");

        assertThrows(
            InvalidParticipantForTransactionTypeException.class,
            () -> this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
                TransactionType.FUND_TRANSFER, Currency.EUR, "flow-invalid-participant",
                "flow-invalid-participant description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowLine(
                    1, "HUB", coaEntryId,
                    "TRANSFER_AMOUNT", Side.DEBIT, "invalid participant")))));
    }

    @Test
    @DisplayName("Throw when participant is required for flow line")
    public void requireParticipantForCoaEntry() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow Participant Required CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "FLOW_PARTICIPANT_REQUIRED_01",
            "Flow Participant Required 01",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand,
            coaEntryId,
            306L,
            Currency.USD,
            "FLOW_PARTICIPANT_REQUIRED_ACC_01",
            "Flow Participant Required Account 01");

        assertThrows(
            RequireParticipantForCoaEntryException.class,
            () -> this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
                TransactionType.FUND_TRANSFER,
                Currency.USD,
                "flow-participant-required",
                "flow-participant-required description",
                List.of(new CreateFlowDefinitionCommand.Input.FlowLine(
                    1,
                    "   ",
                    coaEntryId,
                    "TRANSFER_AMOUNT",
                    Side.DEBIT,
                    "missing participant")))));
    }

    @Test
    @DisplayName("Throw when CoA entry id cannot be found in flow line")
    public void coaEntryIdNotFound() {

        assertThrows(
            CoaEntryIdNotFoundException.class,
            () -> this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
                TransactionType.FUND_TRANSFER,
                Currency.USD,
                "flow-missing-entry",
                "flow-missing-entry description",
                List.of(new CreateFlowDefinitionCommand.Input.FlowLine(
                    1,
                    "PAYER_FSP",
                    new CoaEntryId(Long.MAX_VALUE),
                    "TRANSFER_AMOUNT",
                    Side.DEBIT,
                    "missing entry")))));
    }

    @Test
    @DisplayName("Throw when CoA entry is immature")
    public void immatureCoaEntry() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow Immature CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "FLOW_IMMATURE_ENTRY_01",
            "Flow Immature Entry 01",
            AccountType.ASSET);

        assertThrows(
            ImmatureCoaEntryException.class,
            () -> this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
                TransactionType.FUND_TRANSFER,
                Currency.USD,
                "flow-immature-entry",
                "flow-immature-entry description",
                List.of(new CreateFlowDefinitionCommand.Input.FlowLine(
                    1,
                    "PAYER_FSP",
                    coaEntryId,
                    "TRANSFER_AMOUNT",
                    Side.DEBIT,
                    "immature entry")))));
    }

    @Test
    @DisplayName("Throw when CoA entry conflicts inside definition")
    public void coaEntryConflictInDefinition() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow Conflict CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "FLOW_CONFLICT_ENTRY_01",
            "Flow Conflict Entry 01",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand,
            coaEntryId,
            307L,
            Currency.USD,
            "FLOW_CONFLICT_ACC_01",
            "Flow Conflict Account 01");

        assertThrows(
            CoaEntryConflictInDefinitionException.class,
            () -> this.createFlowDefinitionCommand.execute(new CreateFlowDefinitionCommand.Input(
                TransactionType.FUND_TRANSFER,
                Currency.USD,
                "flow-coa-conflict",
                "flow-coa-conflict description",
                List.of(
                    new CreateFlowDefinitionCommand.Input.FlowLine(
                        1,
                        "PAYER_FSP",
                        coaEntryId,
                        "TRANSFER_AMOUNT",
                        Side.DEBIT,
                        "first line"),
                    new CreateFlowDefinitionCommand.Input.FlowLine(
                        2,
                        "PAYER_FSP",
                        coaEntryId,
                        "TRANSFER_AMOUNT",
                        Side.DEBIT,
                        "conflict line")))));
    }

    @Test
    @DisplayName("Create flow definition successfully")
    public void successful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow CoA");
        final var coaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "FLOW_PAYER_SETTLE_01",
            "Flow Payer Settle 01", AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand, coaEntryId, 301L, Currency.USD, "FLOW_ACC_01",
            "Flow Account 01");

        final var output = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                TransactionType.FUND_TRANSFER, Currency.USD, "fund-transfer-usd",
                "fund-transfer-usd description",
                List.of(new CreateFlowDefinitionCommand.Input.FlowLine(
                    1, " PAYER_FSP ", coaEntryId, " transfer_amount ", Side.DEBIT,
                    "payer transfer amount"))));
        final var definition = this.flowDefinitionQuery.get(output.flowDefinitionId());

        assertNotNull(output.flowDefinitionId());
        assertEquals(TransactionType.FUND_TRANSFER, definition.transactionType());
        assertEquals(Currency.USD, definition.currency());
        assertEquals(1, definition.flowLines().size());
        assertEquals("PAYER_FSP", definition.flowLines().getFirst().participant());
        assertEquals("TRANSFER_AMOUNT", definition.flowLines().getFirst().amountName());
    }

}
