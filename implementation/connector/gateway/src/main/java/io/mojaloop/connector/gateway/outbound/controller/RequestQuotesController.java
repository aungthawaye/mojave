package io.mojaloop.connector.gateway.outbound.controller;

import io.mojaloop.connector.gateway.outbound.command.RequestQuotesCommand;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.Party;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransactionInitiator;
import io.mojaloop.fspiop.spec.core.TransactionInitiatorType;
import io.mojaloop.fspiop.spec.core.TransactionScenario;
import io.mojaloop.fspiop.spec.core.TransactionType;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RequestQuotesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestQuotesController.class.getName());

    private final RequestQuotesCommand requestQuotesCommand;

    public RequestQuotesController(RequestQuotesCommand requestQuotesCommand) {

        assert requestQuotesCommand != null;
        this.requestQuotesCommand = requestQuotesCommand;
    }

    @PostMapping("/quote")
    public ResponseEntity<?> quote(@RequestBody @Valid Request request) {

        LOGGER.info("Received quote request for destination: {}", request.destination());
        LOGGER.debug("Quote request: {}", request);

        try {
            var quotesPostRequest = new QuotesPostRequest()
                                        .quoteId(UUID.randomUUID().toString())
                                        .transactionId(UUID.randomUUID().toString())
                                        .payee(new Party().partyIdInfo(request.payee()))
                                        .payer(new Party().partyIdInfo(request.payer()))
                                        .amountType(request.amountType())
                                        .amount(request.amount())
                                        .transactionType(new TransactionType()
                                                             .scenario(TransactionScenario.TRANSFER)
                                                             .initiator(TransactionInitiator.PAYER)
                                                             .initiatorType(TransactionInitiatorType.CONSUMER)
                                                        );

            var output = this.requestQuotesCommand.execute(new RequestQuotesCommand.Input(new Destination(request.destination()),
                                                                                          quotesPostRequest));

            return ResponseEntity.ok(output.response());

        } catch (FspiopException e) {
            throw new RuntimeException(e);
        }
    }

    public record Request(String destination, AmountType amountType, Money amount, PartyIdInfo payer, PartyIdInfo payee) { }

}
