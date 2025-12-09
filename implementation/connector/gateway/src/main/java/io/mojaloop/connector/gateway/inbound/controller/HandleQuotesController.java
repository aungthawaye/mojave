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
import io.mojaloop.connector.gateway.inbound.command.quotes.HandlePostQuotesRequestCommand;
import io.mojaloop.connector.gateway.inbound.command.quotes.HandlePutQuotesErrorCommand;
import io.mojaloop.connector.gateway.inbound.command.quotes.HandlePutQuotesResponseCommand;
import io.mojaloop.connector.gateway.inbound.event.PostQuotesEvent;
import io.mojaloop.connector.gateway.inbound.event.PutQuotesErrorEvent;
import io.mojaloop.connector.gateway.inbound.event.PutQuotesEvent;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
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
public class HandleQuotesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandleQuotesController.class.getName());

    private final EventPublisher eventPublisher;

    public HandleQuotesController(EventPublisher eventPublisher) {

        assert eventPublisher != null;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/quotes")
    public ResponseEntity<?> postQuotes(@RequestHeader Map<String, String> headers,
                                        @RequestBody QuotesPostRequest request) {

        var payer = new Payer(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PostQuotesEvent(
            new HandlePostQuotesRequestCommand.Input(payer, request.getQuoteId(), request)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/quotes/{quoteId}")
    public ResponseEntity<?> putQuotes(@RequestHeader Map<String, String> headers,
                                       @PathVariable String quoteId,
                                       @RequestBody QuotesIDPutResponse response) {

        var payee = new Payee(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(
            new PutQuotesEvent(new HandlePutQuotesResponseCommand.Input(payee, quoteId, response)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/quotes/{quoteId}/error")
    public ResponseEntity<?> putQuotesError(@RequestHeader Map<String, String> headers,
                                            @PathVariable String quoteId,
                                            @RequestBody ErrorInformationObject errorInformation) {

        var payee = new Payee(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutQuotesErrorEvent(
            new HandlePutQuotesErrorCommand.Input(payee, quoteId, errorInformation)));

        return ResponseEntity.accepted().build();
    }

}
