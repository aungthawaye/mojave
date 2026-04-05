package org.mojave.core.participant.domain.command.fsp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.command.fsp.CreateFspCommand;
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
@DisplayName("Create Fsp Command Integration Test")
public class CreateFspCommandIT extends BaseIT {

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private CreateFspCommand createFspCommand;

    @Test
    @Order(1)
    @DisplayName("Create fsp successfully")
    public void successful() {

        this.createHubCommand.execute(new CreateHubCommand.Input(
            "hub", new Currency[]{
            Currency.USD,
            Currency.BYN}));

        this.createFspCommand.execute(new CreateFspCommand.Input(
            new FspCode("wallet1"), "Wallet 1", new Currency[]{Currency.USD},
            new CreateFspCommand.Input.Endpoint[]{
                new CreateFspCommand.Input.Endpoint(
                    EndpointType.PARTIES, "http://localhost:3201/parties"),
                new CreateFspCommand.Input.Endpoint(
                    EndpointType.QUOTES,
                    "http://localhost:3201/quotes"),
                new CreateFspCommand.Input.Endpoint(
                    EndpointType.TRANSFERS,
                    "http://localhost:3201/transfers")}));
    }

}
