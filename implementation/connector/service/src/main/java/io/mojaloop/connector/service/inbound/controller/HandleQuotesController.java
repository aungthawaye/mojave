package io.mojaloop.connector.service.inbound.controller;

import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.connector.service.inbound.command.quotes.HandleQuotesErrorCommand;
import io.mojaloop.connector.service.inbound.command.quotes.HandleQuotesRequestCommand;
import io.mojaloop.connector.service.inbound.command.quotes.HandleQuotesResponseCommand;
import io.mojaloop.connector.service.inbound.event.PostQuotesEvent;
import io.mojaloop.connector.service.inbound.event.PutQuotesErrorEvent;
import io.mojaloop.connector.service.inbound.event.PutQuotesEvent;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.QuotesIDPutResponse;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
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
public class HandleQuotesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleQuotesController.class.getName());

    private final EventPublisher eventPublisher;

    public HandleQuotesController(EventPublisher eventPublisher) {

        assert eventPublisher != null;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping("/quotes")
    public ResponseEntity<?> postQuotes(@RequestHeader Map<String, String> headers, @Valid @RequestBody QuotesPostRequest request) {

        LOGGER.debug("Received POST /quotes");
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PostQuotesEvent(new HandleQuotesRequestCommand.Input(source, request.getQuoteId(), request)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/quotes/{quoteId}")
    public ResponseEntity<?> putQuotes(@RequestHeader Map<String, String> headers,
                                       @PathVariable String quoteId,
                                       @Valid @RequestBody QuotesIDPutResponse response) {

        LOGGER.debug("Received PUT /quotes/{}", quoteId);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutQuotesEvent(new HandleQuotesResponseCommand.Input(source, quoteId, response)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/quotes/{quoteId}/error")
    public ResponseEntity<?> putQuotesError(@RequestHeader Map<String, String> headers,
                                            @PathVariable String quoteId,
                                            @Valid @RequestBody ErrorInformationObject errorInformation) {

        LOGGER.debug("Received PUT /quotes/{}/error", quoteId);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new PutQuotesErrorEvent(new HandleQuotesErrorCommand.Input(source, quoteId, errorInformation)));

        return ResponseEntity.accepted().build();
    }

}
