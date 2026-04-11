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

import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.participant.contract.data.FspGroupData;
import org.mojave.core.participant.contract.exception.fsp.FspGroupIdNotFoundException;
import org.mojave.core.participant.contract.query.FspGroupQuery;
import org.mojave.core.participant.domain.model.fsp.FspGroup;
import org.mojave.core.participant.domain.repository.FspGroupRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Primary
public class FspGroupQueryHandler implements FspGroupQuery {

    private final FspGroupRepository fspGroupRepository;

    public FspGroupQueryHandler(final FspGroupRepository fspGroupRepository) {

        Objects.requireNonNull(fspGroupRepository);

        this.fspGroupRepository = fspGroupRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public FspGroupData get(final FspGroupId fspGroupId) {

        var withId = FspGroupRepository.Filters.withId(fspGroupId);

        return this.fspGroupRepository
                   .findOne(withId)
                   .orElseThrow(() -> new FspGroupIdNotFoundException(fspGroupId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<FspGroupData> getAll() {

        return this.fspGroupRepository
                   .findAll()
                   .stream()
                   .map(FspGroup::convert)
                   .toList();
    }

}
