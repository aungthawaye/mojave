package org.mojave.accounting.domain.command.definition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.accounting.contract.command.definition.ActivateFlowDefinitionCommand;
import org.mojave.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import org.mojave.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
import org.mojave.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.accounting.contract.command.definition.DeactivateFlowDefinitionCommand;
import org.mojave.accounting.contract.command.definition.RemoveFlowLineCommand;
import org.mojave.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionAlreadyConfiguredException;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import org.mojave.accounting.contract.exception.definition.FlowLineNotFoundException;
import org.mojave.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.accounting.domain.BaseIT;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.enums.accounting.Side;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.identifier.accounting.FlowLineId;
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Flow Definition Lifecycle Commands Integration Test")
public class FlowDefinitionLifecycleCommandIT extends BaseIT {

    @Autowired
    private ActivateFlowDefinitionCommand activateFlowDefinitionCommand;

    @Autowired
    private ChangeFlowDefinitionCurrencyCommand changeFlowDefinitionCurrencyCommand;

    @Autowired
    private ChangeFlowDefinitionPropertiesCommand changeFlowDefinitionPropertiesCommand;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private DeactivateFlowDefinitionCommand deactivateFlowDefinitionCommand;

    @Autowired
    private FlowDefinitionQuery flowDefinitionQuery;

    @Autowired
    private RemoveFlowLineCommand removeFlowLineCommand;

    @Autowired
    private TerminateFlowDefinitionCommand terminateFlowDefinitionCommand;

