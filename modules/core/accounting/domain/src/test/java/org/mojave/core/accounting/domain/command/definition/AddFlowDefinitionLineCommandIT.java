package org.mojave.core.accounting.domain.command.definition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.command.definition.AddFlowDefinitionLineCommand;
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.core.accounting.contract.exception.definition.CoaEntryConflictInDefinitionException;
import org.mojave.core.accounting.contract.exception.definition.DuplicateFlowDefinitionLineIndexException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import org.mojave.core.accounting.contract.exception.definition.ImmatureCoaEntryException;
import org.mojave.core.accounting.contract.exception.definition.InvalidAmountNameForAccountingScenarioException;
import org.mojave.core.accounting.contract.exception.definition.InvalidParticipantForAccountingScenarioException;
import org.mojave.core.accounting.contract.exception.definition.RequireParticipantForCoaEntryException;
import org.mojave.core.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.core.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.core.accounting.domain.BaseIT;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.accounting.AccountType;
import org.mojave.scheme.rule.enums.accounting.Side;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
import org.mojave.scheme.rule.identifier.accounting.FlowDefinitionId;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Add Flow Definition Line Command Integration Test")
public class AddFlowDefinitionLineCommandIT extends BaseIT {

    @Autowired
    private AddFlowDefinitionLineCommand addFlowDefinitionLineCommand;

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
    @DisplayName("Throw when flow definition line step already exists")
    public void duplicateFlowDefinitionLineIndex() {

        final var coaId = this.createCoa(this.createCoaCommand, "Duplicate Flow Definition Line CoA");
        
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "DUP_FLOW_PAYER_01",
            "Dup Flow Payer 01", AccountType.ASSET);
        
