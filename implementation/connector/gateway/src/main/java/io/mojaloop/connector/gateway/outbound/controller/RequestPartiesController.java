/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.connector.gateway.outbound.controller;

import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.connector.gateway.outbound.command.RequestPartiesCommand;
import io.mojaloop.connector.gateway.outbound.data.Parties;
import io.mojaloop.connector.gateway.outbound.event.PartiesErrorEvent;
import io.mojaloop.connector.gateway.outbound.event.PartiesRequestEvent;
import io.mojaloop.connector.gateway.outbound.event.PartiesResponseEvent;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestPartiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RequestPartiesController.class.getName());

    private final RequestPartiesCommand requestPartiesCommand;

    private final EventPublisher eventPublisher;

    public RequestPartiesController(RequestPartiesCommand requestPartiesCommand,
                                    EventPublisher eventPublisher) {

        assert null != requestPartiesCommand;
        assert null != eventPublisher;

        this.requestPartiesCommand = requestPartiesCommand;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/lookup")
    public ResponseEntity<?> lookup(@RequestBody @Valid RequestPartiesController.Request request)
        throws FspiopException {

        final var payee = new Payee(request.destination());

        try {

            final var input = new RequestPartiesCommand.Input(
                payee, request.partyIdType, request.partyId, request.subId);

            final var partiesRequest = new Parties.Request(
                payee, request.partyIdType, request.partyId, request.subId);

            this.eventPublisher.publish(new PartiesRequestEvent(partiesRequest));

            final var output = this.requestPartiesCommand.execute(input);

            final var partiesResponse = new Parties.Response(
                payee, request.partyIdType, request.partyId, request.subId, output.response());

            this.eventPublisher.publish(new PartiesResponseEvent(partiesResponse));

            return ResponseEntity.ok(output.response());

        } catch (FspiopException e) {

            this.eventPublisher.publish(new PartiesErrorEvent(
                new Parties.Error(
                    payee, request.partyIdType, request.partyId, request.subId,
                    e.toErrorObject())));

            throw e;
        }

    }

    public record Request(String destination,
                          PartyIdType partyIdType,
                          String partyId,
                          String subId) { }

}
