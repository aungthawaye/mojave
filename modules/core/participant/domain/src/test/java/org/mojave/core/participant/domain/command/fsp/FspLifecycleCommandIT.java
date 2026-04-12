package org.mojave.core.participant.domain.command.fsp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.participant.EndpointType;
import org.mojave.scheme.rule.identifier.participant.FspId;
import org.mojave.core.participant.contract.command.fsp.ActivateEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.ActivateFspCommand;
import org.mojave.core.participant.contract.command.fsp.ActivateFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.AddEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.AddFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.ChangeFspEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.ChangeFspNameCommand;
import org.mojave.core.participant.contract.command.fsp.CreateFspCommand;
import org.mojave.core.participant.contract.command.fsp.CreateFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateEndpointCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateFspCommand;
import org.mojave.core.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import org.mojave.core.participant.contract.command.fsp.JoinFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.LeaveFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.RemoveFspGroupCommand;
import org.mojave.core.participant.contract.command.fsp.TerminateFspCommand;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;
import org.mojave.core.participant.contract.exception.fsp.FspGroupIdNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.contract.query.FspGroupQuery;
import org.mojave.core.participant.contract.query.FspQuery;
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
@DisplayName("Fsp Commands Integration Test")
public class FspLifecycleCommandIT extends BaseIT {

    @Autowired
    private ActivateEndpointCommand activateEndpointCommand;

    @Autowired
    private ActivateFspCommand activateFspCommand;

    @Autowired
    private ActivateFspCurrencyCommand activateFspCurrencyCommand;

    @Autowired
    private AddEndpointCommand addEndpointCommand;

    @Autowired
    private AddFspCurrencyCommand addFspCurrencyCommand;

    @Autowired
    private ChangeFspEndpointCommand changeFspEndpointCommand;

    @Autowired
    private ChangeFspNameCommand changeFspNameCommand;

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private CreateFspGroupCommand createFspGroupCommand;

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private DeactivateEndpointCommand deactivateEndpointCommand;

    @Autowired
    private DeactivateFspCommand deactivateFspCommand;

    @Autowired
    private DeactivateFspCurrencyCommand deactivateFspCurrencyCommand;

    @Autowired
    private FspGroupQuery fspGroupQuery;

    @Autowired
    private FspQuery fspQuery;

    @Autowired
    private JoinFspGroupCommand joinFspGroupCommand;

    @Autowired
    private LeaveFspGroupCommand leaveFspGroupCommand;

    @Autowired
    private RemoveFspGroupCommand removeFspGroupCommand;

    @Autowired
    private TerminateFspCommand terminateFspCommand;

