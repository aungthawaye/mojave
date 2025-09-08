package io.mojaloop.core.participant.admin.client.api.hub;

import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class GetAllHubsIT {

    @Autowired
    private CreateHub createHub;

    @Autowired
    private GetAllHubs getAllHubs;

    @Test
    public void test_successfully_get_all_hubs() throws ParticipantCommandClientException {
        this.createHub.execute(new CreateHubCommand.Input("Hub List 1", new Currency[]{Currency.USD}));
        this.createHub.execute(new CreateHubCommand.Input("Hub List 2", new Currency[]{Currency.USD}));
        this.getAllHubs.execute();
    }
}
