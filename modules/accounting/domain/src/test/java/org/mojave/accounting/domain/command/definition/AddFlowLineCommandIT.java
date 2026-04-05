package org.mojave.accounting.domain.command.definition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.accounting.contract.command.definition.AddFlowLineCommand;
import org.mojave.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.accounting.contract.exception.definition.CoaEntryConflictInDefinitionException;
import org.mojave.accounting.contract.exception.definition.DuplicateFlowLineIndexException;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
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
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.scheme.rule.type.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Add Flow Line Command Integration Test")
public class AddFlowLineCommandIT extends BaseIT {

    @Autowired
    private AddFlowLineCommand addFlowLineCommand;

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
    @DisplayName("Throw when flow line step already exists")
    public void duplicateFlowLineIndex() {

        final var coaId = this.createCoa(this.createCoaCommand, "Duplicate Flow Line CoA");
        
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
            this.createFlowDefinitionCommand, TransactionType.FUND_TRANSFER, Currency.BYN,
            "duplicate-flow-line-definition", payerCoaEntryId, "PAYER_FSP", "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            DuplicateFlowLineIndexException.class,
            () -> this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
                flowDefinitionId,
                new AddFlowLineCommand.Input.FlowLine(
                    1, "PAYEE_FSP", payeeCoaEntryId, "PAYEE_FSP_FEE", Side.CREDIT,
                    "duplicate step"))));
    }

    @Test
    @DisplayName("Throw when flow definition cannot be found")
    public void flowDefinitionNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class,
            () -> this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
                new FlowDefinitionId(Long.MAX_VALUE), new AddFlowLineCommand.Input.FlowLine(
                1, "PAYER_FSP", new CoaEntryId(Long.MAX_VALUE), "TRANSFER_AMOUNT", Side.DEBIT,
                "missing definition"))));
    }

    @Test
    @DisplayName("Add flow line successfully")
    public void successful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Add Flow Line CoA");
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
            this.createFlowDefinitionCommand, TransactionType.FUND_TRANSFER, Currency.USD,
            "add-flow-line-definition", payerCoaEntryId, "PAYER_FSP", "TRANSFER_AMOUNT",
            Side.DEBIT);

        final var output = this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
            flowDefinitionId,
            new AddFlowLineCommand.Input.FlowLine(
                2, "PAYEE_FSP", payeeCoaEntryId, "PAYEE_FSP_FEE", Side.CREDIT, "payee fee line")));
        final var definition = this.flowDefinitionQuery.get(flowDefinitionId);

        assertNotNull(output.flowLineId());
        assertEquals(flowDefinitionId, output.flowDefinitionId());
        assertEquals(2, definition.flowLines().size());
        assertEquals("PAYEE_FSP", definition.flowLines().get(1).participant());
        assertEquals("PAYEE_FSP_FEE", definition.flowLines().get(1).amountName());
    }

    @Test
    @DisplayName("Throw when participant is invalid for transaction type")
    public void invalidParticipantForTransactionType() {

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
            TransactionType.FUND_TRANSFER,
            Currency.USD,
            "invalid-participant-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            InvalidParticipantForTransactionTypeException.class,
            () -> this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
                flowDefinitionId,
                new AddFlowLineCommand.Input.FlowLine(
                    2,
                    "HUB",
                    payerCoaEntryId,
                    "PAYEE_FSP_FEE",
                    Side.CREDIT,
                    "invalid participant"))));
    }

    @Test
    @DisplayName("Throw when amount name is invalid for transaction type")
    public void invalidAmountNameForTransactionType() {

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
            TransactionType.FUND_TRANSFER,
            Currency.USD,
            "invalid-amount-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            InvalidAmountNameForTransactionTypeException.class,
            () -> this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
                flowDefinitionId,
                new AddFlowLineCommand.Input.FlowLine(
                    2,
                    "PAYEE_FSP",
                    payerCoaEntryId,
                    "INVALID_AMOUNT",
                    Side.CREDIT,
                    "invalid amount"))));
    }

    @Test
    @DisplayName("Throw when participant is required for flow line")
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
            TransactionType.FUND_TRANSFER,
            Currency.USD,
            "required-participant-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            RequireParticipantForCoaEntryException.class,
            () -> this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
                flowDefinitionId,
                new AddFlowLineCommand.Input.FlowLine(
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
            TransactionType.FUND_TRANSFER,
            Currency.USD,
            "missing-entry-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            CoaEntryIdNotFoundException.class,
            () -> this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
                flowDefinitionId,
                new AddFlowLineCommand.Input.FlowLine(
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
            TransactionType.FUND_TRANSFER,
            Currency.USD,
            "immature-entry-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            ImmatureCoaEntryException.class,
            () -> this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
                flowDefinitionId,
                new AddFlowLineCommand.Input.FlowLine(
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
            TransactionType.FUND_TRANSFER,
            Currency.USD,
            "conflict-add-flow",
            payerCoaEntryId,
            "PAYER_FSP",
            "TRANSFER_AMOUNT",
            Side.DEBIT);

        assertThrows(
            CoaEntryConflictInDefinitionException.class,
            () -> this.addFlowLineCommand.execute(new AddFlowLineCommand.Input(
                flowDefinitionId,
                new AddFlowLineCommand.Input.FlowLine(
                    2,
                    "PAYER_FSP",
                    payerCoaEntryId,
                    "TRANSFER_AMOUNT",
                    Side.DEBIT,
                    "conflict"))));
    }

}
