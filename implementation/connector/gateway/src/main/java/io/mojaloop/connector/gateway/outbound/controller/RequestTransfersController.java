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
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.connector.gateway.outbound.command.RequestTransfersCommand;
import io.mojaloop.connector.gateway.outbound.event.RequestTransfersEvent;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Extension;
import io.mojaloop.fspiop.spec.core.ExtensionList;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.TransfersPostRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@RestController
public class RequestTransfersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTransfersController.class.getName());

    private final ParticipantContext participantContext;

    private final ConnectorOutboundConfiguration.TransactionSettings transactionSettings;

    private final RequestTransfersCommand requestTransfersCommand;

    private final EventPublisher eventPublisher;

    public RequestTransfersController(ParticipantContext participantContext,
                                      ConnectorOutboundConfiguration.TransactionSettings transactionSettings,
                                      RequestTransfersCommand requestTransfersCommand,
                                      EventPublisher eventPublisher) {

        assert participantContext != null;
        assert transactionSettings != null;
        assert requestTransfersCommand != null;
        assert eventPublisher != null;

        this.participantContext = participantContext;
        this.transactionSettings = transactionSettings;
        this.requestTransfersCommand = requestTransfersCommand;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody @Valid Request request) {

        LOGGER.info("Received transfer request for destination: {}", request.destination());
        LOGGER.debug("Transfer request: {}", request);

        try {

            var transfersPostRequest = new TransfersPostRequest();
            var extensionList = new ExtensionList();
            // Payer related
            extensionList.addExtensionItem(new Extension("payerFspId", this.participantContext.fspCode()));
            extensionList.addExtensionItem(new Extension("payerPartyIdType", request.payer.getPartyIdType().toString()));
            extensionList.addExtensionItem(new Extension("payerPartyId", request.payer.getPartyIdentifier()));

            // Payee related
            extensionList.addExtensionItem(new Extension("payeeFspId", request.destination));
            extensionList.addExtensionItem(new Extension("payeePartyIdType", request.payee.getPartyIdType().toString()));
            extensionList.addExtensionItem(new Extension("payeePartyId", request.payee.getPartyIdentifier()));

            var expireAfterSeconds = new Date(Instant.now().plus(this.transactionSettings.expireAfterSeconds(), ChronoUnit.SECONDS).toEpochMilli());

            transfersPostRequest.transferId(request.transferId()).payerFsp(this.participantContext.fspCode()).payeeFsp(request.destination)
                                .amount(request.amount).ilpPacket(request.ilpPacket).condition(request.condition)
                                .expiration(FspiopDates.forRequestBody(expireAfterSeconds)).extensionList(extensionList);

            var input = new RequestTransfersCommand.Input(new Destination(request.destination()), transfersPostRequest);
            var output = this.requestTransfersCommand.execute(input);

            this.eventPublisher.publish(new RequestTransfersEvent(input));

            return ResponseEntity.ok(output.result());

        } catch (FspiopException e) {
            throw new RuntimeException(e);
        }
    }

    public record Request(String destination,
                          String transferId,
                          AmountType amountType,
                          Money amount,
                          PartyIdInfo payer,
                          PartyIdInfo payee,
                          String ilpPacket,
                          String condition) { }

}
