package io.mojaloop.connector.service.inbound.controller;

import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HandlePartiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlePartiesController.class.getName());

    private final EventPublisher eventPublisher;

    public HandlePartiesController(EventPublisher eventPublisher) {

        assert eventPublisher != null;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/parties/{partyIdType}/{partyId}")
    public ResponseEntity<?> getParties(@RequestHeader Map<String, String> headers,
                                        @PathVariable String partyIdType,
                                        @PathVariable String partyId) {

        var sendBackTo = headers.get(FspiopHeaders.Names.FSPIOP_SOURCE);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/parties/{partyIdType}/{partyId}/{subId}")
    public ResponseEntity<?> getParties(@RequestHeader Map<String, String> headers,
                                        @PathVariable String partyIdType,
                                        @PathVariable String partyId,
                                        @PathVariable String subId) {

        var sendBackTo = headers.get(FspiopHeaders.Names.FSPIOP_SOURCE);

        return ResponseEntity.accepted().build();
    }

}
