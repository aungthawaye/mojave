package io.mojaloop.core.participant.admin.client.api.hub;

import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.contract.command.hub.ChangeHubNameCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class ChangeHubNameIT {

    @Autowired
    private CreateHub createHub;

    @Autowired
    private ChangeHubName changeHubName;

    @Test
    public void test_successfully_change_hub_name() throws ParticipantCommandClientException {
        var created = this.createHub.execute(new CreateHubCommand.Input("Hub ChangeName", new Currency[]{Currency.USD}));
        this.changeHubName.execute(new ChangeHubNameCommand.Input(new HubId(created.hubId().getId()), "Hub Updated"));
    }
}
