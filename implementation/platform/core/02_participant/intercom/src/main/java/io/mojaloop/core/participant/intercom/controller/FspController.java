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

package io.mojaloop.core.participant.intercom.controller;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.contract.query.FspQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class FspController {

    private static final Logger LOGGER = Logger.getLogger(FspController.class.getName());

    private final FspQuery fspQuery;

    public FspController(FspQuery fspQuery) {

        assert fspQuery != null;

        this.fspQuery = fspQuery;
    }

    @GetMapping("/fsps/{fspId}")
    public ResponseEntity<FspData> getFspById(@PathVariable Long fspId) throws FspIdNotFoundException {

        return ResponseEntity.ok(this.fspQuery.get(new FspId(fspId)));
    }

    @GetMapping("/fsps")
    @ResponseStatus(HttpStatus.OK)
    public List<FspData> getFsps() {

        return this.fspQuery.getAll();
    }

}
