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

package io.mojaloop.core.quoting.service.controller.api;

import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.core.quoting.contract.command.GetQuotesCommand;
import io.mojaloop.core.quoting.contract.command.PostQuotesCommand;
import io.mojaloop.core.quoting.contract.command.PutQuotesCommand;
import io.mojaloop.core.quoting.contract.command.PutQuotesErrorCommand;
import io.mojaloop.core.quoting.service.controller.event.GetQuotesEvent;
import io.mojaloop.core.quoting.service.controller.event.PostQuotesEvent;
import io.mojaloop.core.quoting.service.controller.event.PutQuotesErrorEvent;
import io.mojaloop.core.quoting.service.controller.event.PutQuotesEvent;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class QuotesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuotesController.class);

    private final EventPublisher eventPublisher;

    public QuotesController(EventPublisher eventPublisher) {

        assert eventPublisher != null;

        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/quotes/{quoteId}")
    public ResponseEntity<?> getQuotes(@PathVariable UdfQuoteId quoteId, HttpServletRequest request)
        throws IOException {

        LOGGER.info("Received GET /quotes/{}", quoteId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new GetQuotesEvent(new GetQuotesCommand.Input(fspiopHttpRequest, quoteId));

        LOGGER.info("Publishing GetQuotesEvent : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published GetQuotesEvent : [{}]", event);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/quotes")
    public ResponseEntity<?> postQuotes(@RequestBody QuotesPostRequest requestBody,
                                        HttpServletRequest request) throws IOException {

        LOGGER.info("Received POST /quotes");

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PostQuotesEvent(
            new PostQuotesCommand.Input(fspiopHttpRequest, requestBody));

        LOGGER.info("Publishing PostQuotesEvent : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PostQuotesEvent : [{}]", event);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/quotes/{quoteId}")
    public ResponseEntity<?> putQuotes(@PathVariable UdfQuoteId quoteId,
                                       @RequestBody QuotesIDPutResponse response,
                                       HttpServletRequest request) throws IOException {

        LOGGER.info("Received PUT /quotes/{}", quoteId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PutQuotesEvent(
            new PutQuotesCommand.Input(fspiopHttpRequest, quoteId, response));

        LOGGER.info("Publishing PutQuotesEvent : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PutQuotesEvent : [{}]", event);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/quotes/{quoteId}/error")
    public ResponseEntity<?> putQuotesError(@PathVariable UdfQuoteId quoteId,
                                            @RequestBody ErrorInformationObject error,
                                            HttpServletRequest request) throws IOException {

        LOGGER.info("Received PUT /quotes/{}/error", quoteId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PutQuotesErrorEvent(
            new PutQuotesErrorCommand.Input(fspiopHttpRequest, quoteId, error));

        LOGGER.info("Publishing PutQuotesErrorEvent : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PutQuotesErrorEvent : [{}]", event);

        return ResponseEntity.accepted().build();
    }

}
