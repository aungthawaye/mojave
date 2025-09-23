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
package io.mojaloop.connector.gateway.inbound.controller;

import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.connector.gateway.inbound.command.transfers.HandleTransfersErrorCommand;
import io.mojaloop.connector.gateway.inbound.command.transfers.HandleTransfersRequestCommand;
import io.mojaloop.connector.gateway.inbound.command.transfers.HandleTransfersResponseCommand;
import io.mojaloop.connector.gateway.inbound.event.PostTransfersEvent;
import io.mojaloop.connector.gateway.inbound.event.PutTransfersErrorEvent;
import io.mojaloop.connector.gateway.inbound.event.PutTransfersEvent;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.TransfersIDPutResponse;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HandleTransfersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleTransfersController.class.getName());

    private final EventPublisher eventPublisher;

    public HandleTransfersController(EventPublisher eventPublisher) {

        assert eventPublisher != null;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/transfers")
    public ResponseEntity<?> postTransfers(@RequestHeader Map<String, String> headers, @RequestBody TransfersPostRequest request) {

        LOGGER.debug("Received POST /transfers : request : {}", request);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PostTransfersEvent(new HandleTransfersRequestCommand.Input(source, request.getTransferId(), request)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/transfers/{transferId}")
    public ResponseEntity<?> putTransfers(@RequestHeader Map<String, String> headers,
                                          @PathVariable String transferId,
                                          @RequestBody TransfersIDPutResponse response) {

        LOGGER.debug("Received PUT /transfers/{} : response : {}", transferId, response);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutTransfersEvent(new HandleTransfersResponseCommand.Input(source, transferId, response)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/transfers/{transferId}/error")
    public ResponseEntity<?> putTransfersError(@RequestHeader Map<String, String> headers,
                                               @PathVariable String transferId,
                                               @RequestBody ErrorInformationObject errorInformation) {

        LOGGER.debug("Received PUT /transfers/{}/error : errorInformation : {}", transferId, errorInformation);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutTransfersErrorEvent(new HandleTransfersErrorCommand.Input(source, transferId, errorInformation)));

        return ResponseEntity.accepted().build();
    }

}
