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

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeNotFoundException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.contract.query.FspQuery;
import io.mojaloop.core.participant.domain.model.fsp.Fsp;
import io.mojaloop.core.participant.domain.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FspQueryHandler implements FspQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspQueryHandler.class);

    private final FspRepository fspRepository;

    public FspQueryHandler(FspRepository fspRepository) {

        assert fspRepository != null;

        this.fspRepository = fspRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public FspData get(FspId fspId) {

        return this.fspRepository.findById(fspId).orElseThrow(() -> new FspIdNotFoundException(fspId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public FspData get(FspCode fspCode) {

        return this.fspRepository.findOne(FspRepository.Filters.withFspCode(fspCode)).orElseThrow(() -> new FspCodeNotFoundException(fspCode)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<FspData> getAll() {

        return this.fspRepository.findAll().stream().map(Fsp::convert).toList();
    }

}
