package io.mojaloop.core.participant.admin.client.api.fsp;

import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.contract.command.fsp.AddEndpointCommand;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class AddEndpointIT {

    @Autowired
    private CreateFsp createFsp;

    @Autowired
    private AddEndpoint addEndpoint;

    @Test
    public void test_successfully_add_endpoint() throws ParticipantCommandClientException {
        var output = this.createFsp.execute(
            new CreateFspCommand.Input(new FspCode("fsp-add-endpoint"), "FSP Add Endpoint", new Currency[]{Currency.USD},
                                       new CreateFspCommand.Input.Endpoint[]{
                                           new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://localhost:7080")
                                       }));

        this.addEndpoint.execute(new AddEndpointCommand.Input(new FspId(output.fspId().getId()), EndpointType.QUOTES, "http://localhost:7080"));
    }
}
