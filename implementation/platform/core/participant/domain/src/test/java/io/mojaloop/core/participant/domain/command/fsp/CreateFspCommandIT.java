/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.domain.TestConfiguration;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import io.mojaloop.fspiop.spec.core.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public class CreateFspCommandIT {

    @Autowired
    private CreateFspCommand createFspCommand;

    @Autowired
    private FspRepository fspRepository;

    @Test
    public void createTwoFsps_success_persistsAndReturnsIds()
        throws FspEndpointAlreadyConfiguredException, FspCurrencyAlreadySupportedException, FspCodeAlreadyExistsException {
        // Arrange
        assertNotNull(createFspCommand);

        CreateFspCommand.Input input1 = new CreateFspCommand.Input(new FspCode("fsp1"), "FSP 1", new Currency[]{Currency.USD, Currency.MMK, Currency.MYR},
                                                                   new CreateFspCommand.Input.Endpoint[]{
                                                                       new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://localhost:8080"),
                                                                       new CreateFspCommand.Input.Endpoint(EndpointType.QUOTES, "http://localhost:8080"),
                                                                       new CreateFspCommand.Input.Endpoint(EndpointType.TRANSFERS, "http://localhost:8080")});

        // Act
        var output1 = this.createFspCommand.execute(input1);

        // Assert
        assertNotNull(output1);
        assertNotNull(output1.fspId());
        var saved1 = fspRepository.findById(output1.fspId());
        assertTrue(saved1.isPresent());
        assertEquals("FSP 1", saved1.get().convert().name());
        assertEquals(3, saved1.get().getCurrencies().size());
        assertEquals(3, saved1.get().getEndpoints().size());

        // Second FSP
        CreateFspCommand.Input input2 = new CreateFspCommand.Input(new FspCode("fsp2"), "FSP 2", new Currency[]{Currency.USD, Currency.MMK, Currency.MYR},
                                                                   new CreateFspCommand.Input.Endpoint[]{
                                                                       new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://localhost:9080"),
                                                                       new CreateFspCommand.Input.Endpoint(EndpointType.QUOTES, "http://localhost:9080"),
                                                                       new CreateFspCommand.Input.Endpoint(EndpointType.TRANSFERS, "http://localhost:9080")});

        var output2 = this.createFspCommand.execute(input2);
        assertNotNull(output2.fspId());
        var saved2 = fspRepository.findById(output2.fspId());
        assertTrue(saved2.isPresent());
        assertEquals("FSP 2", saved2.get().convert().name());
    }

    @Test
    public void duplicateCurrencyInInput_throwsCurrencyAlreadySupportedException() {
        // Arrange: same currency twice
        CreateFspCommand.Input bad = new CreateFspCommand.Input(new FspCode("dupCurrency"), "Has dup currency", new Currency[]{Currency.USD, Currency.USD},
                                                                new CreateFspCommand.Input.Endpoint[]{
                                                                    new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://h1")});

        // Assert
        assertThrows(FspCurrencyAlreadySupportedException.class, () -> this.createFspCommand.execute(bad));
    }

    @Test
    public void duplicateEndpointInInput_throwsEndpointAlreadyConfiguredException() {
        // Arrange: same endpoint type twice
        CreateFspCommand.Input bad = new CreateFspCommand.Input(new FspCode("dupEndpoint"), "Has dup endpoint", new Currency[]{Currency.USD},
                                                                new CreateFspCommand.Input.Endpoint[]{
                                                                    new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://h1"),
                                                                    new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://h2")});

        // Assert
        assertThrows(FspEndpointAlreadyConfiguredException.class, () -> this.createFspCommand.execute(bad));
    }

    @Test
    public void duplicateFspCode_throwsFspCodeAlreadyExistsException()
        throws FspEndpointAlreadyConfiguredException, FspCurrencyAlreadySupportedException, FspCodeAlreadyExistsException {
        // Arrange
        CreateFspCommand.Input first = new CreateFspCommand.Input(new FspCode("dupFsp"), "First", new Currency[]{Currency.USD},
                                                                  new CreateFspCommand.Input.Endpoint[]{
                                                                      new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://host")});
        this.createFspCommand.execute(first);

        CreateFspCommand.Input duplicate = new CreateFspCommand.Input(new FspCode("dupFsp"), "Second", new Currency[]{Currency.USD},
                                                                      new CreateFspCommand.Input.Endpoint[]{
                                                                          new CreateFspCommand.Input.Endpoint(EndpointType.PARTIES, "http://host2")});

        // Assert
        assertThrows(FspCodeAlreadyExistsException.class, () -> this.createFspCommand.execute(duplicate));
    }

}
