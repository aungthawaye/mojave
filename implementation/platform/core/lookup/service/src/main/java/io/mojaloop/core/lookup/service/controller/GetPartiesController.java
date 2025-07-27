/*-
 * ================================================================================
 * Mojaloop OSS
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

import io.mojaloop.common.component.http.CachedBodyHttpServletRequest;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class GetPartiesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPartiesController.class);

    @GetMapping("/parties/{partyIdType}/{partyId}")
    public ResponseEntity<?> getParties(@PathVariable PartyIdType partyIdType, @PathVariable String partyId, HttpServletRequest request) {

        LOGGER.info("Received GET /parties/{}/{}", partyIdType, partyId);

        var cachedBodyRequest = (CachedBodyHttpServletRequest) request;

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/parties/{partyIdType}/{partyId}/{subId}")
    public ResponseEntity<?> getPartiesWithSubId(@PathVariable PartyIdType partyIdType,
                                                 @PathVariable String partyId,
                                                 @PathVariable String subId,
                                                 HttpServletRequest request) throws IOException {

        LOGGER.info("Received GET /parties/{}/{}/{}", partyIdType, partyId, subId);

        var cachedBodyRequest = new CachedBodyHttpServletRequest( request);
        cachedBodyRequest.getCachedBodyAsString();

        return ResponseEntity.accepted().build();
    }

}
