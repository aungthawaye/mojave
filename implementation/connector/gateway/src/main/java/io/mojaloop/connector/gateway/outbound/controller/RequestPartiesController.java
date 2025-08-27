package io.mojaloop.connector.gateway.outbound.controller;

import io.mojaloop.connector.gateway.outbound.command.RequestPartiesCommand;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestPartiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPartiesController.class.getName());

    private final RequestPartiesCommand requestPartiesCommand;

    public RequestPartiesController(RequestPartiesCommand requestPartiesCommand) {

        assert null != requestPartiesCommand;

        this.requestPartiesCommand = requestPartiesCommand;
    }

    @PostMapping("/lookup")
    public ResponseEntity<?> lookup(@RequestBody @Valid RequestPartiesController.Request request) {

        LOGGER.info("Received lookup request for partyId: {}, partyIdType: {}, destination: {}",
                    request.partyId(),
                    request.partyIdType(),
                    request.destination());
        LOGGER.debug("Lookup request: {}", request);

        try {

            var output = this.requestPartiesCommand.execute(new RequestPartiesCommand.Input(new Destination(request.destination()),
                                                                                            request.partyIdType,
                                                                                            request.partyId,
                                                                                            request.subId));

            return ResponseEntity.ok(output.response());

        } catch (FspiopException e) {
            throw new RuntimeException(e);
        }

    }

    public record Request(String destination, PartyIdType partyIdType, String partyId, String subId) { }

}
