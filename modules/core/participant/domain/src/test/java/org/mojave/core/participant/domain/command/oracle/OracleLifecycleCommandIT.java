package org.mojave.core.participant.domain.command.oracle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.scheme.rule.enums.participant.PartyIdType;
import org.mojave.core.participant.contract.command.oracle.ActivateOracleCommand;
import org.mojave.core.participant.contract.command.oracle.ChangeOracleNameCommand;
import org.mojave.core.participant.contract.command.oracle.ChangeOracleTypeCommand;
import org.mojave.core.participant.contract.command.oracle.CreateOracleCommand;
import org.mojave.core.participant.contract.command.oracle.DeactivateOracleCommand;
import org.mojave.core.participant.contract.command.oracle.TerminateOracleCommand;
import org.mojave.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import org.mojave.core.participant.contract.exception.oracle.OracleTypeNotFoundException;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.domain.BaseIT;
import org.mojave.core.participant.domain.ParticipantDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantDomainTestConfiguration.class})
@DisplayName("Oracle Commands Integration Test")
public class OracleLifecycleCommandIT extends BaseIT {

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
    private OracleQuery oracleQuery;

    @Autowired
    private TerminateOracleCommand terminateOracleCommand;

    @Test
    @DisplayName("Execute oracle command lifecycle successfully")
    public void lifecycleSuccessful() {

        final var createOracleOutput = this.createOracleCommand.execute(new CreateOracleCommand.Input(
            PartyIdType.MSISDN,
            "Oracle 1",
            "http://localhost:3501/oracle"));

        final var oracleId = createOracleOutput.oracleId();

        this.changeOracleNameCommand.execute(new ChangeOracleNameCommand.Input(
            oracleId,
            "Oracle 1 Updated"));

        this.changeOracleTypeCommand.execute(new ChangeOracleTypeCommand.Input(
            oracleId,
            PartyIdType.EMAIL));

        this.deactivateOracleCommand.execute(new DeactivateOracleCommand.Input(oracleId));

        assertThrows(OracleIdNotFoundException.class, () -> this.oracleQuery.get(oracleId));

        this.activateOracleCommand.execute(new ActivateOracleCommand.Input(oracleId));

        final var activatedOracle = this.oracleQuery.get(oracleId);

        this.terminateOracleCommand.execute(new TerminateOracleCommand.Input(oracleId));

        assertThrows(OracleIdNotFoundException.class, () -> this.oracleQuery.get(oracleId));
        assertThrows(
            OracleTypeNotFoundException.class,
            () -> this.oracleQuery.get(PartyIdType.MSISDN));

        assertEquals(oracleId, activatedOracle.oracleId());
        assertEquals(PartyIdType.EMAIL, activatedOracle.type());
        assertEquals("Oracle 1 Updated", activatedOracle.name());

        final var findByType = this.oracleQuery.find(PartyIdType.EMAIL);

        assertTrue(findByType.isEmpty());
    }

    @Test
    @DisplayName("Throw when activating terminated oracle")
    public void activateTerminatedOracle() {

        final var createOracleOutput = this.createOracleCommand.execute(new CreateOracleCommand.Input(
            PartyIdType.ACCOUNT_ID,
            "Oracle 2",
            "http://localhost:3502/oracle"));

        final var oracleId = createOracleOutput.oracleId();

        this.terminateOracleCommand.execute(new TerminateOracleCommand.Input(oracleId));

        assertThrows(
            OracleIdNotFoundException.class,
            () -> this.activateOracleCommand.execute(new ActivateOracleCommand.Input(oracleId)));
    }

}