        final var payeeCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "DUP_FLOW_PAYEE_01",
            "Dup Flow Payee 01", AccountType.REVENUE);

        this.createAccount(
            this.createAccountCommand, payerCoaEntryId, 403L, Currency.BYN, "DUP_FLOW_ACC_01",
            "Dup Flow Account 01");
        this.createAccount(
            this.createAccountCommand, payeeCoaEntryId, 404L, Currency.BYN, "DUP_FLOW_ACC_02",
            "Dup Flow Account 02");

        final var flowDefinitionId = this.createFlowDefinition(
            this.createFlowDefinitionCommand, ScenarioType.P2P_TRANSFER, Currency.BYN,
            "duplicate-flow-line-definition", payerCoaEntryId, "PAYER_FSP", "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            DuplicateFlowDefinitionLineIndexException.class,
            () -> this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
                flowDefinitionId,
                new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                    1, "PAYEE_FSP", payeeCoaEntryId, "PAYEE_FSP_FEE", Side.CREDIT,
                    "duplicate step"))));
    }

    @Test
    @DisplayName("Throw when flow definition cannot be found")
    public void flowDefinitionNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class,
            () -> this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
                new FlowDefinitionId(Long.MAX_VALUE), new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                1, "PAYER_FSP", new CoaEntryId(Long.MAX_VALUE), "TRANSFER_AMOUNT", Side.DEBIT,
                "missing definition"))));
    }

    @Test
    @DisplayName("Add flow definition line successfully")
    public void successful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Add Flow Definition Line CoA");
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "ADD_FLOW_PAYER_01",
            "Add Flow Payer 01", AccountType.ASSET);
        final var payeeCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "ADD_FLOW_PAYEE_01",
            "Add Flow Payee 01", AccountType.REVENUE);

        this.createAccount(
            this.createAccountCommand, payerCoaEntryId, 401L, Currency.USD, "ADD_FLOW_ACC_01",
            "Add Flow Account 01");
        this.createAccount(
            this.createAccountCommand, payeeCoaEntryId, 402L, Currency.USD, "ADD_FLOW_ACC_02",
            "Add Flow Account 02");

        final var flowDefinitionId = this.createFlowDefinition(
            this.createFlowDefinitionCommand, ScenarioType.P2P_TRANSFER, Currency.USD,
            "add-flow-line-definition", payerCoaEntryId, "PAYER_FSP", "TRANSFER_AMOUNT",
            Side.DEBIT);

        final var output = this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
            flowDefinitionId,
            new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                2, "PAYEE_FSP", payeeCoaEntryId, "PAYEE_FSP_FEE", Side.CREDIT, "payee fee line")));
        final var definition = this.flowDefinitionQuery.get(flowDefinitionId);

        assertNotNull(output.flowDefinitionLineId());
        assertEquals(flowDefinitionId, output.flowDefinitionId());
        assertEquals(2, definition.flowDefinitionLines().size());
        assertEquals("PAYEE_FSP", definition.flowDefinitionLines().get(1).participant());
        assertEquals("PAYEE_FSP_FEE", definition.flowDefinitionLines().get(1).amountName());
    }

    @Test
    @DisplayName("Throw when participant is invalid for transaction type")
    public void invalidParticipantForAccountingScenario() {

        final var coaId = this.createCoa(this.createCoaCommand, "Invalid Participant Add CoA");
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "INVALID_PARTICIPANT_ADD_01",
            "Invalid Participant Add 01",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand,
            payerCoaEntryId,
            405L,
            Currency.USD,
            "INVALID_PARTICIPANT_ADD_ACC_01",
            "Invalid Participant Add Account 01");

        final var flowDefinitionId = this.createFlowDefinition(
            this.createFlowDefinitionCommand,
            ScenarioType.P2P_TRANSFER,
            Currency.USD,
            "invalid-participant-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            InvalidParticipantForAccountingScenarioException.class,
            () -> this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
                flowDefinitionId,
                new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                    2,
                    "HUB_X",
                    payerCoaEntryId,
                    "PAYEE_FSP_FEE",
                    Side.CREDIT,
                    "invalid participant"))));
    }

    @Test
    @DisplayName("Throw when amount name is invalid for transaction type")
    public void invalidAmountNameForAccountingScenario() {

        final var coaId = this.createCoa(this.createCoaCommand, "Invalid Amount Add CoA");
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "INVALID_AMOUNT_ADD_01",
            "Invalid Amount Add 01",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand,
            payerCoaEntryId,
            406L,
            Currency.USD,
            "INVALID_AMOUNT_ADD_ACC_01",
            "Invalid Amount Add Account 01");

        final var flowDefinitionId = this.createFlowDefinition(
            this.createFlowDefinitionCommand,
            ScenarioType.P2P_TRANSFER,
            Currency.USD,
            "invalid-amount-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            InvalidAmountNameForAccountingScenarioException.class,
            () -> this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
                flowDefinitionId,
                new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                    2,
                    "PAYEE_FSP",
                    payerCoaEntryId,
                    "INVALID_AMOUNT",
                    Side.CREDIT,
                    "invalid amount"))));
    }

    @Test
    @DisplayName("Throw when participant is required for flow definition line")
    public void requireParticipantForCoaEntry() {

        final var coaId = this.createCoa(this.createCoaCommand, "Required Participant Add CoA");
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "REQUIRED_PARTICIPANT_ADD_01",
            "Required Participant Add 01",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand,
            payerCoaEntryId,
            407L,
            Currency.USD,
            "REQUIRED_PARTICIPANT_ADD_ACC_01",
            "Required Participant Add Account 01");

        final var flowDefinitionId = this.createFlowDefinition(
            this.createFlowDefinitionCommand,
            ScenarioType.P2P_TRANSFER,
            Currency.USD,
            "required-participant-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            RequireParticipantForCoaEntryException.class,
            () -> this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
                flowDefinitionId,
                new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                    2,
                    " ",
                    payerCoaEntryId,
                    "PAYEE_FSP_FEE",
                    Side.CREDIT,
                    "missing participant"))));
    }

    @Test
    @DisplayName("Throw when CoA entry id cannot be found")
    public void coaEntryIdNotFound() {

        final var coaId = this.createCoa(this.createCoaCommand, "Missing Entry Add CoA");
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "MISSING_ENTRY_ADD_01",
            "Missing Entry Add 01",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand,
            payerCoaEntryId,
            408L,
            Currency.USD,
            "MISSING_ENTRY_ADD_ACC_01",
            "Missing Entry Add Account 01");

        final var flowDefinitionId = this.createFlowDefinition(
            this.createFlowDefinitionCommand,
            ScenarioType.P2P_TRANSFER,
            Currency.USD,
            "missing-entry-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            CoaEntryIdNotFoundException.class,
            () -> this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
                flowDefinitionId,
                new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                    2,
                    "PAYEE_FSP",
                    new CoaEntryId(Long.MAX_VALUE),
                    "PAYEE_FSP_FEE",
                    Side.CREDIT,
                    "missing entry"))));
    }

    @Test
    @DisplayName("Throw when CoA entry is immature")
    public void immatureCoaEntry() {

        final var coaId = this.createCoa(this.createCoaCommand, "Immature Entry Add CoA");
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "IMMATURE_ENTRY_ADD_01",
            "Immature Entry Add 01",
            AccountType.ASSET);
        final var payeeCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "IMMATURE_ENTRY_ADD_02",
            "Immature Entry Add 02",
            AccountType.REVENUE);

        this.createAccount(
            this.createAccountCommand,
            payerCoaEntryId,
            409L,
            Currency.USD,
            "IMMATURE_ENTRY_ADD_ACC_01",
            "Immature Entry Add Account 01");

        final var flowDefinitionId = this.createFlowDefinition(
            this.createFlowDefinitionCommand,
            ScenarioType.P2P_TRANSFER,
            Currency.USD,
            "immature-entry-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            ImmatureCoaEntryException.class,
            () -> this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
                flowDefinitionId,
                new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                    2,
                    "PAYEE_FSP",
                    payeeCoaEntryId,
                    "PAYEE_FSP_FEE",
                    Side.CREDIT,
                    "immature entry"))));
    }

    @Test
    @DisplayName("Throw when CoA entry conflicts inside definition")
    public void coaEntryConflictInDefinition() {

        final var coaId = this.createCoa(this.createCoaCommand, "Conflict Add CoA");
        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand,
            coaId,
            "FSP",
            "CONFLICT_ADD_01",
            "Conflict Add 01",
            AccountType.ASSET);

        this.createAccount(
            this.createAccountCommand,
            payerCoaEntryId,
            410L,
            Currency.USD,
            "CONFLICT_ADD_ACC_01",
            "Conflict Add Account 01");

        final var flowDefinitionId = this.createFlowDefinition(
            this.createFlowDefinitionCommand,
            ScenarioType.P2P_TRANSFER,
            Currency.USD,
            "conflict-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            CoaEntryConflictInDefinitionException.class,
            () -> this.addFlowDefinitionLineCommand.execute(new AddFlowDefinitionLineCommand.Input(
                flowDefinitionId,
                new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                    2,
                    "PAYER_FSP",
                    payerCoaEntryId,
                    "TRANSFER_AMOUNT",
                    Side.DEBIT,
                    "conflict"))));
    }

}
