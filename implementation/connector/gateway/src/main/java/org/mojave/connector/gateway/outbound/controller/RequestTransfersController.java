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

package org.mojave.connector.gateway.outbound.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.mojave.component.misc.spring.event.EventPublisher;
import org.mojave.connector.gateway.outbound.ConnectorOutboundConfiguration;
import org.mojave.connector.gateway.outbound.command.RequestTransfersCommand;
import org.mojave.connector.gateway.outbound.data.Transfers;
import org.mojave.connector.gateway.outbound.event.TransfersErrorEvent;
import org.mojave.connector.gateway.outbound.event.TransfersRequestEvent;
import org.mojave.connector.gateway.outbound.event.TransfersResponseEvent;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.handy.FspiopDates;
import org.mojave.fspiop.component.participant.ParticipantContext;
import org.mojave.fspiop.component.type.Payee;
import org.mojave.fspiop.spec.core.AmountType;
import org.mojave.fspiop.spec.core.Extension;
import org.mojave.fspiop.spec.core.ExtensionList;
import org.mojave.fspiop.spec.core.Money;
import org.mojave.fspiop.spec.core.PartyIdInfo;
import org.mojave.fspiop.spec.core.TransfersPostRequest;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RequestTransfersController.class.getName());

    private final ParticipantContext participantContext;

    private final ConnectorOutboundConfiguration.TransferSettings transferSettings;

    private final RequestTransfersCommand requestTransfersCommand;

    private final EventPublisher eventPublisher;

    public RequestTransfersController(ParticipantContext participantContext,
                                      ConnectorOutboundConfiguration.TransferSettings transferSettings,
                                      RequestTransfersCommand requestTransfersCommand,
                                      EventPublisher eventPublisher) {

        assert participantContext != null;
        assert transferSettings != null;
        assert requestTransfersCommand != null;
        assert eventPublisher != null;

        this.participantContext = participantContext;
        this.transferSettings = transferSettings;
        this.requestTransfersCommand = requestTransfersCommand;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody @Valid Request request) throws FspiopException {

        final var transfersPostRequest = new TransfersPostRequest();
        final var extensionList = new ExtensionList();

        // Payer related
        extensionList.addExtensionItem(
            new Extension("payerFspId", this.participantContext.fspCode()));
        extensionList.addExtensionItem(
            new Extension("payerPartyIdType", request.payer.getPartyIdType().toString()));
        extensionList.addExtensionItem(
            new Extension("payerPartyId", request.payer.getPartyIdentifier()));

        // Payee related
        extensionList.addExtensionItem(new Extension("payeeFspId", request.payeeFsp()));
        extensionList.addExtensionItem(
            new Extension("payeePartyIdType", request.payee.getPartyIdType().toString()));
        extensionList.addExtensionItem(
            new Extension("payeePartyId", request.payee.getPartyIdentifier()));

        final var expireAfterSeconds = new Date(Instant
                                                    .now()
                                                    .plus(
                                                        this.transferSettings.transferRequestExpirySeconds(),
                                                        ChronoUnit.SECONDS)
                                                    .toEpochMilli());

        transfersPostRequest
            .transferId(request.transferId())
            .payerFsp(this.participantContext.fspCode())
            .payeeFsp(request.payeeFsp())
            .amount(request.amount)
            .ilpPacket(request.ilpPacket)
            .condition(request.condition)
            .expiration(FspiopDates.forRequestBody(expireAfterSeconds))
            .extensionList(extensionList);

        final var payee = new Payee(request.payeeFsp());

        try {
            // Publish request event
            final var transfersRequest = new Transfers.Request(payee, transfersPostRequest);
            this.eventPublisher.publish(new TransfersRequestEvent(transfersRequest));

            // Execute command
            final var input = new RequestTransfersCommand.Input(payee, transfersPostRequest);
            final var output = this.requestTransfersCommand.execute(input);

            // Publish response event
            final var transfersResponse = new Transfers.Response(
                payee, request.transferId(), output.result().response());
            this.eventPublisher.publish(new TransfersResponseEvent(transfersResponse));

            return ResponseEntity.ok(output.result());

        } catch (FspiopException e) {
            // Publish error event and rethrow
            this.eventPublisher.publish(new TransfersErrorEvent(
                new Transfers.Error(payee, request.transferId(), e.toErrorObject())));
            throw e;
        }

    }

    public record Request(@JsonProperty(required = true) @NotNull @NotBlank String payeeFsp,
                          @JsonProperty(required = true) @NotNull @NotBlank String transferId,
                          @JsonProperty(required = true) @NotNull AmountType amountType,
                          @JsonProperty(required = true) @NotNull Money amount,
                          @JsonProperty(required = true) @NotNull PartyIdInfo payer,
                          @JsonProperty(required = true) @NotNull PartyIdInfo payee,
                          @JsonProperty(required = true) @NotNull String ilpPacket,
                          @JsonProperty(required = true) @NotNull String condition) { }

}
