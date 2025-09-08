package io.mojaloop.core.participant.admin.client.api.hub;

import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateHubIT {

    @Autowired
    private CreateHub createHub;

    @Test
    public void test_successfully_create_hub() throws ParticipantCommandClientException {
        this.createHub.execute(new CreateHubCommand.Input("HUB One", new io.mojaloop.fspiop.spec.core.Currency[]{io.mojaloop.fspiop.spec.core.Currency.USD}));
    }
}
