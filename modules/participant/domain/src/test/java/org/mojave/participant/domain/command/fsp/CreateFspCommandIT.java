package org.mojave.participant.domain.command.fsp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.participant.contract.command.fsp.CreateFspCommand;
import org.mojave.participant.contract.command.hub.CreateHubCommand;
import org.mojave.participant.contract.data.FspData;
import org.mojave.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import org.mojave.participant.contract.exception.hub.HubNotFoundException;
import org.mojave.participant.contract.query.FspQuery;
import org.mojave.participant.domain.BaseIT;
import org.mojave.participant.domain.ParticipantDomainTestConfiguration;
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
@DisplayName("Create Fsp Command Integration Test")
public class CreateFspCommandIT extends BaseIT {

    @Autowired
    private CreateHubCommand createHubCommand;

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private FspQuery fspQuery;

    @Test
    @DisplayName("Create fsp successfully")
    public void successful() {

        this.createHub(
            this.createHubCommand, "hub", Currency.USD,
            Currency.BYN);

        final var output = this.createFsp(
            this.createFspCommand, "wallet1", "Wallet 1",
            new Currency[]{Currency.USD, Currency.BYN},
            new CreateFspCommand.Input.Endpoint[]{
                this.endpoint(EndpointType.PARTIES, "http://localhost:3201/parties"),
                this.endpoint(EndpointType.QUOTES, "http://localhost:3201/quotes"),
                this.endpoint(EndpointType.TRANSFERS, "http://localhost:3201/transfers")
            });

        final var byId = this.fspQuery.get(new FspId(output.fspId().getId()));
        final var byCode = this.fspQuery.get(new FspCode("wallet1"));
        final var all = this.fspQuery.getAll();

        assertNotNull(output.fspId());
        this.assertFspData(byId);
        this.assertFspData(byCode);
        assertEquals(1, all.size());
        this.assertFspData(all.getFirst());
    }

    @Test
    @DisplayName("Throw when hub has not been configured")
    public void hubNotFound() {

        assertThrows(
            HubNotFoundException.class,
            () -> this.createFsp(
                this.createFspCommand, "wallet1", "Wallet 1",
                new Currency[]{Currency.USD},
                new CreateFspCommand.Input.Endpoint[]{
                    this.endpoint(EndpointType.PARTIES, "http://localhost:3201/parties")
                }));
    }

    @Test
    @DisplayName("Throw when fsp code already exists")
    public void fspCodeAlreadyExists() {

        this.createHub(
            this.createHubCommand, "hub", Currency.USD,
            Currency.BYN);

        this.createFsp(
            this.createFspCommand, "wallet1", "Wallet 1",
            new Currency[]{Currency.USD},
            new CreateFspCommand.Input.Endpoint[]{
                this.endpoint(EndpointType.PARTIES, "http://localhost:3201/parties")
            });

        assertThrows(
            FspCodeAlreadyExistsException.class,
            () -> this.createFsp(
                this.createFspCommand, "wallet1", "Wallet 1 Duplicate",
                new Currency[]{Currency.USD},
                new CreateFspCommand.Input.Endpoint[]{
                    this.endpoint(EndpointType.PARTIES, "http://localhost:3202/parties")
                }));
    }

    private void assertFspData(final FspData fspData) {

        final var currencies = Arrays
                                   .stream(fspData.currencies())
                                   .map(currency -> currency.currency())
                                   .collect(Collectors.toSet());

        assertEquals("wallet1", fspData.code().value());
        assertEquals("Wallet 1", fspData.name());
        assertEquals(Set.of(Currency.USD, Currency.BYN), currencies);
        assertEquals(3, fspData.endpoints().size());
        assertNotNull(fspData.endpoints().get(EndpointType.PARTIES));
        assertNotNull(fspData.endpoints().get(EndpointType.QUOTES));
        assertNotNull(fspData.endpoints().get(EndpointType.TRANSFERS));
    }

}
