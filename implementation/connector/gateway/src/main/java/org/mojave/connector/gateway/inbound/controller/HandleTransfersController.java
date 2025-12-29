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
import org.mojave.connector.gateway.inbound.command.transfers.HandlePatchTransfersCommand;
import org.mojave.connector.gateway.inbound.command.transfers.HandlePostTransfersRequestCommand;
import org.mojave.connector.gateway.inbound.command.transfers.HandlePutTransfersErrorCommand;
import org.mojave.connector.gateway.inbound.command.transfers.HandlePutTransfersResponseCommand;
import org.mojave.connector.gateway.inbound.event.PostTransfersEvent;
import org.mojave.connector.gateway.inbound.event.PutTransfersErrorEvent;
import org.mojave.connector.gateway.inbound.event.PutTransfersEvent;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.handy.FspiopHeaders;
import org.mojave.rail.fspiop.component.type.Payee;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.scheme.fspiop.core.ErrorInformationObject;
import org.mojave.scheme.fspiop.core.TransfersIDPatchResponse;
import org.mojave.scheme.fspiop.core.TransfersIDPutResponse;
import org.mojave.scheme.fspiop.core.TransfersPostRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HandleTransfersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandleTransfersController.class.getName());

    private final EventPublisher eventPublisher;

    private final HandlePatchTransfersCommand handlePatchTransfersCommand;

    public HandleTransfersController(EventPublisher eventPublisher,
                                     HandlePatchTransfersCommand handlePatchTransfersCommand) {

        assert eventPublisher != null;
        assert handlePatchTransfersCommand != null;

        this.eventPublisher = eventPublisher;
        this.handlePatchTransfersCommand = handlePatchTransfersCommand;
    }

    @PostMapping("/transfers")
    public ResponseEntity<?> postTransfers(@RequestHeader Map<String, String> headers,
                                           @RequestBody TransfersPostRequest request) {

        var payer = new Payer(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PostTransfersEvent(
            new HandlePostTransfersRequestCommand.Input(payer, request.getTransferId(), request)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/transfers/{transferId}")
    public ResponseEntity<?> putTransfers(@RequestHeader Map<String, String> headers,
                                          @PathVariable String transferId,
                                          @RequestBody TransfersIDPutResponse response) {

        var payee = new Payee(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutTransfersEvent(
            new HandlePutTransfersResponseCommand.Input(payee, transferId, response)));

        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/transfers/{transferId}")
    public ResponseEntity<?> putTransfers(@RequestHeader Map<String, String> headers,
                                          @PathVariable String transferId,
                                          @RequestBody TransfersIDPatchResponse response)
        throws FspiopException {

        var payer = new Payer(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.handlePatchTransfersCommand.execute(
            new HandlePatchTransfersCommand.Input(payer, transferId, response));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/transfers/{transferId}/error")
    public ResponseEntity<?> putTransfersError(@RequestHeader Map<String, String> headers,
                                               @PathVariable String transferId,
                                               @RequestBody
                                               ErrorInformationObject errorInformation) {

        var payee = new Payee(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutTransfersErrorEvent(
            new HandlePutTransfersErrorCommand.Input(payee, transferId, errorInformation)));

        return ResponseEntity.accepted().build();
    }

}