    @Test
    @DisplayName("Throw when activating missing flow definition")
    public void activateFlowDefinitionNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class, () -> this.activateFlowDefinitionCommand.execute(
                new ActivateFlowDefinitionCommand.Input(new FlowDefinitionId(Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("Activate flow definition successfully")
    public void activateFlowDefinitionSuccessful() {

        final var fixture = this.createTransferDefinition("ACTIVATE_FLOW", Currency.USD);

        this.deactivateFlowDefinitionCommand.execute(
            new DeactivateFlowDefinitionCommand.Input(fixture.flowDefinitionId()));

        final var output = this.activateFlowDefinitionCommand.execute(
            new ActivateFlowDefinitionCommand.Input(fixture.flowDefinitionId()));
        final var definition = this.flowDefinitionQuery.get(fixture.flowDefinitionId());

        assertEquals(fixture.flowDefinitionId(), output.flowDefinitionId());
        assertEquals(ActivationStatus.ACTIVE, definition.activationStatus());
    }

    @Test
    @DisplayName("Throw when target currency is already configured")
    public void changeFlowDefinitionCurrencyAlreadyConfigured() {

        this.createTransferDefinition("CHANGE_FLOW_CURRENCY_01", Currency.USD);
        final var secondFixture = this.createTransferDefinition(
            "CHANGE_FLOW_CURRENCY_02", Currency.EUR);

        assertThrows(
            FlowDefinitionAlreadyConfiguredException.class,
            () -> this.changeFlowDefinitionCurrencyCommand.execute(
                new ChangeFlowDefinitionCurrencyCommand.Input(
                    secondFixture.flowDefinitionId(),
                    Currency.USD)));
        assertEquals(2, this.flowDefinitionQuery.getAll().size());
    }

    @Test
    @DisplayName("Throw when changing currency for missing flow definition")
    public void changeFlowDefinitionCurrencyNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class,
            () -> this.changeFlowDefinitionCurrencyCommand.execute(
                new ChangeFlowDefinitionCurrencyCommand.Input(
                    new FlowDefinitionId(Long.MAX_VALUE),
                    Currency.USD)));
    }

    @Test
    @DisplayName("Change flow definition currency successfully")
    public void changeFlowDefinitionCurrencySuccessful() {

        final var fixture = this.createTransferDefinition("CHANGE_FLOW_CURRENCY", Currency.USD);

        final var output = this.changeFlowDefinitionCurrencyCommand.execute(
            new ChangeFlowDefinitionCurrencyCommand.Input(
                fixture.flowDefinitionId(),
                Currency.EUR));
        final var definition = this.flowDefinitionQuery.get(fixture.flowDefinitionId());

        assertEquals(fixture.flowDefinitionId(), output.flowDefinitionId());
        assertEquals(Currency.EUR, definition.currency());
    }

    @Test
    @DisplayName("Throw when flow definition name is already taken")
    public void changeFlowDefinitionPropertiesNameTaken() {

        final var firstFixture = this.createTransferDefinition(
            "CHANGE_FLOW_NAME_TAKEN_01", Currency.USD);
        final var secondFixture = this.createTransferDefinition(
            "CHANGE_FLOW_NAME_TAKEN_02", Currency.EUR);

        assertThrows(
            FlowDefinitionNameTakenException.class,
            () -> this.changeFlowDefinitionPropertiesCommand.execute(
                new ChangeFlowDefinitionPropertiesCommand.Input(
                    secondFixture.flowDefinitionId(), "CHANGE_FLOW_NAME_TAKEN_01-flow",
                    "Duplicate name")));
    }

    @Test
    @DisplayName("Throw when changing properties for missing flow definition")
    public void changeFlowDefinitionPropertiesNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class,
            () -> this.changeFlowDefinitionPropertiesCommand.execute(
                new ChangeFlowDefinitionPropertiesCommand.Input(
                    new FlowDefinitionId(Long.MAX_VALUE), "Missing", "Missing")));
    }

    @Test
    @DisplayName("Change flow definition properties successfully")
    public void changeFlowDefinitionPropertiesSuccessful() {

        final var fixture = this.createTransferDefinition("CHANGE_FLOW_PROPERTIES", Currency.USD);

        final var output = this.changeFlowDefinitionPropertiesCommand.execute(
            new ChangeFlowDefinitionPropertiesCommand.Input(
                fixture.flowDefinitionId(), "Updated Flow Definition",
                "Updated Flow Definition Description"));
        final var definition = this.flowDefinitionQuery.get(fixture.flowDefinitionId());

        assertEquals(fixture.flowDefinitionId(), output.flowDefinitionId());
        assertEquals("Updated Flow Definition", definition.name());
        assertEquals("Updated Flow Definition Description", definition.description());
    }

    @Test
    @DisplayName("Change flow definition description when name is null")
    public void changeFlowDefinitionPropertiesWithNullName() {

        final var fixture = this.createTransferDefinition("CHANGE_FLOW_NULL_NAME", Currency.USD);

        this.changeFlowDefinitionPropertiesCommand.execute(
            new ChangeFlowDefinitionPropertiesCommand.Input(
                fixture.flowDefinitionId(), null,
                "Description Only"));

        final var definition = this.flowDefinitionQuery.get(fixture.flowDefinitionId());

        assertEquals("CHANGE_FLOW_NULL_NAME-flow", definition.name());
        assertEquals("Description Only", definition.description());
    }

    @Test
    @DisplayName("Throw when deactivating missing flow definition")
    public void deactivateFlowDefinitionNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class,
            () -> this.deactivateFlowDefinitionCommand.execute(
                new DeactivateFlowDefinitionCommand.Input(new FlowDefinitionId(Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("Deactivate flow definition successfully")
    public void deactivateFlowDefinitionSuccessful() {

        final var fixture = this.createTransferDefinition("DEACTIVATE_FLOW", Currency.USD);

        final var output = this.deactivateFlowDefinitionCommand.execute(
            new DeactivateFlowDefinitionCommand.Input(fixture.flowDefinitionId()));
        final var definition = this.flowDefinitionQuery.get(fixture.flowDefinitionId());

        assertEquals(fixture.flowDefinitionId(), output.flowDefinitionId());
        assertEquals(ActivationStatus.INACTIVE, definition.activationStatus());
    }

    @Test
    @DisplayName("Throw when removing flow line from missing definition")
    public void removeFlowLineDefinitionNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class, () -> this.removeFlowLineCommand.execute(
                new RemoveFlowLineCommand.Input(
                    new FlowDefinitionId(Long.MAX_VALUE),
                    new FlowLineId(Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("Throw when flow line cannot be found")
    public void removeFlowLineNotFound() {

        final var fixture = this.createTransferDefinition("REMOVE_FLOW_LINE_MISSING", Currency.USD);

        assertThrows(
            FlowLineNotFoundException.class, () -> this.removeFlowLineCommand.execute(
                new RemoveFlowLineCommand.Input(
                    fixture.flowDefinitionId(),
                    new FlowLineId(Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("Remove flow line successfully")
    public void removeFlowLineSuccessful() {

        final var fixture = this.createTransferDefinition("REMOVE_FLOW_LINE", Currency.USD);

        final var output = this.removeFlowLineCommand.execute(
            new RemoveFlowLineCommand.Input(fixture.flowDefinitionId(), fixture.payeeFlowLineId()));
        final var definition = this.flowDefinitionQuery.get(fixture.flowDefinitionId());

        assertEquals(fixture.flowDefinitionId(), output.flowDefinitionId());
        assertEquals(1, definition.flowLines().size());
        assertEquals(fixture.payerFlowLineId(), definition.flowLines().getFirst().flowLineId());
    }

    @Test
    @DisplayName("Throw when terminating missing flow definition")
    public void terminateFlowDefinitionNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class,
            () -> this.terminateFlowDefinitionCommand.execute(
                new TerminateFlowDefinitionCommand.Input(new FlowDefinitionId(Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("Terminate flow definition successfully")
    public void terminateFlowDefinitionSuccessful() {

        final var fixture = this.createTransferDefinition("TERMINATE_FLOW", Currency.USD);

        final var output = this.terminateFlowDefinitionCommand.execute(
            new TerminateFlowDefinitionCommand.Input(fixture.flowDefinitionId()));
        final var definition = this.flowDefinitionQuery.get(fixture.flowDefinitionId());

        assertEquals(fixture.flowDefinitionId(), output.flowDefinitionId());
        assertEquals(ActivationStatus.INACTIVE, definition.activationStatus());
        assertEquals(TerminationStatus.TERMINATED, definition.terminationStatus());
    }

    private String code(final String prefix, final String suffix) {

        final var compactPrefix = prefix.replace("_", "").replace("-", "");
        final var base =
            compactPrefix.length() > 12 ? compactPrefix.substring(0, 12) : compactPrefix;
        final var hash = Integer.toHexString(compactPrefix.hashCode()).toUpperCase();

        return base + "_" + hash + "_" + suffix;
    }

    private TransferDefinitionFixture createTransferDefinition(final String prefix,
                                                               final Currency currency) {

        final var coaId = this.createCoa(this.createCoaCommand, prefix + "-coa");

        final var payerCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", this.code(prefix, "PAYER01"),
            prefix + " Payer 01", AccountType.ASSET);

        final var payeeCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", this.code(prefix, "PAYEE01"),
            prefix + " Payee 01", AccountType.REVENUE);

        this.createAccount(
            this.createAccountCommand, payerCoaEntryId, Math.abs(prefix.hashCode()) + 700L,
            currency, this.code(prefix, "ACC01"), prefix + " Account 01");
        this.createAccount(
            this.createAccountCommand, payeeCoaEntryId, Math.abs(prefix.hashCode()) + 800L,
            currency, this.code(prefix, "ACC02"), prefix + " Account 02");

        final var output = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                AccountingScenario.FUND_TRANSFER, currency, prefix + "-flow",
                prefix + "-flow description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowLine(
                    1, "PAYER_FSP", payerCoaEntryId,
                    "TRANSFER_AMOUNT", Side.DEBIT, prefix + " payer line"),
                new CreateFlowDefinitionCommand.Input.FlowLine(
                    2, "PAYEE_FSP", payeeCoaEntryId, "PAYEE_FSP_FEE", Side.CREDIT,
                    prefix + " payee line"))));

        return new TransferDefinitionFixture(
            output.flowDefinitionId(),
            output.flowLineIds().getFirst(), output.flowLineIds().get(1));
    }

    private record TransferDefinitionFixture(FlowDefinitionId flowDefinitionId,
                                             FlowLineId payerFlowLineId,
                                             FlowLineId payeeFlowLineId) { }

}
