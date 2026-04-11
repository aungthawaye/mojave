package org.mojave.core.participant.domain.command.hub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.core.participant.contract.command.hub.ActivateHubCurrencyCommand;
import org.mojave.core.participant.contract.command.hub.AddHubCurrencyCommand;
import org.mojave.core.participant.contract.command.hub.ChangeHubNameCommand;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;
import org.mojave.core.participant.contract.command.hub.DeactivateHubCurrencyCommand;
import org.mojave.core.participant.contract.data.HubData;
import org.mojave.core.participant.contract.query.HubQuery;
import org.mojave.core.participant.domain.BaseIT;
import org.mojave.core.participant.domain.ParticipantDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantDomainTestConfiguration.class})
@DisplayName("Hub Commands Integration Test")
public class HubLifecycleCommandIT extends BaseIT {

    @Autowired
    private ActivateHubCurrencyCommand activateHubCurrencyCommand;

    @Autowired
    private AddHubCurrencyCommand addHubCurrencyCommand;

    @Autowired
    private ChangeHubNameCommand changeHubNameCommand;

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private DeactivateHubCurrencyCommand deactivateHubCurrencyCommand;

    @Autowired
    private HubQuery hubQuery;

    @Test
    @DisplayName("Execute hub command lifecycle successfully")
    public void lifecycleSuccessful() {

        final var createOutput = this.createHub(
            this.createHubCommand, "hub-1", Currency.USD);
        final var hubId = createOutput.hubId();

        final var addCurrencyOutput = this.addHubCurrencyCommand.execute(
            new AddHubCurrencyCommand.Input(hubId, Currency.BYN));

        final var deactivateCurrencyOutput = this.deactivateHubCurrencyCommand.execute(
            new DeactivateHubCurrencyCommand.Input(hubId, Currency.BYN));

        final var activateCurrencyOutput = this.activateHubCurrencyCommand.execute(
            new ActivateHubCurrencyCommand.Input(hubId, Currency.BYN));

        this.changeHubNameCommand.execute(new ChangeHubNameCommand.Input(hubId, "hub-1-updated"));

        final var hub = this.hubQuery.get();
        final var currencyStatusByCurrency = Arrays
                                                 .stream(hub.currencies())
                                                 .collect(Collectors.toMap(
                                                     HubData.HubCurrencyData::currency,
                                                     HubData.HubCurrencyData::activationStatus));

        assertNotNull(addCurrencyOutput.hubCurrencyId());

        assertEquals(addCurrencyOutput.hubCurrencyId(), deactivateCurrencyOutput.hubCurrencyId());
        assertTrue(deactivateCurrencyOutput.deactivated());

        assertTrue(activateCurrencyOutput.activated());

        assertEquals(hubId, hub.hubId());
        assertEquals("hub-1-updated", hub.name());
        assertEquals(2, hub.currencies().length);

        assertEquals(ActivationStatus.ACTIVE, currencyStatusByCurrency.get(Currency.USD));
        assertEquals(ActivationStatus.ACTIVE, currencyStatusByCurrency.get(Currency.BYN));
    }

    @Test
    @DisplayName("Return false when activating and deactivating unknown hub currency")
    public void activateDeactivateUnknownCurrency() {

        final var createOutput = this.createHub(
            this.createHubCommand, "hub-2", Currency.USD);

        final var deactivateOutput = this.deactivateHubCurrencyCommand.execute(
            new DeactivateHubCurrencyCommand.Input(createOutput.hubId(), Currency.BYN));

        final var activateOutput = this.activateHubCurrencyCommand.execute(
            new ActivateHubCurrencyCommand.Input(createOutput.hubId(), Currency.BYN));

        assertNull(deactivateOutput.hubCurrencyId());
        assertFalse(deactivateOutput.deactivated());
        assertFalse(activateOutput.activated());
    }

}
