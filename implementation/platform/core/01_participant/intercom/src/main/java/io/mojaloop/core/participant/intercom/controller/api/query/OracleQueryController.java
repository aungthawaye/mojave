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

package io.mojaloop.core.participant.intercom.controller.api.query;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OracleQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryController.class.getName());

    private final OracleQuery oracleQuery;

    public OracleQueryController(OracleQuery oracleQuery) {

        assert oracleQuery != null;
        this.oracleQuery = oracleQuery;
    }

    @GetMapping("/oracles/get-by-party-id-type")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public OracleData get(PartyIdType type) {

        return this.oracleQuery.get(type);
    }

    @GetMapping("/oracles/get-by-oracle-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public OracleData get(OracleId oracleId) {

        return this.oracleQuery.get(oracleId);
    }

    @GetMapping("/oracles/get-all-oracles")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<OracleData> getAll() {

        return this.oracleQuery.getAll();
    }

}
