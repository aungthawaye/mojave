package io.mojaloop.core.participant.admin.client.api.fsp;

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateFspIT {

    @Autowired
    private CreateFsp createFsp;

    @Test
    public void test_successfully_create() throws ParticipantCommandClientException {

        this.createFsp.execute(
            new CreateFspCommand.Input(new FspCode("fsp2"), "FSP 1", new Currency[]{Currency.USD, Currency.EUR, Currency.GBP, Currency.MMK},
                                       new CreateFspCommand.Input.Endpoint[]{
                                           new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://localhost:7080"),
                                           new CreateFspCommand.Input.Endpoint(EndpointType.QUOTES, "http://localhost:7080"),
                                           new CreateFspCommand.Input.Endpoint(EndpointType.TRANSFERS, "http://localhost:7080")}));
    }

}
