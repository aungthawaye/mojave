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
import org.mojave.scheme.common.datatype.enums.ActivationStatus;
import org.mojave.scheme.common.datatype.enums.TerminationStatus;
import org.mojave.scheme.common.datatype.identifier.participant.SspId;
import org.mojave.scheme.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.exception.ssp.SspCodeNotFoundException;
import org.mojave.core.participant.contract.exception.ssp.SspIdNotFoundException;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.core.participant.domain.model.ssp.Ssp;
import org.mojave.core.participant.domain.repository.SspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Primary
public class SspQueryHandler implements SspQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(SspQueryHandler.class);

    private final SspRepository sspRepository;

    public SspQueryHandler(final SspRepository sspRepository) {

        assert sspRepository != null;

        this.sspRepository = sspRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public SspData get(final SspId sspId) {

        var activationStatus = SspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = SspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.sspRepository.findOne(activationStatus.and(terminationStatus)).orElseThrow(
            () -> new SspIdNotFoundException(sspId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public SspData get(final SspCode sspCode) {

        var activationStatus = SspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = SspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.sspRepository
                   .findOne(activationStatus
                                .and(terminationStatus)
                                .and(SspRepository.Filters.withSspCode(sspCode)))
                   .orElseThrow(() -> new SspCodeNotFoundException(sspCode))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<SspData> getAll() {

        var activationStatus = SspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = SspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.sspRepository.findAll(activationStatus.and(terminationStatus)).stream().map(
            Ssp::convert).toList();
    }
}
