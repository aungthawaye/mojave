package org.mojave.core.accounting.intercom.requestor.command.definition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.command.definition.ActivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.AddFlowDefinitionLineCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.DeactivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.RemoveFlowDefinitionLineCommand;
import org.mojave.core.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import org.mojave.core.accounting.intercom.requestor.AccountingIntercomRequestorTestConfiguration;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.accounting.AccountType;
import org.mojave.scheme.rule.enums.accounting.Side;
import org.mojave.scheme.rule.type.accounting.CoaEntryCode;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingIntercomRequestorTestConfiguration.class})
@DisplayName("Definition Commands Requestor Integration Test")
public class DefinitionCommandsRequestorIT {

    @Autowired
    private ActivateFlowDefinitionCommand activateFlowDefinitionCommand;

    @Autowired
    private AddFlowDefinitionLineCommand addFlowDefinitionLineCommand;

    @Autowired
    private ChangeFlowDefinitionCurrencyCommand changeFlowDefinitionCurrencyCommand;

    @Autowired
    private ChangeFlowDefinitionPropertiesCommand changeFlowDefinitionPropertiesCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private DeactivateFlowDefinitionCommand deactivateFlowDefinitionCommand;

    @Autowired
    private RemoveFlowDefinitionLineCommand removeFlowDefinitionLineCommand;

    @Autowired
    private TerminateFlowDefinitionCommand terminateFlowDefinitionCommand;

    @Test
    @DisplayName("Execute flow definition commands through requestor")
    public void successful() {

        final var suffix = Long.toString(System.currentTimeMillis());

        final var createCoaOutput = this.createCoaCommand.execute(
            new CreateCoaCommand.Input("requestor-flow-coa-" + suffix));

        final var debitEntryOutput = this.createCoaEntryCommand.execute(
            new CreateCoaEntryCommand.Input(
                createCoaOutput.coaId(),
                "FSP",
                new CoaEntryCode("REQ_FLOW_DB_" + suffix),
                "Requestor Flow Debit Entry " + suffix,
                "Requestor Flow Debit Entry Description " + suffix,
                AccountType.ASSET));

        final var creditEntryOutput = this.createCoaEntryCommand.execute(
            new CreateCoaEntryCommand.Input(
                createCoaOutput.coaId(),
                "FSP",
                new CoaEntryCode("REQ_FLOW_CR_" + suffix),
                "Requestor Flow Credit Entry " + suffix,
                "Requestor Flow Credit Entry Description " + suffix,
                AccountType.LIABILITY));

        final var createDefinitionOutput = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                ScenarioType.P2P_TRANSFER,
                Currency.BYN,
                "requestor-flow-" + suffix,
                "requestor-flow-description-" + suffix,
                List.of(
                    new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                        1,
                        "PAYER_FSP",
                        debitEntryOutput.coaEntryId(),
                        "TRANSFER_AMOUNT",
                        Side.DEBIT,
                        "requestor-flow-line-debit-" + suffix),
                    new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                        2,
                        "PAYEE_FSP",
                        creditEntryOutput.coaEntryId(),
                        "TRANSFER_AMOUNT",
                        Side.CREDIT,
                        "requestor-flow-line-credit-" + suffix))));

        final var addLineOutput = this.addFlowDefinitionLineCommand.execute(
            new AddFlowDefinitionLineCommand.Input(
                createDefinitionOutput.flowDefinitionId(),
                new AddFlowDefinitionLineCommand.Input.FlowDefinitionLine(
                    3,
                    "HUB",
                    creditEntryOutput.coaEntryId(),
                    "TRANSFER_FEE",
                    Side.CREDIT,
                    "requestor-flow-line-fee-" + suffix)));

        final var removeLineOutput = this.removeFlowDefinitionLineCommand.execute(
            new RemoveFlowDefinitionLineCommand.Input(
                createDefinitionOutput.flowDefinitionId(),
                addLineOutput.flowDefinitionLineId()));

        final var changePropertiesOutput = this.changeFlowDefinitionPropertiesCommand.execute(
            new ChangeFlowDefinitionPropertiesCommand.Input(
                createDefinitionOutput.flowDefinitionId(),
                "requestor-flow-updated-" + suffix,
                "requestor-flow-description-updated-" + suffix));

        final var changeCurrencyOutput = this.changeFlowDefinitionCurrencyCommand.execute(
            new ChangeFlowDefinitionCurrencyCommand.Input(
                createDefinitionOutput.flowDefinitionId(),
                Currency.USD));

        final var deactivateOutput = this.deactivateFlowDefinitionCommand.execute(
            new DeactivateFlowDefinitionCommand.Input(createDefinitionOutput.flowDefinitionId()));

        final var activateOutput = this.activateFlowDefinitionCommand.execute(
            new ActivateFlowDefinitionCommand.Input(createDefinitionOutput.flowDefinitionId()));

        final var terminateOutput = this.terminateFlowDefinitionCommand.execute(
            new TerminateFlowDefinitionCommand.Input(createDefinitionOutput.flowDefinitionId()));

        assertNotNull(createDefinitionOutput.flowDefinitionId());
        assertNotNull(createDefinitionOutput.flowDefinitionLineIds());
        assertNotNull(addLineOutput.flowDefinitionLineId());

        assertEquals(createDefinitionOutput.flowDefinitionId(), removeLineOutput.flowDefinitionId());
        assertEquals(createDefinitionOutput.flowDefinitionId(), changePropertiesOutput.flowDefinitionId());
        assertEquals(createDefinitionOutput.flowDefinitionId(), changeCurrencyOutput.flowDefinitionId());
        assertEquals(createDefinitionOutput.flowDefinitionId(), deactivateOutput.flowDefinitionId());
        assertEquals(createDefinitionOutput.flowDefinitionId(), activateOutput.flowDefinitionId());
        assertEquals(createDefinitionOutput.flowDefinitionId(), terminateOutput.flowDefinitionId());
    }

}