    @Test
    @DisplayName("Execute fsp and fsp-group command lifecycle successfully")
    public void lifecycleSuccessful() {

        this.createHub(this.createHubCommand, "hub-1", Currency.USD, Currency.BYN);

        final var createFspOutput = this.createFsp(
            this.createFspCommand, "fsp-1", "Fsp 1", new Currency[]{Currency.USD},
            new CreateFspCommand.Input.Endpoint[]{
                this.endpoint(EndpointType.PARTIES, "http://localhost:3301/parties")
            });

        final var fspId = createFspOutput.fspId();

        this.changeFspNameCommand.execute(new ChangeFspNameCommand.Input(fspId, "Fsp 1 Updated"));

        final var addCurrencyOutput = this.addFspCurrencyCommand.execute(
            new AddFspCurrencyCommand.Input(fspId, Currency.BYN));

        final var deactivateCurrencyOutput = this.deactivateFspCurrencyCommand.execute(
            new DeactivateFspCurrencyCommand.Input(fspId, Currency.BYN));

        final var activateCurrencyOutput = this.activateFspCurrencyCommand.execute(
            new ActivateFspCurrencyCommand.Input(fspId, Currency.BYN));

        final var addEndpointOutput = this.addEndpointCommand.execute(
            new AddEndpointCommand.Input(fspId, EndpointType.QUOTES, "http://localhost:3301/quotes"));

        final var changeEndpointOutput = this.changeFspEndpointCommand.execute(
            new ChangeFspEndpointCommand.Input(
                fspId, EndpointType.QUOTES, "http://localhost:3301/quotes/v2"));

        final var deactivateEndpointOutput = this.deactivateEndpointCommand.execute(
            new DeactivateEndpointCommand.Input(fspId, EndpointType.QUOTES));

        final var activateEndpointOutput = this.activateEndpointCommand.execute(
            new ActivateEndpointCommand.Input(fspId, EndpointType.QUOTES));

        final var createGroupOutput = this.createFspGroupCommand.execute(
            new CreateFspGroupCommand.Input("fsp-group-1"));

        this.joinFspGroupCommand.execute(new JoinFspGroupCommand.Input(
            createGroupOutput.fspGroupId(), fspId));

        final var groupAfterJoin = this.fspGroupQuery.get(createGroupOutput.fspGroupId());

        this.leaveFspGroupCommand.execute(new LeaveFspGroupCommand.Input(
            createGroupOutput.fspGroupId(), fspId));

        final var groupAfterLeave = this.fspGroupQuery.get(createGroupOutput.fspGroupId());

        this.removeFspGroupCommand.execute(new RemoveFspGroupCommand.Input(createGroupOutput.fspGroupId()));

        this.deactivateFspCommand.execute(new DeactivateFspCommand.Input(fspId));

        assertThrows(FspIdNotFoundException.class, () -> this.fspQuery.get(fspId));

        this.activateFspCommand.execute(new ActivateFspCommand.Input(fspId));

        final var activeFsp = this.fspQuery.get(new FspId(fspId.getId()));

        this.terminateFspCommand.execute(new TerminateFspCommand.Input(fspId));

        assertThrows(FspIdNotFoundException.class, () -> this.fspQuery.get(fspId));
        assertThrows(
            FspGroupIdNotFoundException.class,
            () -> this.fspGroupQuery.get(createGroupOutput.fspGroupId()));

        assertEquals("Fsp 1 Updated", activeFsp.name());

        assertNotNull(addCurrencyOutput.fspCurrencyId());
        assertEquals(addCurrencyOutput.fspCurrencyId(), deactivateCurrencyOutput.fspCurrencyId());
        assertFalse(deactivateCurrencyOutput.changed());
        assertTrue(activateCurrencyOutput.activated());

        assertNotNull(addEndpointOutput.fspEndpointId());
        assertEquals(addEndpointOutput.fspEndpointId(), changeEndpointOutput.fspEndpointId());
        assertTrue(changeEndpointOutput.changed());
        assertEquals(addEndpointOutput.fspEndpointId(), deactivateEndpointOutput.fspEndpointId());
        assertFalse(deactivateEndpointOutput.deactivated());
        assertTrue(activateEndpointOutput.activated());

        assertEquals(1, groupAfterJoin.fspIds().size());
        assertEquals(fspId, groupAfterJoin.fspIds().getFirst());
        assertTrue(groupAfterLeave.fspIds().isEmpty());
    }

    @Test
    @DisplayName("Throw when activating terminated fsp")
    public void activateTerminatedFsp() {

        this.createHub(this.createHubCommand, "hub-2", Currency.USD, Currency.BYN);

        final var createFspOutput = this.createFsp(
            this.createFspCommand, "fsp-2", "Fsp 2", new Currency[]{Currency.USD},
            new CreateFspCommand.Input.Endpoint[]{
                this.endpoint(EndpointType.PARTIES, "http://localhost:3401/parties")
            });

        final var fspId = createFspOutput.fspId();

        this.terminateFspCommand.execute(new TerminateFspCommand.Input(fspId));

        assertThrows(
            FspIdNotFoundException.class,
            () -> this.activateFspCommand.execute(new ActivateFspCommand.Input(fspId)));
    }

}
