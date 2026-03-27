package org.mojave.core.participant.domain.command.hub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;
import org.mojave.core.participant.domain.BaseIT;
import org.mojave.core.participant.domain.ParticipantDomainConfiguration;
import org.mojave.core.participant.domain.ParticipantDomainSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantDomainConfiguration.class,
        ParticipantDomainSettings.class})
@DisplayName("Create Hub Command Integration Test")
public class CreateHubCommandIT extends BaseIT {

    @Autowired
    private CreateHubCommand createHubCommand;

    @Test
    @Order(1)
    @DisplayName("Create hub successfully")
    public void successful() {

        this.createHubCommand.execute(new CreateHubCommand.Input(
            "hub", new Currency[]{
            Currency.USD,
            Currency.BYN}));
    }

}
