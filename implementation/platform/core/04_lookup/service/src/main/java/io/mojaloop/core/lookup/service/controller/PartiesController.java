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

package io.mojaloop.core.lookup.service.controller;

import io.mojaloop.component.misc.spring.event.EventPublisher;
import io.mojaloop.component.web.request.CachedServletRequest;
import io.mojaloop.core.lookup.contract.command.GetPartiesCommand;
import io.mojaloop.core.lookup.contract.command.PutPartiesCommand;
import io.mojaloop.core.lookup.contract.command.PutPartiesErrorCommand;
import io.mojaloop.core.lookup.service.event.GetPartiesEvent;
import io.mojaloop.core.lookup.service.event.PutPartiesErrorEvent;
import io.mojaloop.core.lookup.service.event.PutPartiesEvent;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import io.mojaloop.fspiop.spec.core.PartiesTypeIDPutResponse;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PartiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartiesController.class);

    private final EventPublisher eventPublisher;

    public PartiesController(EventPublisher eventPublisher) {

        assert eventPublisher != null;

        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/parties/{partyIdType}/{partyId}")
    public ResponseEntity<?> getParties(@PathVariable PartyIdType partyIdType, @PathVariable String partyId, HttpServletRequest request) throws IOException {

        LOGGER.info("Received GET /parties/{}/{}", partyIdType, partyId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new GetPartiesEvent(new GetPartiesCommand.Input(fspiopHttpRequest, partyIdType, partyId, null));

        LOGGER.info("Publishing GetPartiesEvent : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published GetPartiesEvent : [{}]", event);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/parties/{partyIdType}/{partyId}/{subId}")
    public ResponseEntity<?> getPartiesWithSubId(@PathVariable PartyIdType partyIdType, @PathVariable String partyId, @PathVariable String subId, HttpServletRequest request)
        throws IOException {

        LOGGER.info("Received GET /parties/{}/{}/{}", partyIdType, partyId, subId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new GetPartiesEvent(new GetPartiesCommand.Input(fspiopHttpRequest, partyIdType, partyId, subId));

        LOGGER.info("Publishing GetPartiesEvent (subId) : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published GetPartiesEvent (subId) : [{}]", event);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}")
    public ResponseEntity<?> putParties(@PathVariable PartyIdType partyIdType,
                                        @PathVariable String partyId,
                                        @RequestBody PartiesTypeIDPutResponse response,
                                        HttpServletRequest request) throws IOException {

        LOGGER.info("Received PUT /parties/{}/{}", partyIdType, partyId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PutPartiesEvent(new PutPartiesCommand.Input(fspiopHttpRequest, partyIdType, partyId, null, response));

        LOGGER.info("Publishing PutPartiesEvent : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PutPartiesEvent : [{}]", event);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/error")
    public ResponseEntity<?> putPartiesError(@PathVariable PartyIdType partyIdType,
                                             @PathVariable String partyId,
                                             @RequestBody ErrorInformationObject error,
                                             HttpServletRequest request) throws IOException {

        LOGGER.info("Received PUT /parties/{}/{}/error", partyIdType, partyId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PutPartiesErrorEvent(new PutPartiesErrorCommand.Input(fspiopHttpRequest, partyIdType, partyId, null, error));

        LOGGER.info("Publishing PutPartiesErrorEvent : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PutPartiesErrorEvent : [{}]", event);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/{subId}/error")
    public ResponseEntity<?> putPartiesErrorWithSubId(@PathVariable PartyIdType partyIdType,
                                                      @PathVariable String partyId,
                                                      @PathVariable String subId,
                                                      @RequestBody ErrorInformationObject error,
                                                      HttpServletRequest request) throws IOException {

        LOGGER.info("Received PUT /parties/{}/{}/{}/error", partyIdType, partyId, subId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PutPartiesErrorEvent(new PutPartiesErrorCommand.Input(fspiopHttpRequest, partyIdType, partyId, subId, error));

        LOGGER.info("Publishing PutPartiesErrorEvent (subId) : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PutPartiesErrorEvent (subId) : [{}]", event);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/parties/{partyIdType}/{partyId}/{subId}")
    public ResponseEntity<?> putPartiesWithSubId(@PathVariable PartyIdType partyIdType,
                                                 @PathVariable String partyId,
                                                 @PathVariable String subId,
                                                 @RequestBody PartiesTypeIDPutResponse response,
                                                 HttpServletRequest request) throws IOException {

        LOGGER.info("Received PUT /parties/{}/{}/{}", partyIdType, partyId, subId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PutPartiesEvent(new PutPartiesCommand.Input(fspiopHttpRequest, partyIdType, partyId, subId, response));

        LOGGER.info("Publishing PutPartiesEvent (subId) : [{}]", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PutPartiesEvent (subId) : [{}]", event);

        return ResponseEntity.accepted().build();
    }

}
