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

package org.mojave.core.accounting.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.accounting.contract.data.ChartEntryData;
import org.mojave.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import org.mojave.core.accounting.contract.query.ChartEntryQuery;
import org.mojave.core.accounting.domain.model.ChartEntry;
import org.mojave.core.accounting.domain.repository.ChartEntryRepository;
import org.mojave.core.common.datatype.enums.accounting.ChartEntryCategory;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.core.common.datatype.identifier.accounting.ChartId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChartEntryQueryHandler implements ChartEntryQuery {

    private final ChartEntryRepository chartEntryRepository;

    public ChartEntryQueryHandler(final ChartEntryRepository chartEntryRepository) {

        assert chartEntryRepository != null;

        this.chartEntryRepository = chartEntryRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public ChartEntryData get(final ChartEntryId chartEntryId)
        throws ChartEntryIdNotFoundException {

        return this.chartEntryRepository
                   .findById(chartEntryId)
                   .orElseThrow(() -> new ChartEntryIdNotFoundException(chartEntryId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartEntryData> get(final ChartId chartId) {

        return this.chartEntryRepository
                   .findAll(ChartEntryRepository.Filters.withChartId(chartId))
                   .stream()
                   .map(ChartEntry::convert)
                   .toList();
    }

    @Override
    public List<ChartEntryData> get(ChartEntryCategory category) {

        return this.chartEntryRepository
                   .findAll(ChartEntryRepository.Filters.withCategory(category))
                   .stream()
                   .map(ChartEntry::convert)
                   .toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartEntryData> get(final String name) {

        return this.chartEntryRepository
                   .findAll(ChartEntryRepository.Filters.withNameContains(name))
                   .stream()
                   .map(ChartEntry::convert)
                   .toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartEntryData> getAll() {

        return this.chartEntryRepository.findAll().stream().map(ChartEntry::convert).toList();
    }

}
