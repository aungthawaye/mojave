package org.mojave.core.accounting.domain.command.definition;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.command.definition.ActivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import org.mojave.core.accounting.contract.command.definition.ChangeFlowDefinitionPropertiesCommand;
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.DeactivateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.command.definition.RemoveFlowDefinitionLineCommand;
import org.mojave.core.accounting.contract.command.definition.TerminateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionAlreadyConfiguredException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionLineNotFoundException;
import org.mojave.core.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.core.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.core.accounting.domain.BaseIT;
import org.mojave.scheme.rule.enums.ActivationStatus;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.TerminationStatus;
import org.mojave.scheme.rule.enums.accounting.AccountType;
import org.mojave.scheme.rule.enums.accounting.Side;
import org.mojave.scheme.rule.identifier.accounting.FlowDefinitionId;
import org.mojave.scheme.rule.identifier.accounting.FlowDefinitionLineId;
import org.mojave.scheme.rule.scenario.ScenarioType;
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
    private RemoveFlowDefinitionLineCommand removeFlowDefinitionLineCommand;

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
    @DisplayName("Throw when removing flow definition line from missing definition")
    public void removeFlowDefinitionLineDefinitionNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class, () -> this.removeFlowDefinitionLineCommand.execute(
                new RemoveFlowDefinitionLineCommand.Input(
                    new FlowDefinitionId(Long.MAX_VALUE),
                    new FlowDefinitionLineId(Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("Throw when flow definition line cannot be found")
    public void removeFlowDefinitionLineNotFound() {

        final var fixture = this.createTransferDefinition("REMOVE_FLOW_DEFINITION_LINE_MISSING", Currency.USD);

        assertThrows(
            FlowDefinitionLineNotFoundException.class, () -> this.removeFlowDefinitionLineCommand.execute(
                new RemoveFlowDefinitionLineCommand.Input(
                    fixture.flowDefinitionId(),
                    new FlowDefinitionLineId(Long.MAX_VALUE))));
    }

    @Test
    @DisplayName("Remove flow definition line successfully")
    public void removeFlowDefinitionLineSuccessful() {

        final var fixture = this.createTransferDefinition("REMOVE_FLOW_DEFINITION_LINE", Currency.USD);

        final var output = this.removeFlowDefinitionLineCommand.execute(
            new RemoveFlowDefinitionLineCommand.Input(fixture.flowDefinitionId(), fixture.payeeFlowDefinitionLineId()));
        final var definition = this.flowDefinitionQuery.get(fixture.flowDefinitionId());

        assertEquals(fixture.flowDefinitionId(), output.flowDefinitionId());
        assertEquals(1, definition.flowDefinitionLines().size());
        assertEquals(fixture.payerFlowDefinitionLineId(), definition.flowDefinitionLines().getFirst().flowDefinitionLineId());
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
                ScenarioType.P2P_TRANSFER, currency, prefix + "-flow",
                prefix + "-flow description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    1, "PAYER_FSP", payerCoaEntryId,
                    "TRANSFER_AMOUNT", Side.DEBIT, prefix + " payer line"),
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    2, "PAYEE_FSP", payeeCoaEntryId, "PAYEE_FSP_FEE", Side.CREDIT,
                    prefix + " payee line"))));

        return new TransferDefinitionFixture(
            output.flowDefinitionId(),
            output.flowDefinitionLineIds().getFirst(), output.flowDefinitionLineIds().get(1));
    }

    private record TransferDefinitionFixture(FlowDefinitionId flowDefinitionId,
                                             FlowDefinitionLineId payerFlowDefinitionLineId,
                                             FlowDefinitionLineId payeeFlowDefinitionLineId) { }

}
