package org.mojave.core.participant.domain.command.hub;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;
import org.mojave.core.participant.contract.data.HubData;
import org.mojave.core.participant.contract.exception.hub.HubCountLimitReachedException;
import org.mojave.core.participant.contract.query.HubQuery;
import org.mojave.core.participant.domain.BaseIT;
import org.mojave.core.participant.domain.ParticipantDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantDomainTestConfiguration.class})
@DisplayName("Create Hub Command Integration Test")
public class CreateHubCommandIT extends BaseIT {

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private HubQuery hubQuery;

    @Test
    @DisplayName("Create hub successfully")
    public void successful() {

        final var output = this.createHub(
            this.createHubCommand, "hub", Currency.USD,
            Currency.BYN);
        final var hub = this.hubQuery.get();
        final var currencies = Arrays
                                   .stream(hub.currencies())
                                   .map(HubData.HubCurrencyData::currency)
                                   .collect(Collectors.toSet());

        assertNotNull(output.hubId());
        assertEquals(output.hubId(), hub.hubId());
        assertEquals("hub", hub.name());
        assertEquals(2, hub.currencies().length);
        assertEquals(Set.of(Currency.USD, Currency.BYN), currencies);
        assertEquals(1, this.hubQuery.count());
        assertEquals(1, this.hubQuery.getAll().size());
    }

    @Test
    @DisplayName("Throw when hub count limit has been reached")
    public void hubCountLimitReached() {

        this.createHub(
            this.createHubCommand, "primary-hub", Currency.USD,
            Currency.BYN);

        assertThrows(
            HubCountLimitReachedException.class,
            () -> this.createHub(this.createHubCommand, "secondary-hub", Currency.USD));
    }

}
