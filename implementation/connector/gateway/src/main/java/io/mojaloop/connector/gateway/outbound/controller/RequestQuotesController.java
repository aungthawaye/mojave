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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.connector.gateway.outbound.command.RequestQuotesCommand;
import io.mojaloop.connector.gateway.outbound.data.Quotes;
import io.mojaloop.connector.gateway.outbound.event.QuotesErrorEvent;
import io.mojaloop.connector.gateway.outbound.event.QuotesRequestEvent;
import io.mojaloop.connector.gateway.outbound.event.QuotesResponseEvent;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payee;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RequestQuotesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RequestQuotesController.class.getName());

    private final RequestQuotesCommand requestQuotesCommand;

    private final EventPublisher eventPublisher;

    public RequestQuotesController(RequestQuotesCommand requestQuotesCommand,
                                   EventPublisher eventPublisher) {

        assert requestQuotesCommand != null;
        assert eventPublisher != null;
        this.requestQuotesCommand = requestQuotesCommand;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/quote")
    public ResponseEntity<?> quote(@RequestBody @Valid Request request) throws FspiopException {

        final var id = UUID.randomUUID().toString();

        final var quotesPostRequest = new QuotesPostRequest()
                                          .quoteId(id)
                                          .transactionId(id)
                                          .payee(new Party().partyIdInfo(request.payee()))
                                          .payer(new Party().partyIdInfo(request.payer()))
                                          .amountType(request.amountType())
                                          .amount(request.amount())
                                          .fees(request.fees())
                                          .expiration(request.expiration())
                                          .transactionType(new TransactionType()
                                                               .scenario(
                                                                   TransactionScenario.TRANSFER)
                                                               .initiator(
                                                                   TransactionInitiator.PAYER)
                                                               .initiatorType(
                                                                   TransactionInitiatorType.CONSUMER));

        final var payee = new Payee(request.destination);

        try {

            // Publish request event
            final var quotesRequest = new Quotes.Request(payee, quotesPostRequest);
            this.eventPublisher.publish(new QuotesRequestEvent(quotesRequest));

            // Execute command
            final var input = new RequestQuotesCommand.Input(payee, quotesPostRequest);
            final var output = this.requestQuotesCommand.execute(input);

            // Publish response event
            final var quotesResponse = new Quotes.Response(payee, id, output.result().response());
            this.eventPublisher.publish(new QuotesResponseEvent(quotesResponse));

            return ResponseEntity.ok(output.result());

        } catch (FspiopException e) {

            // Publish error event and rethrow
            this.eventPublisher.publish(
                new QuotesErrorEvent(new Quotes.Error(payee, id, e.toErrorObject())));
            throw e;
        }

    }

    public record Request(@JsonProperty(required = true) @NotNull @NotBlank String destination,
                          @JsonProperty(required = true) @NotNull AmountType amountType,
                          @JsonProperty(required = true) @NotNull Money amount,
                          @JsonProperty(required = true) @NotNull PartyIdInfo payer,
                          @JsonProperty(required = true) @NotNull PartyIdInfo payee,
                          @JsonProperty() Money fees,
                          @JsonProperty() String expiration) { }

}
