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

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.exception.fsp.FspCodeNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.contract.query.FspQuery;
import org.mojave.core.participant.domain.model.fsp.Fsp;
import org.mojave.core.participant.domain.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Primary
public class FspQueryHandler implements FspQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspQueryHandler.class);

    private final FspRepository fspRepository;

    public FspQueryHandler(FspRepository fspRepository) {

        Objects.requireNonNull(fspRepository);

        this.fspRepository = fspRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public FspData get(FspId fspId) {

        var activationStatus = FspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = FspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.fspRepository
                   .findOne(activationStatus.and(terminationStatus))
                   .orElseThrow(() -> new FspIdNotFoundException(fspId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public FspData get(FspCode fspCode) {

        var activationStatus = FspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = FspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.fspRepository
                   .findOne(activationStatus
                                .and(terminationStatus)
                                .and(FspRepository.Filters.withFspCode(fspCode)))
                   .orElseThrow(() -> new FspCodeNotFoundException(fspCode))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<FspData> getAll() {

        var activationStatus = FspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = FspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.fspRepository
                   .findAll(activationStatus.and(terminationStatus))
                   .stream()
                   .map(Fsp::convert)
                   .toList();
    }

}
