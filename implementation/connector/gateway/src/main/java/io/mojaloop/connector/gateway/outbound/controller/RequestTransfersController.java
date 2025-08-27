package io.mojaloop.connector.gateway.outbound.controller;

import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.connector.gateway.outbound.command.RequestTransfersCommand;
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
import java.util.UUID;

@RestController
public class RequestTransfersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTransfersController.class.getName());

    private final ParticipantContext participantContext;

    private final ConnectorOutboundConfiguration.TransactionSettings transactionSettings;

    private final RequestTransfersCommand requestTransfersCommand;

    public RequestTransfersController(ParticipantContext participantContext,
                                      ConnectorOutboundConfiguration.TransactionSettings transactionSettings,
                                      RequestTransfersCommand requestTransfersCommand) {

        assert participantContext != null;
        assert transactionSettings != null;
        assert requestTransfersCommand != null;

        this.participantContext = participantContext;
        this.transactionSettings = transactionSettings;
        this.requestTransfersCommand = requestTransfersCommand;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody @Valid Request request) {

        LOGGER.info("Received transfer request for destination: {}", request.destination());
        LOGGER.debug("Transfer request: {}", request);

        try {
            var transferId = UUID.randomUUID().toString();

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

            var expireAfterSeconds = new Date(
                Instant.now().plus(this.transactionSettings.expireAfterSeconds(), ChronoUnit.SECONDS).toEpochMilli());

            transfersPostRequest
                .transferId(transferId)
                .payerFsp(this.participantContext.fspCode())
                .payeeFsp(request.destination)
                .amount(request.amount)
                .ilpPacket(request.ilpPacket)
                .condition(request.condition)
                .expiration(FspiopDates.forRequestBody(expireAfterSeconds))
                .extensionList(extensionList);

            var output = this.requestTransfersCommand.execute(
                new RequestTransfersCommand.Input(new Destination(request.destination()), transfersPostRequest));

            return ResponseEntity.ok(output.response());

        } catch (FspiopException e) {
            throw new RuntimeException(e);
        }
    }

    public record Request(String destination,
                          AmountType amountType,
                          Money amount,
                          PartyIdInfo payer,
                          PartyIdInfo payee,
                          String ilpPacket,
                          String condition) { }

}
