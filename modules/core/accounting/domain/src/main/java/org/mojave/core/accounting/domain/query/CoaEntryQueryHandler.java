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

package org.mojave.core.accounting.domain.query;

import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.core.accounting.contract.query.CoaEntryQuery;
import org.mojave.core.accounting.domain.model.CoaEntry;
import org.mojave.core.accounting.domain.repository.CoaEntryRepository;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
import org.mojave.scheme.rule.identifier.accounting.CoaId;
import org.mojave.component.jpa.routing.annotation.Read;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CoaEntryQueryHandler implements CoaEntryQuery {

    private final CoaEntryRepository coaEntryRepository;

    public CoaEntryQueryHandler(final CoaEntryRepository coaEntryRepository) {

        Objects.requireNonNull(coaEntryRepository);

        this.coaEntryRepository = coaEntryRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public CoaEntryData get(final CoaEntryId coaEntryId) throws CoaEntryIdNotFoundException {

        return this.coaEntryRepository
                   .findById(coaEntryId)
                   .orElseThrow(() -> new CoaEntryIdNotFoundException(coaEntryId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<CoaEntryData> get(final CoaId coaId) {

        return this.coaEntryRepository
                   .findAll(CoaEntryRepository.Filters.withCoaId(coaId))
                   .stream()
                   .map(CoaEntry::convert)
                   .toList();
    }

    @Override
    public List<CoaEntryData> get(String category) {

        return this.coaEntryRepository
                   .findAll(CoaEntryRepository.Filters.withCategory(category))
                   .stream()
                   .map(CoaEntry::convert)
                   .toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<CoaEntryData> getAll() {

        return this.coaEntryRepository.findAll().stream().map(CoaEntry::convert).toList();
    }

}
