package org.mojave.core.participant.domain.command.ssp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;
import org.mojave.core.participant.contract.command.ssp.ActivateSspCommand;
import org.mojave.core.participant.contract.command.ssp.ActivateSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.AddSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.ChangeSspEndpointCommand;
import org.mojave.core.participant.contract.command.ssp.ChangeSspNameCommand;
import org.mojave.core.participant.contract.command.ssp.CreateSspCommand;
import org.mojave.core.participant.contract.command.ssp.DeactivateSspCommand;
import org.mojave.core.participant.contract.command.ssp.DeactivateSspCurrencyCommand;
import org.mojave.core.participant.contract.command.ssp.TerminateSspCommand;
import org.mojave.core.participant.contract.exception.ssp.SspIdNotFoundException;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.core.participant.domain.BaseIT;
import org.mojave.core.participant.domain.ParticipantDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantDomainTestConfiguration.class})
@DisplayName("Ssp Commands Integration Test")
public class SspLifecycleCommandIT extends BaseIT {

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
    private SspQuery sspQuery;

    @Autowired
    private TerminateSspCommand terminateSspCommand;

    @Test
    @DisplayName("Execute ssp command lifecycle successfully")
    public void lifecycleSuccessful() {

        this.createHub(this.createHubCommand, "hub-ssp-1", Currency.USD, Currency.BYN);

        final var createSspOutput = this.createSspCommand.execute(new CreateSspCommand.Input(
            new SspCode("ssp1"),
            "Ssp 1",
            new Currency[]{Currency.USD},
            "http://localhost:3601/ssp"));

        final var sspId = createSspOutput.sspId();

        this.changeSspNameCommand.execute(new ChangeSspNameCommand.Input(sspId, "Ssp 1 Updated"));

        this.changeSspEndpointCommand.execute(new ChangeSspEndpointCommand.Input(
            sspId,
            "http://localhost:3601/ssp/v2"));

        final var addCurrencyOutput = this.addSspCurrencyCommand.execute(
            new AddSspCurrencyCommand.Input(sspId, Currency.BYN));

        final var deactivateCurrencyOutput = this.deactivateSspCurrencyCommand.execute(
            new DeactivateSspCurrencyCommand.Input(sspId, Currency.BYN));

        final var activateCurrencyOutput = this.activateSspCurrencyCommand.execute(
            new ActivateSspCurrencyCommand.Input(sspId, Currency.BYN));

        this.deactivateSspCommand.execute(new DeactivateSspCommand.Input(sspId));

        assertThrows(SspIdNotFoundException.class, () -> this.sspQuery.get(sspId));

        this.activateSspCommand.execute(new ActivateSspCommand.Input(sspId));

        final var activeSsp = this.sspQuery.get(new SspId(sspId.getId()));

        this.terminateSspCommand.execute(new TerminateSspCommand.Input(sspId));

        assertThrows(SspIdNotFoundException.class, () -> this.sspQuery.get(sspId));

        assertEquals("Ssp 1 Updated", activeSsp.name());
        assertEquals("http://localhost:3601/ssp/v2", activeSsp.baseUrl());

        assertNotNull(addCurrencyOutput.sspCurrencyId());
        assertEquals(addCurrencyOutput.sspCurrencyId(), deactivateCurrencyOutput.sspCurrencyId());
        assertFalse(deactivateCurrencyOutput.changed());
        assertTrue(activateCurrencyOutput.activated());
    }

    @Test
    @DisplayName("Throw when activating terminated ssp")
    public void activateTerminatedSsp() {

        this.createHub(this.createHubCommand, "hub-ssp-2", Currency.USD, Currency.BYN);

        final var createSspOutput = this.createSspCommand.execute(new CreateSspCommand.Input(
            new SspCode("ssp2"),
            "Ssp 2",
            new Currency[]{Currency.USD},
            "http://localhost:3602/ssp"));

        final var sspId = createSspOutput.sspId();

        this.terminateSspCommand.execute(new TerminateSspCommand.Input(sspId));

        assertThrows(
            SspIdNotFoundException.class,
            () -> this.activateSspCommand.execute(new ActivateSspCommand.Input(sspId)));
    }

}
