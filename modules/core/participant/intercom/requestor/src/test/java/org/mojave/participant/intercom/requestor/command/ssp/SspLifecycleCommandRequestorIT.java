package org.mojave.participant.intercom.requestor.command.ssp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.type.participant.SspCode;
import org.mojave.participant.contract.command.hub.CreateHubCommand;
import org.mojave.participant.contract.command.ssp.ActivateSspCommand;
import org.mojave.participant.contract.command.ssp.ActivateSspCurrencyCommand;
import org.mojave.participant.contract.command.ssp.AddSspCurrencyCommand;
import org.mojave.participant.contract.command.ssp.ChangeSspEndpointCommand;
import org.mojave.participant.contract.command.ssp.ChangeSspNameCommand;
import org.mojave.participant.contract.command.ssp.CreateSspCommand;
import org.mojave.participant.contract.command.ssp.DeactivateSspCommand;
import org.mojave.participant.contract.command.ssp.DeactivateSspCurrencyCommand;
import org.mojave.participant.contract.command.ssp.TerminateSspCommand;
import org.mojave.participant.contract.exception.ssp.SspIdNotFoundException;
import org.mojave.participant.intercom.requestor.ParticipantIntercomRequestorTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantIntercomRequestorTestConfiguration.class})
@DisplayName("Ssp Commands Requestor Integration Test")
public class SspLifecycleCommandRequestorIT {

    @Autowired
    private ActivateSspCommand activateSspCommand;

    @Autowired
    private ActivateSspCurrencyCommand activateSspCurrencyCommand;

    @Autowired
    private AddSspCurrencyCommand addSspCurrencyCommand;

    @Autowired
    private ChangeSspEndpointCommand changeSspEndpointCommand;

    @Autowired
    private ChangeSspNameCommand changeSspNameCommand;

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private CreateSspCommand createSspCommand;

    @Autowired
    private DeactivateSspCommand deactivateSspCommand;

    @Autowired
    private DeactivateSspCurrencyCommand deactivateSspCurrencyCommand;

    @Autowired
    private TerminateSspCommand terminateSspCommand;

    @Test
    @DisplayName("Execute ssp command lifecycle through requestor")
    public void lifecycleSuccessful() {

        final var suffix = Long.toString(System.currentTimeMillis());

        this.createHubCommand.execute(
            new CreateHubCommand.Input("requestor-ssp-hub-" + suffix, new Currency[]{Currency.USD, Currency.BYN}));

        final var createSspOutput = this.createSspCommand.execute(
            new CreateSspCommand.Input(
                new SspCode("ssp" + suffix.substring(suffix.length() - 4)),
                "requestor-ssp-" + suffix,
                new Currency[]{Currency.USD},
                "http://localhost:46" + suffix.substring(suffix.length() - 2) + "/ssp"));

        this.changeSspNameCommand.execute(
            new ChangeSspNameCommand.Input(createSspOutput.sspId(), "requestor-ssp-updated-" + suffix));

        final var changeEndpointOutput = this.changeSspEndpointCommand.execute(
            new ChangeSspEndpointCommand.Input(
                createSspOutput.sspId(),
                "http://localhost:46" + suffix.substring(suffix.length() - 2) + "/ssp/v2"));

        final var addCurrencyOutput = this.addSspCurrencyCommand.execute(
            new AddSspCurrencyCommand.Input(createSspOutput.sspId(), Currency.BYN));

        final var deactivateCurrencyOutput = this.deactivateSspCurrencyCommand.execute(
            new DeactivateSspCurrencyCommand.Input(createSspOutput.sspId(), Currency.BYN));

        final var activateCurrencyOutput = this.activateSspCurrencyCommand.execute(
            new ActivateSspCurrencyCommand.Input(createSspOutput.sspId(), Currency.BYN));

        this.deactivateSspCommand.execute(new DeactivateSspCommand.Input(createSspOutput.sspId()));

        this.activateSspCommand.execute(new ActivateSspCommand.Input(createSspOutput.sspId()));

        this.terminateSspCommand.execute(new TerminateSspCommand.Input(createSspOutput.sspId()));

        assertNotNull(createSspOutput.sspId());
        assertTrue(changeEndpointOutput.changed());
        assertNotNull(addCurrencyOutput.sspCurrencyId());
        assertEquals(addCurrencyOutput.sspCurrencyId(), deactivateCurrencyOutput.sspCurrencyId());
        assertTrue(activateCurrencyOutput.activated());

        assertThrows(
            SspIdNotFoundException.class,
            () -> this.activateSspCommand.execute(new ActivateSspCommand.Input(new SspId(createSspOutput.sspId().getId()))));
    }

}

