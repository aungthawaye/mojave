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
package io.mojaloop.core.accounting.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.ChartEntryQuery;
import io.mojaloop.core.accounting.domain.model.ChartEntry;
import io.mojaloop.core.accounting.domain.repository.ChartEntryRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
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
    public ChartEntryData get(final ChartEntryId chartEntryId) throws ChartEntryIdNotFoundException {

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

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartEntryData> getAll() {

        return this.chartEntryRepository.findAll().stream().map(ChartEntry::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartEntryData> getByNameContains(final String name) {

        return this.chartEntryRepository
                   .findAll(ChartEntryRepository.Filters.withNameContains(name))
                   .stream()
                   .map(ChartEntry::convert)
                   .toList();
    }

}
