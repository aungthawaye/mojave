package io.mojaloop.core.participant.admin.client.api.oracle;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.contract.command.oracle.ActivateOracleCommand;
import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class ActivateOracleIT {

    @Autowired
    private CreateOracle createOracle;

    @Autowired
    private ActivateOracle activateOracle;

    @Test
    public void test_successfully_activate_oracle() throws ParticipantCommandClientException {
        var created = this.createOracle.execute(new CreateOracleCommand.Input(PartyIdType.EMAIL, "Oracle Activate", "http://localhost:7090"));
        this.activateOracle.execute(new ActivateOracleCommand.Input(new OracleId(created.oracleId().getId())));
    }
}
