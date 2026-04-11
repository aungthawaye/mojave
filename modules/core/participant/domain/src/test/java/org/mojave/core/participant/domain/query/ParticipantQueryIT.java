package org.mojave.core.participant.domain.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.command.fsp.CreateFspCommand;
import org.mojave.core.participant.contract.command.hub.CreateHubCommand;
import org.mojave.core.participant.contract.exception.fsp.FspCodeNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.contract.exception.hub.HubNotFoundException;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.contract.query.HubQuery;
import org.mojave.core.participant.domain.BaseIT;
import org.mojave.core.participant.domain.ParticipantDomainTestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        ParticipantDomainTestConfiguration.class})
@DisplayName("Participant Queries Integration Test")
public class ParticipantQueryIT extends BaseIT {

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private FspQuery fspQuery;

    @Autowired
    private HubQuery hubQuery;

    @Test
    @DisplayName("Throw when getting hub before setup")
    public void hubGetNotFound() {

        assertThrows(HubNotFoundException.class, () -> this.hubQuery.get());
    }

    @Test
    @DisplayName("Throw when getting fsp by id and code before setup")
    public void fspGetNotFound() {

        assertThrows(
            FspIdNotFoundException.class,
            () -> this.fspQuery.get(new FspId(Long.MAX_VALUE)));
        assertThrows(
            FspCodeNotFoundException.class,
            () -> this.fspQuery.get(new FspCode("UNKNOWN_FSP")));
    }

    @Test
    @DisplayName("Get hub and fsp data after setup")
    public void querySuccessful() {

        this.createHub(
            this.createHubCommand, "hub", Currency.USD,
            Currency.BYN);

        final var output = this.createFsp(
            this.createFspCommand, "wallet1", "Wallet 1",
            new Currency[]{Currency.USD},
            new CreateFspCommand.Input.Endpoint[]{
                this.endpoint(EndpointType.PARTIES, "http://localhost:3201/parties")
            });

        final var hub = this.hubQuery.get();
        final var fspById = this.fspQuery.get(output.fspId());
        final var fspByCode = this.fspQuery.get(new FspCode("wallet1"));

        assertEquals(1, this.hubQuery.count());
        assertEquals("hub", hub.name());
        assertEquals(output.fspId(), fspById.fspId());
        assertEquals(output.fspId(), fspByCode.fspId());
        assertEquals(1, this.fspQuery.getAll().size());
    }

}
