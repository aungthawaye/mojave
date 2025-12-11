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
package org.mojave.platform.core.transfer.service.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import org.mojave.component.misc.spring.event.EventPublisher;
import org.mojave.component.web.request.CachedServletRequest;
import org.mojave.core.common.datatype.identifier.transfer.UdfTransferId;
import org.mojave.core.transfer.contract.command.GetTransfersCommand;
import org.mojave.core.transfer.contract.command.PostTransfersCommand;
import org.mojave.core.transfer.contract.command.PutTransfersCommand;
import org.mojave.core.transfer.contract.command.PutTransfersErrorCommand;
import org.mojave.fspiop.service.component.FspiopHttpRequest;
import org.mojave.fspiop.spec.core.ErrorInformationObject;
import org.mojave.fspiop.spec.core.TransfersIDPutResponse;
import org.mojave.fspiop.spec.core.TransfersPostRequest;
import org.mojave.platform.core.transfer.service.controller.event.GetTransfersEvent;
import org.mojave.platform.core.transfer.service.controller.event.PostTransfersEvent;
import org.mojave.platform.core.transfer.service.controller.event.PutTransfersErrorEvent;
import org.mojave.platform.core.transfer.service.controller.event.PutTransfersEvent;
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
public class TransfersController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransfersController.class);

    private final EventPublisher eventPublisher;

    public TransfersController(EventPublisher eventPublisher) {

        assert eventPublisher != null;

        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/transfers/{transferId}")
    public ResponseEntity<?> getTransfers(@PathVariable UdfTransferId transferId,
                                          HttpServletRequest request) throws IOException {

        LOGGER.info("Received GET /transfers/{}", transferId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new GetTransfersEvent(
            new GetTransfersCommand.Input(fspiopHttpRequest, transferId));

        LOGGER.info("Publishing GetTransfersEvent : ({})", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published GetTransfersEvent : ({})", event);

        return ResponseEntity.accepted().build();
    }

    @PostMapping("/transfers")
    public ResponseEntity<?> postTransfers(@RequestBody TransfersPostRequest requestBody,
                                           HttpServletRequest request) throws IOException {

        LOGGER.info("Received POST /transfers");

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PostTransfersEvent(
            new PostTransfersCommand.Input(fspiopHttpRequest, requestBody));

        LOGGER.info("Publishing PostTransfersEvent : ({})", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PostTransfersEvent : ({})", event);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/transfers/{transferId}")
    public ResponseEntity<?> putTransfers(@PathVariable UdfTransferId transferId,
                                          @RequestBody TransfersIDPutResponse response,
                                          HttpServletRequest request) throws IOException {

        LOGGER.info("Received PUT /transfers/{}", transferId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PutTransfersEvent(
            new PutTransfersCommand.Input(fspiopHttpRequest, transferId, response));

        LOGGER.info("Publishing PutTransfersEvent : ({})", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PutTransfersEvent : ({})", event);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/transfers/{transferId}/error")
    public ResponseEntity<?> putTransfersError(@PathVariable UdfTransferId transferId,
                                               @RequestBody ErrorInformationObject error,
                                               HttpServletRequest request) throws IOException {

        LOGGER.info("Received PUT /transfers/{}/error", transferId);

        var cachedBodyRequest = new CachedServletRequest(request);
        var fspiopHttpRequest = FspiopHttpRequest.with(cachedBodyRequest);

        var event = new PutTransfersErrorEvent(
            new PutTransfersErrorCommand.Input(fspiopHttpRequest, transferId, error));

        LOGGER.info("Publishing PutTransfersErrorEvent : ({})", event);
        this.eventPublisher.publish(event);
        LOGGER.info("Published PutTransfersErrorEvent : ({})", event);

        return ResponseEntity.accepted().build();
    }

}
