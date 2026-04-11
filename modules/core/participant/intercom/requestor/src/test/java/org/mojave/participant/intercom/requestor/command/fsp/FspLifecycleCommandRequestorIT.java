package org.mojave.participant.intercom.requestor.command.fsp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.participant.contract.command.fsp.ActivateEndpointCommand;
import org.mojave.participant.contract.command.fsp.ActivateFspCommand;
import org.mojave.participant.contract.command.fsp.ActivateFspCurrencyCommand;
import org.mojave.participant.contract.command.fsp.AddEndpointCommand;
import org.mojave.participant.contract.command.fsp.AddFspCurrencyCommand;
import org.mojave.participant.contract.command.fsp.ChangeFspEndpointCommand;
import org.mojave.participant.contract.command.fsp.ChangeFspNameCommand;
import org.mojave.participant.contract.command.fsp.CreateFspCommand;
import org.mojave.participant.contract.command.fsp.CreateFspGroupCommand;
import org.mojave.participant.contract.command.fsp.DeactivateEndpointCommand;
import org.mojave.participant.contract.command.fsp.DeactivateFspCommand;
import org.mojave.participant.contract.command.fsp.DeactivateFspCurrencyCommand;
import org.mojave.participant.contract.command.fsp.JoinFspGroupCommand;
import org.mojave.participant.contract.command.fsp.LeaveFspGroupCommand;
import org.mojave.participant.contract.command.fsp.RemoveFspGroupCommand;
import org.mojave.participant.contract.command.fsp.TerminateFspCommand;
import org.mojave.participant.contract.command.hub.CreateHubCommand;
import org.mojave.participant.contract.exception.fsp.FspIdNotFoundException;
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
@DisplayName("Fsp Commands Requestor Integration Test")
public class FspLifecycleCommandRequestorIT {

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
    private JoinFspGroupCommand joinFspGroupCommand;

    @Autowired
    private LeaveFspGroupCommand leaveFspGroupCommand;

    @Autowired
    private RemoveFspGroupCommand removeFspGroupCommand;

    @Autowired
    private TerminateFspCommand terminateFspCommand;

    @Test
    @DisplayName("Execute fsp and fsp-group command lifecycle through requestor")
    public void lifecycleSuccessful() {

        final var suffix = Long.toString(System.currentTimeMillis());

        this.createHubCommand.execute(
            new CreateHubCommand.Input("requestor-fsp-hub-" + suffix, new Currency[]{Currency.USD, Currency.BYN}));

        final var createFspOutput = this.createFspCommand.execute(
            new CreateFspCommand.Input(
                new FspCode("fsp" + suffix.substring(suffix.length() - 4)),
                "requestor-fsp-" + suffix,
                new Currency[]{Currency.USD},
                new CreateFspCommand.Input.Endpoint[]{
                    new CreateFspCommand.Input.Endpoint(
                        EndpointType.PARTIES,
                        "http://localhost:47" + suffix.substring(suffix.length() - 2) + "/parties")
                }));

        this.changeFspNameCommand.execute(
            new ChangeFspNameCommand.Input(createFspOutput.fspId(), "requestor-fsp-updated-" + suffix));

        final var addCurrencyOutput = this.addFspCurrencyCommand.execute(
            new AddFspCurrencyCommand.Input(createFspOutput.fspId(), Currency.BYN));

        final var deactivateCurrencyOutput = this.deactivateFspCurrencyCommand.execute(
            new DeactivateFspCurrencyCommand.Input(createFspOutput.fspId(), Currency.BYN));

        final var activateCurrencyOutput = this.activateFspCurrencyCommand.execute(
            new ActivateFspCurrencyCommand.Input(createFspOutput.fspId(), Currency.BYN));

        final var addEndpointOutput = this.addEndpointCommand.execute(
            new AddEndpointCommand.Input(
                createFspOutput.fspId(),
                EndpointType.QUOTES,
                "http://localhost:47" + suffix.substring(suffix.length() - 2) + "/quotes"));

        final var changeEndpointOutput = this.changeFspEndpointCommand.execute(
            new ChangeFspEndpointCommand.Input(
                createFspOutput.fspId(),
                EndpointType.QUOTES,
                "http://localhost:47" + suffix.substring(suffix.length() - 2) + "/quotes/v2"));

        final var deactivateEndpointOutput = this.deactivateEndpointCommand.execute(
            new DeactivateEndpointCommand.Input(createFspOutput.fspId(), EndpointType.QUOTES));

        final var activateEndpointOutput = this.activateEndpointCommand.execute(
            new ActivateEndpointCommand.Input(createFspOutput.fspId(), EndpointType.QUOTES));

        final var createGroupOutput = this.createFspGroupCommand.execute(
            new CreateFspGroupCommand.Input("requestor-fsp-group-" + suffix));

        this.joinFspGroupCommand.execute(
            new JoinFspGroupCommand.Input(createGroupOutput.fspGroupId(), createFspOutput.fspId()));

        this.leaveFspGroupCommand.execute(
            new LeaveFspGroupCommand.Input(createGroupOutput.fspGroupId(), createFspOutput.fspId()));

        final var removeGroupOutput = this.removeFspGroupCommand.execute(
            new RemoveFspGroupCommand.Input(createGroupOutput.fspGroupId()));

        this.deactivateFspCommand.execute(new DeactivateFspCommand.Input(createFspOutput.fspId()));

        this.activateFspCommand.execute(new ActivateFspCommand.Input(createFspOutput.fspId()));

        this.terminateFspCommand.execute(new TerminateFspCommand.Input(createFspOutput.fspId()));

        assertNotNull(createFspOutput.fspId());
        assertNotNull(addCurrencyOutput.fspCurrencyId());
        assertEquals(addCurrencyOutput.fspCurrencyId(), deactivateCurrencyOutput.fspCurrencyId());
        assertTrue(activateCurrencyOutput.activated());

        assertNotNull(addEndpointOutput.fspEndpointId());
        assertEquals(addEndpointOutput.fspEndpointId(), changeEndpointOutput.fspEndpointId());
        assertEquals(addEndpointOutput.fspEndpointId(), deactivateEndpointOutput.fspEndpointId());
        assertEquals(addEndpointOutput.fspEndpointId(), activateEndpointOutput.fspEndpointId());

        assertEquals(createGroupOutput.fspGroupId(), removeGroupOutput.fspGroupId());

        assertThrows(
            FspIdNotFoundException.class,
            () -> this.activateFspCommand.execute(new ActivateFspCommand.Input(new FspId(createFspOutput.fspId().getId()))));
    }

}
