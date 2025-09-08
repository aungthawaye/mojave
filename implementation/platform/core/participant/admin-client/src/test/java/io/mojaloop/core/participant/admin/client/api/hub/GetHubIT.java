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
public class GetHubIT {

    @Autowired
    private CreateHub createHub;

    @Autowired
    private GetHub getHub;

    @Test
    public void test_successfully_get_hub() throws ParticipantCommandClientException {
        var created = this.createHub.execute(new CreateHubCommand.Input("Hub Get", new Currency[]{Currency.USD}));
        this.getHub.execute(created.hubId().getId());
    }
}
