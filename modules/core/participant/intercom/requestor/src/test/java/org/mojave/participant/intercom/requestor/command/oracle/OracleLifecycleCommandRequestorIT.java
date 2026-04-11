package org.mojave.participant.intercom.requestor.command.oracle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.participant.PartyIdType;
import org.mojave.participant.contract.command.oracle.ActivateOracleCommand;
import org.mojave.participant.contract.command.oracle.ChangeOracleNameCommand;
import org.mojave.participant.contract.command.oracle.ChangeOracleTypeCommand;
import org.mojave.participant.contract.command.oracle.CreateOracleCommand;
import org.mojave.participant.contract.command.oracle.DeactivateOracleCommand;
import org.mojave.participant.contract.command.oracle.TerminateOracleCommand;
import org.mojave.participant.contract.exception.oracle.OracleIdNotFoundException;
import org.mojave.participant.intercom.requestor.ParticipantIntercomRequestorTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantIntercomRequestorTestConfiguration.class})
@DisplayName("Oracle Commands Requestor Integration Test")
public class OracleLifecycleCommandRequestorIT {

    @Autowired
    private ActivateOracleCommand activateOracleCommand;

    @Autowired
    private ChangeOracleNameCommand changeOracleNameCommand;

    @Autowired
    private ChangeOracleTypeCommand changeOracleTypeCommand;

    @Autowired
    private CreateOracleCommand createOracleCommand;

    @Autowired
    private DeactivateOracleCommand deactivateOracleCommand;

    @Autowired
    private TerminateOracleCommand terminateOracleCommand;

    @Test
    @DisplayName("Execute oracle command lifecycle through requestor")
    public void lifecycleSuccessful() {

        final var suffix = Long.toString(System.currentTimeMillis());

        final var createOracleOutput = this.createOracleCommand.execute(
            new CreateOracleCommand.Input(
                PartyIdType.MSISDN,
                "requestor-oracle-" + suffix,
                "http://localhost:45" + suffix.substring(suffix.length() - 2) + "/oracle"));

        this.changeOracleNameCommand.execute(
            new ChangeOracleNameCommand.Input(
                createOracleOutput.oracleId(),
                "requestor-oracle-updated-" + suffix));

        this.changeOracleTypeCommand.execute(
            new ChangeOracleTypeCommand.Input(createOracleOutput.oracleId(), PartyIdType.EMAIL));

        this.deactivateOracleCommand.execute(
            new DeactivateOracleCommand.Input(createOracleOutput.oracleId()));

        this.activateOracleCommand.execute(
            new ActivateOracleCommand.Input(createOracleOutput.oracleId()));

        this.terminateOracleCommand.execute(
            new TerminateOracleCommand.Input(createOracleOutput.oracleId()));

        assertNotNull(createOracleOutput.oracleId());

        assertThrows(
            OracleIdNotFoundException.class,
            () -> this.activateOracleCommand.execute(
                new ActivateOracleCommand.Input(createOracleOutput.oracleId())));
    }

}

