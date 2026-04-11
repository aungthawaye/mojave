package org.mojave.participant.intercom.requestor.command.hub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.participant.contract.command.hub.ActivateHubCurrencyCommand;
import org.mojave.participant.contract.command.hub.AddHubCurrencyCommand;
import org.mojave.participant.contract.command.hub.ChangeHubNameCommand;
import org.mojave.participant.contract.command.hub.CreateHubCommand;
import org.mojave.participant.contract.command.hub.DeactivateHubCurrencyCommand;
import org.mojave.participant.intercom.requestor.ParticipantIntercomRequestorTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantIntercomRequestorTestConfiguration.class})
@DisplayName("Hub Commands Requestor Integration Test")
public class HubLifecycleCommandRequestorIT {

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

    @Test
    @DisplayName("Execute hub command lifecycle through requestor")
    public void lifecycleSuccessful() {

        final var suffix = Long.toString(System.currentTimeMillis());

        final var createHubOutput = this.createHubCommand.execute(
            new CreateHubCommand.Input("requestor-hub-" + suffix, new Currency[]{Currency.USD}));

        final var addCurrencyOutput = this.addHubCurrencyCommand.execute(
            new AddHubCurrencyCommand.Input(createHubOutput.hubId(), Currency.BYN));

        final var deactivateCurrencyOutput = this.deactivateHubCurrencyCommand.execute(
            new DeactivateHubCurrencyCommand.Input(createHubOutput.hubId(), Currency.BYN));

        final var activateCurrencyOutput = this.activateHubCurrencyCommand.execute(
            new ActivateHubCurrencyCommand.Input(createHubOutput.hubId(), Currency.BYN));

        this.changeHubNameCommand.execute(
            new ChangeHubNameCommand.Input(createHubOutput.hubId(), "requestor-hub-updated-" + suffix));

        assertNotNull(createHubOutput.hubId());
        assertNotNull(addCurrencyOutput.hubCurrencyId());
        assertEquals(addCurrencyOutput.hubCurrencyId(), deactivateCurrencyOutput.hubCurrencyId());
        assertTrue(activateCurrencyOutput.activated());
    }

}

