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

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.accounting.contract.data.ChartData;
import org.mojave.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import org.mojave.core.accounting.contract.query.ChartQuery;
import org.mojave.core.accounting.domain.model.Chart;
import org.mojave.core.accounting.domain.repository.ChartRepository;
import org.mojave.scheme.common.datatype.identifier.accounting.ChartId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChartQueryHandler implements ChartQuery {

    private final ChartRepository chartRepository;

    public ChartQueryHandler(final ChartRepository chartRepository) {

        assert chartRepository != null;

        this.chartRepository = chartRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public ChartData get(final ChartId chartId) throws ChartIdNotFoundException {

        return this.chartRepository
                   .findById(chartId)
                   .orElseThrow(() -> new ChartIdNotFoundException(chartId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartData> getAll() {

        return this.chartRepository.findAll().stream().map(Chart::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<ChartData> getByNameContains(final String name) {

        return this.chartRepository
                   .findAll(ChartRepository.Filters.withNameContains(name))
                   .stream()
                   .map(Chart::convert)
                   .toList();
    }

}
