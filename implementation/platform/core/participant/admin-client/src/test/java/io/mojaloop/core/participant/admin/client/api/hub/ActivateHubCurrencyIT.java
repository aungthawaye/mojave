package io.mojaloop.core.participant.admin.client.api.hub;

import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.participant.admin.client.TestConfiguration;
import io.mojaloop.core.participant.admin.client.exception.ParticipantCommandClientException;
import io.mojaloop.core.participant.contract.command.hub.ActivateHubCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.AddHubCurrencyCommand;
import io.mojaloop.core.participant.contract.command.hub.CreateHubCommand;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class ActivateHubCurrencyIT {

    @Autowired
    private CreateHub createHub;

    @Autowired
    private AddHubCurrency addHubCurrency;

    @Autowired
    private ActivateHubCurrency activateHubCurrency;

    @Test
    public void test_successfully_activate_currency() throws ParticipantCommandClientException {
        var created = this.createHub.execute(new CreateHubCommand.Input("Hub ActiveCurrency", new Currency[]{Currency.USD}));
        this.addHubCurrency.execute(new AddHubCurrencyCommand.Input(new HubId(created.hubId().getId()), Currency.EUR));
        this.activateHubCurrency.execute(new ActivateHubCurrencyCommand.Input(new HubId(created.hubId().getId()), Currency.EUR));
    }
}
