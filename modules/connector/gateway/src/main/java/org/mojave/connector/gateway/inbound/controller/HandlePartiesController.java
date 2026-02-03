/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.connector.gateway.inbound.controller;

import org.mojave.component.misc.spring.event.EventPublisher;
import org.mojave.connector.gateway.inbound.command.parties.HandleGetPartiesRequestCommand;
import org.mojave.connector.gateway.inbound.command.parties.HandlePutPartiesErrorCommand;
import org.mojave.connector.gateway.inbound.command.parties.HandlePutPartiesResponseCommand;
import org.mojave.connector.gateway.inbound.event.GetPartiesEvent;
import org.mojave.connector.gateway.inbound.event.PutPartiesErrorEvent;
import org.mojave.connector.gateway.inbound.event.PutPartiesEvent;
import org.mojave.rail.fspiop.component.type.Payee;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.component.handy.FspiopHeaders;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.mojave.scheme.fspiop.core.PartiesTypeIDPutResponse;
import org.mojave.scheme.fspiop.core.PartyIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
public class HandlePartiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandlePartiesController.class.getName());

    private final EventPublisher eventPublisher;

    public HandlePartiesController(EventPublisher eventPublisher) {

        Objects.requireNonNull(eventPublisher);
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/parties/{partyIdType}/{partyId}")
    public ResponseEntity<?> getParties(@RequestHeader Map<String, String> headers,
                                        @PathVariable PartyIdType partyIdType,
                                        @PathVariable String partyId) {

        var payer = new Payer(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new GetPartiesEvent(
            new HandleGetPartiesRequestCommand.Input(payer, partyIdType, partyId, null)));

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/parties/{partyIdType}/{partyId}/{subId}")
    public ResponseEntity<?> getParties(@RequestHeader Map<String, String> headers,
                                        @PathVariable PartyIdType partyIdType,
                                        @PathVariable String partyId,
                                        @PathVariable String subId) {

        var payer = new Payer(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new GetPartiesEvent(
            new HandleGetPartiesRequestCommand.Input(payer, partyIdType, partyId, subId)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}")
    public ResponseEntity<?> putParties(@RequestHeader Map<String, String> headers,
                                        @PathVariable PartyIdType partyIdType,
                                        @PathVariable String partyId,
                                        @RequestBody PartiesTypeIDPutResponse response) {

        var payee = new Payee(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutPartiesEvent(
            new HandlePutPartiesResponseCommand.Input(
                payee, partyIdType, partyId, null,
                response)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/error")
    public ResponseEntity<?> putPartiesError(@RequestHeader Map<String, String> headers,
                                             @PathVariable PartyIdType partyIdType,
                                             @PathVariable String partyId,
                                             @RequestBody ErrorInformationObject errorInformation) {

        var payee = new Payee(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutPartiesErrorEvent(
            new HandlePutPartiesErrorCommand.Input(
                payee, partyIdType, partyId, null,
                errorInformation)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/{subId}/error")
    public ResponseEntity<?> putPartiesError(@RequestHeader Map<String, String> headers,
                                             @PathVariable PartyIdType partyIdType,
                                             @PathVariable String partyId,
                                             @PathVariable String subId,
                                             @RequestBody ErrorInformationObject errorInformation) {

        var payee = new Payee(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutPartiesErrorEvent(
            new HandlePutPartiesErrorCommand.Input(
                payee, partyIdType, partyId, subId,
                errorInformation)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/{subId}")
    public ResponseEntity<?> putPartiesWithSubId(@RequestHeader Map<String, String> headers,
                                                 @PathVariable PartyIdType partyIdType,
                                                 @PathVariable String partyId,
                                                 @PathVariable String subId,
                                                 @RequestBody PartiesTypeIDPutResponse response) {

        var payee = new Payee(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutPartiesEvent(
            new HandlePutPartiesResponseCommand.Input(
                payee, partyIdType, partyId, subId,
                response)));

        return ResponseEntity.accepted().build();
    }

}
