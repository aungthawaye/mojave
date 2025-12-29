/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.participant.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.identifier.participant.OracleId;
import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import org.mojave.core.participant.contract.exception.oracle.OracleTypeNotFoundException;
import org.mojave.core.participant.contract.query.OracleQuery;
import org.mojave.core.participant.domain.model.oracle.Oracle;
import org.mojave.core.participant.domain.repository.OracleRepository;
import org.mojave.scheme.fspiop.core.PartyIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Primary
public class OracleQueryHandler implements OracleQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryHandler.class);

    private final OracleRepository oracleRepository;

    public OracleQueryHandler(OracleRepository oracleRepository) {

        assert oracleRepository != null;

        this.oracleRepository = oracleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public Optional<OracleData> find(PartyIdType type) {

        var activationStatus =
            OracleRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus =
            OracleRepository.Filters.withTerminationStatus(TerminationStatus.ALIVE);

        var existing = this.oracleRepository.findOne(
            activationStatus.and(terminationStatus).and(OracleRepository.Filters.withType(type)));

        if (existing.isEmpty()) {
            LOGGER.debug("No oracle found for type: {}", type);
            return Optional.empty();
        }

        return Optional.of(existing.get().convert());
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public OracleData get(PartyIdType type) {

        var activationStatus =
            OracleRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus =
            OracleRepository.Filters.withTerminationStatus(TerminationStatus.ALIVE);

        return this.oracleRepository
                   .findOne(activationStatus
                                .and(terminationStatus)
                                .and(OracleRepository.Filters.withType(type)))
                   .orElseThrow(() -> new OracleTypeNotFoundException(type))
                   .convert();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public OracleData get(OracleId oracleId) {

        var activationStatus =
            OracleRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus =
            OracleRepository.Filters.withTerminationStatus(TerminationStatus.ALIVE);

        return this.oracleRepository
                   .findOne(activationStatus
                                .and(terminationStatus)
                                .and(OracleRepository.Filters.withId(oracleId)))
                   .orElseThrow(() -> new OracleIdNotFoundException(oracleId))
                   .convert();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public List<OracleData> getAll() {

        var activationStatus =
            OracleRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus =
            OracleRepository.Filters.withTerminationStatus(TerminationStatus.ALIVE);

        return this.oracleRepository.findAll(activationStatus.and(terminationStatus))
                   .stream()
                   .map(Oracle::convert)
                   .toList();
    }

}
