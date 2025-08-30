package io.mojaloop.connector.gateway.inbound.controller;

import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.connector.gateway.inbound.command.parties.HandlePartiesErrorCommand;
import io.mojaloop.connector.gateway.inbound.command.parties.HandlePartiesRequestCommand;
import io.mojaloop.connector.gateway.inbound.command.parties.HandlePartiesResponseCommand;
import io.mojaloop.connector.gateway.inbound.event.GetPartiesEvent;
import io.mojaloop.connector.gateway.inbound.event.PutPartiesErrorEvent;
import io.mojaloop.connector.gateway.inbound.event.PutPartiesEvent;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
                                        @PathVariable PartyIdType partyIdType,
                                        @PathVariable String partyId) {

        LOGGER.debug("Received GET /parties/{}/{}", partyIdType, partyId);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new GetPartiesEvent(new HandlePartiesRequestCommand.Input(source, partyIdType, partyId, null)));

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/parties/{partyIdType}/{partyId}/{subId}")
    public ResponseEntity<?> getParties(@RequestHeader Map<String, String> headers,
                                        @PathVariable PartyIdType partyIdType,
                                        @PathVariable String partyId,
                                        @PathVariable String subId) {

        LOGGER.debug("Received GET /parties/{}/{}/{}", partyIdType, partyId, subId);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(new GetPartiesEvent(new HandlePartiesRequestCommand.Input(source, partyIdType, partyId, subId)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}")
    public ResponseEntity<?> putParties(@RequestHeader Map<String, String> headers,
                                        @PathVariable PartyIdType partyIdType,
                                        @PathVariable String partyId,
                                        @Valid @RequestBody PartiesTypeIDPutResponse response) {

        LOGGER.debug("Received PUT /parties/{}/{}", partyIdType, partyId);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(
            new PutPartiesEvent(new HandlePartiesResponseCommand.Input(source, partyIdType, partyId, null, response)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/error")
    public ResponseEntity<?> putPartiesError(@RequestHeader Map<String, String> headers,
                                             @PathVariable PartyIdType partyIdType,
                                             @PathVariable String partyId,
                                             @Valid @RequestBody ErrorInformationObject errorInformation) {

        LOGGER.debug("Received PUT /parties/{}/{}/error", partyIdType, partyId);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(
            new PutPartiesErrorEvent(new HandlePartiesErrorCommand.Input(source, partyIdType, partyId, null, errorInformation)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/{subId}/error")
    public ResponseEntity<?> putPartiesError(@RequestHeader Map<String, String> headers,
                                             @PathVariable PartyIdType partyIdType,
                                             @PathVariable String partyId,
                                             @PathVariable String subId,
                                             @Valid @RequestBody ErrorInformationObject errorInformation) {

        LOGGER.debug("Received PUT /parties/{}/{}/{}/error", partyIdType, partyId, subId);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(
            new PutPartiesErrorEvent(new HandlePartiesErrorCommand.Input(source, partyIdType, partyId, subId, errorInformation)));

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/{subId}")
    public ResponseEntity<?> putPartiesWithSubId(@RequestHeader Map<String, String> headers,
                                                 @PathVariable PartyIdType partyIdType,
                                                 @PathVariable String partyId,
                                                 @PathVariable String subId,
                                                 @Valid @RequestBody PartiesTypeIDPutResponse response) {

        LOGGER.debug("Received (withSubId) PUT /parties/{}/{}/{}", partyIdType, partyId, subId);
        var source = new Source(headers.get(FspiopHeaders.Names.FSPIOP_SOURCE));

        this.eventPublisher.publish(
            new PutPartiesEvent(new HandlePartiesResponseCommand.Input(source, partyIdType, partyId, subId, response)));

        return ResponseEntity.accepted().build();
    }

}
