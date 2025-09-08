package io.mojaloop.core.participant.admin.client.api.oracle;

import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class GetAllOraclesIT {

    @Autowired
    private CreateOracle createOracle;

    @Autowired
    private GetAllOracles getAllOracles;

    @Test
    public void test_successfully_get_all_oracles() throws ParticipantCommandClientException {
        this.createOracle.execute(new CreateOracleCommand.Input(PartyIdType.ACCOUNT_ID, "Oracle List 1", "http://localhost:7090"));
        this.createOracle.execute(new CreateOracleCommand.Input(PartyIdType.EMAIL, "Oracle List 2", "http://localhost:7090"));
        this.getAllOracles.execute();
    }
}
