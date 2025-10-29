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

package io.mojaloop.core.participant.domain.query;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleTypeNotFoundException;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import io.mojaloop.core.participant.domain.model.oracle.Oracle;
import io.mojaloop.core.participant.domain.repository.OracleRepository;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OracleQueryHandler implements OracleQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryHandler.class);

    private final OracleRepository oracleRepository;

    public OracleQueryHandler(OracleRepository oracleRepository) {

        assert oracleRepository != null;

        this.oracleRepository = oracleRepository;
    }

    @Override
    public Optional<OracleData> find(PartyIdType type) {

        var existing = this.oracleRepository.findOne(OracleRepository.Filters.withType(type));

        if (existing.isEmpty()) {
            LOGGER.debug("No oracle found for type: {}", type);
            return Optional.empty();
        }

        return Optional.of(existing.get().convert());
    }

    @Override
    public OracleData get(PartyIdType type) {

        return this.oracleRepository.findOne(OracleRepository.Filters.withType(type)).orElseThrow(() -> new OracleTypeNotFoundException(type)).convert();
    }

    @Override
    public OracleData get(OracleId oracleId) {

        return this.oracleRepository.findById(oracleId).orElseThrow(() -> new OracleIdNotFoundException(oracleId)).convert();
    }

    @Override
    public List<OracleData> getAll() {

        return this.oracleRepository.findAll().stream().map(Oracle::convert).toList();
    }

}
