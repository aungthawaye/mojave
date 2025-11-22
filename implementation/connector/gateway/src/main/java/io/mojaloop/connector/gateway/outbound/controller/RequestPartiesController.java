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
import io.mojaloop.connector.gateway.outbound.event.RequestPartiesEvent;
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

        LOGGER.info(
            "Received lookup request for partyId: {}, partyIdType: {}, destination: {}",
            request.partyId(), request.partyIdType(), request.destination());
        LOGGER.debug("Lookup request: {}", request);

        var input = new RequestPartiesCommand.Input(
            new Payee(request.destination()), request.partyIdType, request.partyId, request.subId);

        var output = this.requestPartiesCommand.execute(input);

        this.eventPublisher.publish(new RequestPartiesEvent(input));

        return ResponseEntity.ok(output.response());

    }

    public record Request(String destination,
                          PartyIdType partyIdType,
                          String partyId,
                          String subId) { }

}
