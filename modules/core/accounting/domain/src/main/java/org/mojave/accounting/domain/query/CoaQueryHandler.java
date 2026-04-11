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

package org.mojave.accounting.domain.query;

import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.accounting.contract.data.CoaData;
import org.mojave.accounting.contract.exception.chart.CoaIdNotFoundException;
import org.mojave.accounting.contract.query.CoaQuery;
import org.mojave.accounting.domain.model.Coa;
import org.mojave.accounting.domain.repository.CoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CoaQueryHandler implements CoaQuery {

    private final CoaRepository coaRepository;

    public CoaQueryHandler(final CoaRepository coaRepository) {

        Objects.requireNonNull(coaRepository);

        this.coaRepository = coaRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public CoaData get(final CoaId coaId) throws CoaIdNotFoundException {

        return this.coaRepository
                   .findById(coaId)
                   .orElseThrow(() -> new CoaIdNotFoundException(coaId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<CoaData> getAll() {

        return this.coaRepository.findAll().stream().map(Coa::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<CoaData> getByNameContains(final String name) {

        return this.coaRepository
                   .findAll(CoaRepository.Filters.withNameContains(name))
                   .stream()
                   .map(Coa::convert)
                   .toList();
    }

}
